package com.gauravbg.myresume;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.firebase.FirebaseDBManager;
import com.gauravbg.myresume.firebase.ProfileReader;
import com.gauravbg.myresume.firebase.ProfileWriter;
import com.gauravbg.myresume.utils.MyProfileEntityCreator;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private static final String GAURAV_PROFILE_ID = "-KvW1HBWUJOjZJxQ181t";
    private final String LOG = getClass().getCanonicalName();
    private Map<String, MyResumeEntity> allEntities;

    ProfileReader profileReader = new ProfileReader(new ProfileReader.EntityFetchListener() {
        @Override
        public void onEntityFetched(Map<String, MyResumeEntity> entities) {
            for(Map.Entry<String, MyResumeEntity> entry: entities.entrySet()) {
                if(entry.getValue().getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                    Log.d(LOG, ((Profile)entry.getValue()).getUsername() + "profile read!");
                    allEntities = entities;
                    showProfile();
                }
            }

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
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.demo_tab_1, PageFragment.class)
                .add(R.string.demo_tab_2, PageFragment.class)
                .add(R.string.demo_tab_3, PageFragment.class)
                .add(R.string.demo_tab_4, PageFragment.class)
                .add(R.string.demo_tab_5, PageFragment.class)
                .add(R.string.demo_tab_6, PageFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);


    }






}
