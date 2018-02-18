package com.gauravbg.myresume;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gauravbg.myresume.entities.Link;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.firebase.FirebaseManager;
import com.gauravbg.myresume.firebase.ProfileWriter;
import com.gauravbg.myresume.utils.MyProfileEntityCreator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class AccountSetupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private SignInButton signInButton;
    private Button logoutButton;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private static final int RC_SIGN_IN = 101;
    private static final int PICK_IMAGE = 201;
    private ProgressDialog progressDialog;
    private ProgressDialog accountSetupProgressDialog;


    private TextInputEditText nameET;
    private TextInputEditText usernameET;
    private TextInputEditText emailET;
    private TextInputEditText phoneET;
    private TextInputEditText addressET;
    private TextInputEditText titleET;

    private FancyButton saveButton;
    private FancyButton cancelButton;
    private ImageButton addButton;
    private CircleImageView profileImage;
    private LinearLayout addLinksLayout;
    private AlertDialog photoPickerDialog;
    private AlertDialog cancelConfirmDialog;

    private boolean isAccountSetup;


    private ProfileWriter profileWriter = new ProfileWriter(new ProfileWriter.EntitySaveListener() {

        @Override
        public void onEntitySaved(DatabaseReference databaseReference) {
            if(accountSetupProgressDialog!= null && accountSetupProgressDialog.isShowing()) {
                accountSetupProgressDialog.dismiss();
            }
            Intent intent = new Intent(AccountSetupActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.UID, databaseReference.getKey());
            intent.putExtra(MainActivity.IS_MY_PROFILE, true);
            isAccountSetup = true;
            AccountSetupActivity.this.startActivity(intent);
            AccountSetupActivity.this.finish();
        }

        @Override
        public void onEntitySaveFailed(DatabaseError databaseError) {
            accountSetupProgressDialog.dismiss();
            Toast.makeText(AccountSetupActivity.this, "Profile save failed", Toast.LENGTH_LONG).show();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mAuth = FirebaseAuth.getInstance();

        signInButton = (SignInButton) findViewById(R.id.signIn_btn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if(progressDialog!= null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    updateStateChangeUI(firebaseAuth.getCurrentUser());
                } else {
                    if(progressDialog!= null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    updateStateChangeUI(null);
                }
            }
        };


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(AccountSetupActivity.this, "Signing into account", "Please wait...", true, false);
                signIn();
            }
        });



    }

    private void updateStateChangeUI(final FirebaseUser user) {

        if (user == null) {
            findViewById(R.id.pre_login_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.post_login_layout).setVisibility(View.GONE);
            Snackbar.make(signInButton, "You are now Signed out", Snackbar.LENGTH_LONG).show();
        } else {
            DatabaseReference profileRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PROFILES);
            profileRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        showNewUserUI(user);
                    } else {
                        Intent intent = new Intent(AccountSetupActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.UID, user.getUid());
                        intent.putExtra(MainActivity.IS_MY_PROFILE, true);
                        finish();
                        startActivity(intent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }


    private void showNewUserUI(final FirebaseUser user) {
        findViewById(R.id.pre_login_layout).setVisibility(View.GONE);
        findViewById(R.id.post_login_layout).setVisibility(View.VISIBLE);
        Snackbar.make(signInButton, "New account created, setup your profile", Snackbar.LENGTH_LONG).show();


        nameET = (TextInputEditText) findViewById(R.id.name_et);
        usernameET = (TextInputEditText) findViewById(R.id.username_et);
        emailET = (TextInputEditText) findViewById(R.id.email_et);
        phoneET = (TextInputEditText) findViewById(R.id.phonenumber_et);
        addressET = (TextInputEditText) findViewById(R.id.address_et);
        titleET = (TextInputEditText) findViewById(R.id.title_et);

        saveButton = (FancyButton) findViewById(R.id.save_button);
        cancelButton = (FancyButton) findViewById(R.id.cancel_button);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        addButton = (ImageButton) findViewById(R.id.add_btn);
        addLinksLayout = (LinearLayout) findViewById(R.id.add_links_container);


        nameET.setText(user.getDisplayName());
        emailET.setText(user.getEmail());
        phoneET.setText(user.getPhoneNumber());
//        if(user.getPhotoUrl() != null)
//            profileImage.setImageURI(user.getPhotoUrl());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.photo_picker_dialog_layout, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccountSetupActivity.this);
                FancyButton setPhotoBtn = (FancyButton) view.findViewById(R.id.set_photo_btn);
                FancyButton deletePhotoBtn = (FancyButton) view.findViewById(R.id.delete_photo_btn);

                setPhotoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                    }
                });

                deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileImage.setBackground(getResources().getDrawable(R.mipmap.ic_account_circle_white_48dp));
                        profileImage.setTag(null);
                        photoPickerDialog.dismiss();
                    }
                });
                dialogBuilder.setView(view);
                photoPickerDialog = dialogBuilder.create();
                photoPickerDialog.show();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSaveProfile(user.getUid());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder cancelConfirmationDialogBuilder = new AlertDialog.Builder(AccountSetupActivity.this);
                cancelConfirmationDialogBuilder.setTitle("Abort Account Setup").setMessage("All changes will be lost. Are you sure you want to abort account setup?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                dialog.dismiss();
                                AccountSetupActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                cancelConfirmDialog = cancelConfirmationDialogBuilder.create();
                cancelConfirmDialog.show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.additional_links_layout, null);
                view.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewParent parent = v.getParent();
                        if(parent != null && parent instanceof ViewGroup) {
                            ViewParent layoutParent = parent.getParent();
                            if(layoutParent != null) {
                                addLinksLayout.removeView((RelativeLayout)layoutParent);
                            }
                        }
                    }
                });
                addLinksLayout.addView(view);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(stateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result =  Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                if(progressDialog!= null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "Sign In Failed!", Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                profileImage = (CircleImageView) findViewById(R.id.profile_image);
                profileImage.setImageURI(data.getData());
                profileImage.setTag(data.getData());
            } else {

            }
        }
    }


    private void openGallery() {
        photoPickerDialog.dismiss();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            googleApiClient.clearDefaultAccountAndReconnect();
                        } else {
                            if(progressDialog!= null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(AccountSetupActivity.this, "Sign In Failed!", Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void validateAndSaveProfile(final String uid) {
        final String name = nameET.getText().toString();
        final String username = usernameET.getText().toString();
        final String email = emailET.getText().toString();
        final String phone = phoneET.getText().toString();
        final String address = addressET.getText().toString();
        final String title = titleET.getText().toString();
        final List<Link> additionalLinks = new ArrayList<>();

        for (int i=0; i<addLinksLayout.getChildCount(); i++) {
            additionalLinks.add(getLink((RelativeLayout)addLinksLayout.getChildAt(i)));
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(title) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)
                || areLinksEmpty(additionalLinks)) {
            Snackbar.make(saveButton, "Some fields are empty!", Snackbar.LENGTH_LONG).show();
        } else {

            accountSetupProgressDialog = ProgressDialog.show(AccountSetupActivity.this, "Setting up account", "Please wait...", true, false);

            final Profile profile = new Profile();
            profile.setName(name);
            profile.setUsername(username);
            profile.setEmail(email);
            profile.setPhoneNumber(phone);
            profile.setAddress(address);
            profile.setTitle(title);
            profile.setLinks(additionalLinks);
            profile.setId(uid);
            if(profileImage.getTag() != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

                UploadTask uploadTask = storageRef.child("Profile_Images/"+ uid + "_" + System.currentTimeMillis() + ".jpg").putFile((Uri)profileImage.getTag(), metadata);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if(accountSetupProgressDialog != null && accountSetupProgressDialog.isShowing()) {
                            accountSetupProgressDialog.dismiss();
                        }
                        Toast.makeText(AccountSetupActivity.this, "Profile save failed", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        profile.setImageUrl(downloadUrl.toString());
                        List<MyResumeEntity> entities = new ArrayList<>();
                        entities.add(profile);
                        entities.add(MyProfileEntityCreator.getContactPage(profile, 0));
                        profileWriter.writeProfile(entities, uid);
                    }
                });
            } else {
                List<MyResumeEntity> entities = new ArrayList<>();
                entities.add(profile);
                entities.add(MyProfileEntityCreator.getContactPage(profile, 0));
                profileWriter.writeProfile(entities, uid);
            }




        }
    }

    private Link getLink(RelativeLayout parentView) {
        LinearLayout topLayout = (LinearLayout)parentView.getChildAt(0);
        TextInputLayout hintLayout1 = (TextInputLayout) topLayout.getChildAt(0);
        FrameLayout fl1 = (FrameLayout) hintLayout1.getChildAt(0);
        TextInputEditText nameEt = (TextInputEditText) fl1.getChildAt(0);

        LinearLayout bottomLayout = (LinearLayout)parentView.getChildAt(1);
        FrameLayout fl2 = (FrameLayout) bottomLayout.getChildAt(0);
        TextInputEditText urlEt = (TextInputEditText) fl2.getChildAt(0);


        Link link = new Link();
        link.setUrl(urlEt.getText().toString());
        link.setName(nameEt.getText().toString());

        return link;


    }

    private boolean areLinksEmpty(List<Link> links) {
        for (Link link: links) {
            if(TextUtils.isEmpty(link.getName()) || TextUtils.isEmpty(link.getUrl())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(progressDialog!= null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(this, "Oops! Connection error!", Toast.LENGTH_LONG).show();
    }
}
