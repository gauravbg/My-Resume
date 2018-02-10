package com.gauravbg.myresume;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.gauravbg.myresume.firebase.ProfileReader;
import com.gauravbg.myresume.firebase.ProfileWriter;
import com.gauravbg.myresume.utils.MyProfileEntityCreator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.w3c.dom.Text;

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
    private Map<String, MyResumeEntity> allEntities;

    private static final int MY_PERMISSIONS_REQUEST_CALL = 101;
    private static final int MY_PERMISSIONS_REQUEST_MSG = 301;
    private static final int PICK_IMAGE = 201;


    public static String UID = "USER_ID";
    public static String IS_MY_PROFILE = "MY_PROFILE";

    private boolean isEditMode = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;

    private ProgressDialog progressDialog;

    private Profile profile;

    private CircleImageView profileIV;
    private AlertDialog photoPickerDialog;
    private AlertDialog pageRenameDialog;
    private AlertDialog saveConfirmationDialog;
    private AlertDialog cancelConfirmationDialog;

    private Menu mMenu;
    private boolean isLoading;
    private PagesFragmentAdapter adapter;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

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
            invalidateOptionsMenu();
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
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        PROFILE_ID = getIntent().getStringExtra(UID);
        isMyProfile = getIntent().getBooleanExtra(IS_MY_PROFILE, false);

        findViewById(R.id.full_content_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);

        isLoading = true;
        profileReader.fetchProfile(PROFILE_ID);
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
//                    Intent intent = new Intent(MainActivity.this, IntroActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    startActivity(intent);
                }
            }
        };

    }

    private void refreshUI(boolean saveAndRefresh) {
        invalidateOptionsMenu();

        if(saveAndRefresh) {
            List<MyResumeEntity> entities = new ArrayList<>();
            for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
                entities.add(entry.getValue());
            }
            isLoading = true;
            profileWriter.writeProfile(entities, PROFILE_ID);

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
                        findViewById(R.id.full_content_layout).setVisibility(View.GONE);
                        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.loading_msg)).setText("Saving Data...");
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
                AlertDialog.Builder cancelDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                cancelDialogBuilder.setTitle("Discard Changes");
                cancelDialogBuilder.setMessage("Are you sure you want to discard all changes?");
                cancelDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchEditMode(false);
                        findViewById(R.id.full_content_layout).setVisibility(View.GONE);
                        findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
                    if(page.getPageNumber() == pos) {
                        showRenameDialog(page);
                    }
                }
            }
            return false;
        }
    }

    private void setLongClickListenerOnTabs() {
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

        if(isEditMode) {

            this.isEditMode = true;

            name_et.setEnabled(true);
            title_et.setEnabled(true);
            profileIV.setClickable(true);
            getSupportActionBar().setTitle("Edit Mode");
            mMenu.findItem(R.id.save).setVisible(true);
            mMenu.findItem(R.id.cancel).setVisible(true);
            mMenu.findItem(R.id.add_page).setVisible(true);

        } else {

            this.isEditMode = false;

            name_et.setEnabled(false);
            title_et.setEnabled(false);
            profileIV.setClickable(false);
            getSupportActionBar().setTitle("My Resume");
            mMenu.findItem(R.id.save).setVisible(false);
            mMenu.findItem(R.id.cancel).setVisible(false);
            mMenu.findItem(R.id.add_page).setVisible(false);


        }

        setLongClickListenerOnTabs();
        adapter.setIsEditMode(isEditMode);
        adapter.notifyDataSetChanged();
    }


    private void openGallery() {
        photoPickerDialog.dismiss();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isLoading) {
            return false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showProfile() {

        findViewById(R.id.full_content_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_layout).setVisibility(View.GONE);
        isLoading = false;
        invalidateOptionsMenu();

        EditText name_et = (EditText) findViewById(R.id.name_et);
        EditText title_et = (EditText) findViewById(R.id.title_et);
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
                        profileIV.setBackground(getResources().getDrawable(R.mipmap.ic_account_circle_white_48dp));
                        photoPickerDialog.dismiss();
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
