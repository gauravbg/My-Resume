package com.gauravbg.myresume;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.firebase.ProfileReader;
import com.gauravbg.myresume.firebase.ProfileWriter;
import com.gauravbg.myresume.utils.MyProfileEntityCreator;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    private static final String GAURAV_PROFILE_ID = "-KvW1HBWUJOjZJxQ181t";
    private final String LOG = getClass().getCanonicalName();
    private Map<String, MyResumeEntity> allEntities;

    private static final int MY_PERMISSIONS_REQUEST_CALL = 101;
    private static final int MY_PERMISSIONS_REQUEST_MSG = 301;

    private Profile profile;

    ProfileReader profileReader = new ProfileReader(new ProfileReader.EntityFetchListener() {
        @Override
        public void onEntityFetched(Map<String, MyResumeEntity> entities) {
            allEntities = entities;
            showProfile();
        }

        @Override
        public void onEntityFetchFailed(DatabaseError databaseError) {
            Log.e(LOG, "profile not read!:" + databaseError.getMessage());
        }
    });


    ProfileWriter profileWriter = new ProfileWriter(new ProfileWriter.EntitySaveListener() {

        @Override
        public void onEntitySaved(DatabaseReference databaseReference) {
            Toast.makeText(MainActivity.this, "Profile saved in Database", Toast.LENGTH_LONG).show();
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
        profileReader.fetchProfile(GAURAV_PROFILE_ID);

        Button addProfile = (Button) findViewById(R.id.add_profile);
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProfileEntityCreator profileEntityCreator = new MyProfileEntityCreator();
                profileWriter.writeProfile(profileEntityCreator.getMyProfileEntities());

            }
        });
    }

    private void showProfile() {

        TextView name_tv = (TextView) findViewById(R.id.name_tv);
        TextView title_tv = (TextView) findViewById(R.id.title_tv);
        CircleImageView profileIV = (CircleImageView) findViewById(R.id.profile_image);

        for(Map.Entry<String, MyResumeEntity> entry: allEntities.entrySet()) {
            if(entry.getValue().getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                profile = (Profile)entry.getValue();
                break;
            }
        }

        profileReader.fetchImage(profileIV, profile.getImageUrl());
        name_tv.setText(profile.getName());
        title_tv.setText(profile.getTitle());
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



        PagesFragmentAdapter adapter = new PagesFragmentAdapter(this, getSupportFragmentManager(), allEntities);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);



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
