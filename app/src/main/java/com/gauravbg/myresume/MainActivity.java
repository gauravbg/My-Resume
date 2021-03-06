package com.gauravbg.myresume;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.entities.Section;
import com.gauravbg.myresume.firebase.FirebaseManager;
import com.gauravbg.myresume.firebase.ProfileReader;
import com.gauravbg.myresume.firebase.ProfileWriter;
import com.gauravbg.myresume.utils.MyProfileEntityCreator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity{

    private String PROFILE_ID = "-L1WI0X02u4rIV1xltPV";
    private boolean isMyProfile = false;
    private final String LOG = getClass().getCanonicalName();
    private Map<String, MyResumeEntity> allEntities = new HashMap<>();

    private static final int MY_PERMISSIONS_REQUEST_CALL = 101;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 501;
    private static final int MY_PERMISSIONS_REQUEST_MSG = 301;
    private static final int PICK_IMAGE = 201;
    private static final int PICK_PDF_FILE = 401;


    public static String UID = "USER_ID";
    public static String IS_MY_PROFILE = "MY_PROFILE";
    public static String IS_EDIT_MODE_KEY = "IS_EDIT_MODE_KEY";
    public static String ALL_ENTITIES_VALUE_KEY = "ALL_ENTITIES_VALUE_KEY";
    public static String ALL_ENTITIES_KEY_KEY = "ALL_ENTITIES_KEY_KEY";

    private boolean isEditMode = false;
    private boolean isPhotoChanged = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;

    private ProgressDialog progressDialog;

    private Profile profile;

    private CircleImageView profileIV;
    private AlertDialog photoPickerDialog;
    private AlertDialog pdfUploadDialog;
    private AlertDialog pageRenameDialog;
    private AlertDialog saveConfirmationDialog;
    private AlertDialog cancelConfirmationDialog;

    private Menu mMenu;
    private boolean isLoading;
    private PagesFragmentAdapter adapter;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

    public boolean isRunning = false;

    ProfileReader profileReader = new ProfileReader(new ProfileReader.EntityFetchListener() {
        @Override
        public void onEntityFetched(Map<String, MyResumeEntity> entities) {
            allEntities = entities;
            showProfile();
        }

        @Override
        public void onEntityFetchFailed(DatabaseError databaseError) {
            Log.e(LOG, "profile not read!:" + databaseError.getMessage());
            isLoading = false;
        }
    });


    ProfileWriter profileWriter = new ProfileWriter(new ProfileWriter.EntitySaveListener() {

        @Override
        public void onEntitySaved(DatabaseReference databaseReference) {
            Toast.makeText(MainActivity.this, "Profile Saved", Toast.LENGTH_LONG).show();
            allEntities.clear();
            findViewById(R.id.full_content_layout).setVisibility(View.GONE);
            findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.loading_msg)).setText(getResources().getString(R.string.fetching_profile));
            profileReader.fetchProfile(PROFILE_ID);
        }

        @Override
        public void onEntitySaveFailed(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, "Profile save failed", Toast.LENGTH_LONG).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        PROFILE_ID = getIntent().getStringExtra(UID);
        isMyProfile = getIntent().getBooleanExtra(IS_MY_PROFILE, false);

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if(progressDialog!= null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } else {
                    if(progressDialog!= null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    finish();
                }
            }
        };
        if(savedInstanceState != null) {
            isEditMode = savedInstanceState.getBoolean(IS_EDIT_MODE_KEY);
            ArrayList<MyResumeEntity> entities = savedInstanceState.getParcelableArrayList(ALL_ENTITIES_VALUE_KEY);
            ArrayList<String> keys = savedInstanceState.getStringArrayList(ALL_ENTITIES_KEY_KEY);
            allEntities.clear();
            for (int i=0; i<keys.size(); i++) {
                allEntities.put(keys.get(i), entities.get(i));
            }
            showProfile();
            if(isEditMode) {
                switchEditMode(true);
            } else {
                switchEditMode(false);
            }

        } else {
            refreshUI(false);
        }
    }

    private void refreshUI(boolean saveAndRefresh) {
        invalidateOptionsMenu();
        findViewById(R.id.full_content_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
        isLoading = true;

        if(saveAndRefresh) {
            ((TextView) findViewById(R.id.loading_msg)).setText("Saving Data...");
            final List<MyResumeEntity> entities = new ArrayList<>();
            for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
                if(entry.getValue().getEntityType() == MyResumeEntity.PROFILE_TYPE) {
                    entities.add(0, entry.getValue());
                } else {
                    entities.add(entry.getValue());
                }
            }
            if(isPhotoChanged) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference storageRef = storage.getReference();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

                if(profileIV.getTag() != null) {
                    UploadTask uploadTask = storageRef.child("Profile_Images/"+ PROFILE_ID + "_" + System.currentTimeMillis() + ".jpg").putFile((Uri)profileIV.getTag(), metadata);

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
                            Toast.makeText(MainActivity.this, "Image save failed", Toast.LENGTH_LONG).show();
                            profileWriter.writeProfile(entities, PROFILE_ID);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Profile prof = (Profile) entities.get(0);
                            prof.setImageUrl(downloadUrl.toString());
                            profileWriter.writeProfile(entities, PROFILE_ID);
                        }
                    });
                } else {
                    Profile prof = (Profile) entities.get(0);
                    prof.setImageUrl(null);
                    profileWriter.writeProfile(entities, PROFILE_ID);
                }

            } else {
                profileWriter.writeProfile(entities, PROFILE_ID);
            }

        } else {
            isLoading = true;
            profileReader.fetchProfile(PROFILE_ID);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isMyProfile) {
            mAuth.addAuthStateListener(stateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        mMenu = menu;
        if(!isMyProfile) {
            mMenu.findItem(R.id.edit).setVisible(false);
            mMenu.findItem(R.id.sign_out).setVisible(false);
            mMenu.findItem(R.id.upload_pdf).setVisible(false);
        }
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                profileIV.setImageURI(data.getData());
                profileIV.setTag(data.getData());
                isPhotoChanged = true;
            } else {

            }
        } else if(requestCode == PICK_PDF_FILE) {
            if (resultCode == RESULT_OK) {
                final Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = MainActivity.this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                final View view = getLayoutInflater().inflate(R.layout.upload_dialog_layout, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final FancyButton uploadBtn = (FancyButton) view.findViewById(R.id.upload_button);
                final FancyButton cancelBtn = (FancyButton) view.findViewById(R.id.cancel_button);
                final LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
                TextView filenameTxt = (TextView) view.findViewById(R.id.pdf_name_txt);
                filenameTxt.setText(displayName);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference storageRef = storage.getReference();
                final StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("application/pdf")
                        .build();

                uploadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadBtn.setEnabled(false);
                        cancelBtn.setEnabled(false);
                        loadingLayout.setVisibility(View.VISIBLE);

                        UploadTask uploadTask = storageRef.child("Resume_PDFs/"+ PROFILE_ID + "_" + System.currentTimeMillis() + ".pdf").putFile(uri, metadata);

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
                                Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                                uploadBtn.setEnabled(true);
                                cancelBtn.setEnabled(true);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                DatabaseReference profileRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PROFILES);
                                profileRef.child(PROFILE_ID).child("pdfUrl").setValue(downloadUrl.toString());
                                profile.setPdfUrl(downloadUrl.toString());
                                uploadBtn.setEnabled(true);
                                cancelBtn.setEnabled(true);
                                pdfUploadDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pdfUploadDialog.dismiss();
                    }
                });
                dialogBuilder.setView(view);
                pdfUploadDialog = dialogBuilder.create();
                pdfUploadDialog.show();
            } else {

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_my_profile:
                profileWriter.writeProfile(MyProfileEntityCreator.getMyProfileEntities(), PROFILE_ID);
                return true;
            case R.id.sign_out:
                progressDialog = ProgressDialog.show(MainActivity.this, "Signing Out", "Please wait...", true, false);
                mAuth.signOut();
                return true;
            case R.id.edit:
                switchEditMode(true);
                return true;
            case R.id.upload_pdf:
                handlePdfUpload();
                return true;
            case R.id.download_pdf:
                handlePdfDownload();
                return true;
            case R.id.add_page:
                Page defaultPage = getDefaultPage();
                allEntities.put(defaultPage.getId(), defaultPage);
                adapter.setData(allEntities);
                adapter.notifyDataSetChanged();
                viewPagerTab.setViewPager(viewPager);
                setLongClickListenerOnTabs();
                return true;
            case R.id.save:

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("Save Changes");
                dialogBuilder.setMessage("Are you sure you want to save all changes?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchEditMode(false);
                        refreshUI(true);
                        if(saveConfirmationDialog != null && saveConfirmationDialog.isShowing()) {
                            saveConfirmationDialog.dismiss();
                        }

                    }
                });

                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(saveConfirmationDialog != null && saveConfirmationDialog.isShowing()) {
                            saveConfirmationDialog.dismiss();
                        }

                    }
                });
                saveConfirmationDialog = dialogBuilder.create();
                saveConfirmationDialog.show();

                return true;



            case R.id.cancel:
                showCancelDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void handlePdfDownload() {
        if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "The app needs permission to download pdf", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE);
            }

        } else {
            if(profile.getPdfUrl() != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference pdfRef = storage.getReferenceFromUrl(profile.getPdfUrl());

                File rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                File localFile = new File(rootPath,profile.getName() + ".pdf");
                Toast.makeText(MainActivity.this, "Download in progress...", Toast.LENGTH_LONG).show();
                pdfRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Download Completed. Check Downloads Folder", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "No PDF available for download. Please Upload your PDF.", Toast.LENGTH_LONG).show();
            }
        }



    }

    private void handlePdfUpload() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    private void showCancelDialog() {
        AlertDialog.Builder cancelDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        cancelDialogBuilder.setTitle("Discard Changes");
        cancelDialogBuilder.setMessage("Are you sure you want to discard all changes?");
        cancelDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchEditMode(false);
                refreshUI(false);
                if(cancelConfirmationDialog != null && cancelConfirmationDialog.isShowing()) {
                    cancelConfirmationDialog.dismiss();
                }

            }
        });

        cancelDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(cancelConfirmationDialog != null && cancelConfirmationDialog.isShowing()) {
                    cancelConfirmationDialog.dismiss();
                }

            }
        });
        cancelConfirmationDialog = cancelDialogBuilder.create();
        cancelConfirmationDialog.show();

    }

    @Override
    public void onBackPressed() {
        if(isLoading) {
          //do nothing
        } else if(isEditMode) {
            showCancelDialog();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        if(isEditMode) {
            refreshUI(false);
        }
        super.onDestroy();
    }

    private void changePageNumbers(boolean addedPage, int index) {
        if(addedPage) {
            for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
                if(entry.getValue().getEntityType().equals(MyResumeEntity.PAGE_TYPE)){
                    Page p = (Page) entry.getValue();
                    p.setPageNumber(p.getPageNumber() + 1);
                }
            }
        } else {
            for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
                if(entry.getValue().getEntityType().equals(MyResumeEntity.PAGE_TYPE)){
                    Page p = (Page) entry.getValue();
                    if(p.getPageNumber() > index) {
                        p.setPageNumber(p.getPageNumber() - 1);
                    }
                }
            }
        }

    }

    private Page getDefaultPage() {
        Page page = new Page();
        page.setId(String.valueOf(new Date().getTime()));
        page.setTitle("New Page");
        page.setPageNumber(0);
        Section section = new Section();
        section.setNumber(0);
        changePageNumbers(true, -1);
        Content content = new Content();
        content.setType(Content.ContentType.TEXT);
        List<Content> allContent = new ArrayList<>();
        allContent.add(content);
        section.setContents(allContent);
        List<Section> allSection = new ArrayList<>();
        allSection.add(section);
        page.setSections(allSection);
        return page;
    }

    private class MyLongClickListener implements View.OnLongClickListener {

        private int pos;
        MyLongClickListener(int position) {
           this.pos = position;
        }

        @Override
        public boolean onLongClick(View v) {
            if(pos == -1)
                return false;
            for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
                if(entry.getValue().getEntityType().equals(MyResumeEntity.PAGE_TYPE)){
                    Page page = (Page) entry.getValue();
                    if(page.getPageNumber() == pos && !page.isContactPage()) {
                        showRenameDialog(page);
                    }
                }
            }
            return false;
        }
    }

    private void setLongClickListenerOnTabs() {
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        LinearLayout tabStrip = (LinearLayout) viewPagerTab.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            final TextView tab = (TextView) tabStrip.getChildAt(i);
            if(isEditMode)
                tab.setOnLongClickListener(new MyLongClickListener(i));
            else
                tab.setOnLongClickListener(new MyLongClickListener(-1));
        }
    }

    private void switchEditMode(boolean isEditMode) {

        EditText name_et = (EditText) findViewById(R.id.name_et);
        EditText title_et = (EditText) findViewById(R.id.title_et);
        profileIV = (CircleImageView) findViewById(R.id.profile_image);

        if(isEditMode) {

            this.isEditMode = true;

            name_et.setEnabled(true);
            title_et.setEnabled(true);
            profileIV.setClickable(true);
            getSupportActionBar().setTitle("Edit Mode");

        } else {

            this.isEditMode = false;

            name_et.setEnabled(false);
            title_et.setEnabled(false);
            profileIV.setClickable(false);
            getSupportActionBar().setTitle("My Resume");


        }

        invalidateOptionsMenu();
        setLongClickListenerOnTabs();
        if(adapter != null) {
            adapter.setIsEditMode(isEditMode);
            adapter.notifyDataSetChanged();
        }
    }


    private void openGallery() {
        photoPickerDialog.dismiss();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(isEditMode) {
            menu.findItem(R.id.save).setVisible(true);
            menu.findItem(R.id.cancel).setVisible(true);
            menu.findItem(R.id.add_page).setVisible(true);
            menu.findItem(R.id.sign_out).setVisible(false);
            menu.findItem(R.id.upload_pdf).setVisible(false);
            menu.findItem(R.id.edit).setVisible(false);
        } else {
            menu.findItem(R.id.save).setVisible(false);
            menu.findItem(R.id.cancel).setVisible(false);
            menu.findItem(R.id.add_page).setVisible(false);
            menu.findItem(R.id.sign_out).setVisible(true);
            menu.findItem(R.id.upload_pdf).setVisible(true);
            menu.findItem(R.id.edit).setVisible(true);
        }

        if(isLoading) {
            menu.findItem(R.id.edit).setVisible(false);
            menu.findItem(R.id.upload_pdf).setVisible(false);
            menu.findItem(R.id.download_pdf).setVisible(false);
        } else {
            if(!isEditMode) {
                menu.findItem(R.id.upload_pdf).setVisible(true);
                menu.findItem(R.id.edit).setVisible(true);
            }
            menu.findItem(R.id.download_pdf).setVisible(true);
        }

        if(!isMyProfile) {
            menu.findItem(R.id.save).setVisible(false);
            menu.findItem(R.id.cancel).setVisible(false);
            menu.findItem(R.id.add_page).setVisible(false);
            menu.findItem(R.id.sign_out).setVisible(false);
            menu.findItem(R.id.upload_pdf).setVisible(false);
            menu.findItem(R.id.edit).setVisible(false);
            menu.findItem(R.id.download_pdf).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_EDIT_MODE_KEY, isEditMode);
        ArrayList<MyResumeEntity> entities = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
            keys.add(entry.getKey());
            entities.add(entry.getValue());
        }
        outState.putParcelableArrayList(ALL_ENTITIES_VALUE_KEY, entities);
        outState.putStringArrayList(ALL_ENTITIES_KEY_KEY, keys);
        super.onSaveInstanceState(outState);
    }

    private void showProfile() {

        findViewById(R.id.full_content_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_layout).setVisibility(View.GONE);
        isLoading = false;
        isPhotoChanged = false;
        invalidateOptionsMenu();

        final EditText name_et = (EditText) findViewById(R.id.name_et);
        final EditText title_et = (EditText) findViewById(R.id.title_et);
        name_et.setEnabled(false);
        title_et.setEnabled(false);

        profileIV = (CircleImageView) findViewById(R.id.profile_image);
        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.photo_picker_dialog_layout, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                        profileIV.setImageResource(R.mipmap.ic_account_circle_white_48dp);
                        photoPickerDialog.dismiss();
                        isPhotoChanged = true;
                    }
                });
                dialogBuilder.setView(view);
                photoPickerDialog = dialogBuilder.create();
                photoPickerDialog.show();
            }
        });
        profileIV.setClickable(false);

        for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
            if(entry.getValue().getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                profile = (Profile)entry.getValue();
                break;
            }
        }

        profileReader.fetchImage(this, profileIV, profile.getImageUrl());
        name_et.setText(profile.getName());
        title_et.setText(profile.getTitle());
        name_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    profile.setName(name_et.getText().toString());
                }
            }
        });

        title_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    profile.setTitle(title_et.getText().toString());
                }
            }
        });
        ImageButton call_button = (ImageButton) findViewById(R.id.call_button);
        ImageButton email_button = (ImageButton) findViewById(R.id.email_button);
        ImageButton msg_button = (ImageButton) findViewById(R.id.message_button);
        call_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(MainActivity.this, "The app needs Call Permission to make a call", Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL);
                    }

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + profile.getPhoneNumber()));
                    startActivity(callIntent);
                }


            }
        });

        email_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + profile.getEmail()));
                startActivity(emailIntent);
            }
        });

        msg_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS ) != PackageManager.PERMISSION_GRANTED ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(MainActivity.this, "The app needs Send Message Permission to send a message", Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_MSG);
                    }

                } else {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + profile.getPhoneNumber()));
                    startActivity(smsIntent);
                }

            }
        });


        adapter = new PagesFragmentAdapter(this, getSupportFragmentManager(), allEntities);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        adapter.setIsEditMode(isEditMode);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        setLongClickListenerOnTabs();

        switchEditMode(false);

    }

    private Content getDefaultContent() {
        Content content = new Content();
        content.setType(Content.ContentType.TEXT);
        return content;
    }

    private Section getDefaultSection() {
        Section section = new Section();
        List<Content> allContent = new ArrayList<>();
        allContent.add(getDefaultContent());
        return section;
    }


    private void showRenameDialog(final Page page) {

        View view = getLayoutInflater().inflate(R.layout.page_rename_dialog_layout, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final EditText pageNameET = (EditText) view.findViewById(R.id.page_name_et);
        final EditText pageNumberET = (EditText) view.findViewById(R.id.page_number_et);
        final CheckBox markDeleteCb = (CheckBox) view.findViewById(R.id.delete_cb);
        final ElegantNumberButton numberBtn = (ElegantNumberButton) view.findViewById(R.id.number_button);
        view.findViewById(R.id.delete_mark_layout).setVisibility(View.GONE);
        FancyButton saveBtn = (FancyButton) view.findViewById(R.id.save_button);
        FancyButton cancelBtn = (FancyButton) view.findViewById(R.id.cancel_button);
        pageNameET.setText(page.getTitle());
        pageNumberET.setText(String.valueOf(page.getPageNumber()));
        dialogBuilder.setView(view);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page.setTitle(pageNameET.getText().toString());
                List<Section> allSections = page.getSections();
                for (int i=0; i<Integer.valueOf(numberBtn.getNumber()); i++) {
                    allSections.add(getDefaultSection());
                }
                if(markDeleteCb.isChecked()) {
                    allEntities.remove(page.getId());
                    changePageNumbers(false, page.getPageNumber());
                }

                adapter.setData(allEntities);
                adapter.notifyDataSetChanged();
                viewPagerTab.setViewPager(viewPager);
                setLongClickListenerOnTabs();
                if(pageRenameDialog!= null && pageRenameDialog.isShowing()) {
                    pageRenameDialog.dismiss();
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageRenameDialog!= null && pageRenameDialog.isShowing()) {
                    pageRenameDialog.dismiss();
                }
            }
        });

        pageRenameDialog = dialogBuilder.create();
        pageRenameDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(profile != null) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + profile.getPhoneNumber()));
                        try {
                            startActivity(callIntent);
                        } catch (SecurityException ex) {
                            Log.e(LOG, ex.getMessage());
                        }
                    }
                } else {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_MSG: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(profile != null) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.setData(Uri.parse("sms:" + profile.getPhoneNumber()));
                        startActivity(smsIntent);
                    }
                } else {

                }
                return;
            }

        }
    }

}
