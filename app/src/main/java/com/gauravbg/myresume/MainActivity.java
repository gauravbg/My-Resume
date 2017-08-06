package com.gauravbg.myresume;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.firebase.FirebaseDBManager;
import com.google.firebase.database.DatabaseReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        final DatabaseReference profileRef = FirebaseDBManager.getDBReference(FirebaseDBManager.DBTable.PROFILES);
        Button addProfile = (Button) findViewById(R.id.add_profile);
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile gauravbg = new Profile("gauravbg");
                gauravbg.setName("Gaurav Gopalkrishna");
                gauravbg.setEmail("gaurav.gopalkrishna@stonybrook.edu");
                gauravbg.setPhoneNumber("(631)-452-6564");
                gauravbg.setTitle("Computer Science gradute student");
                gauravbg.setAddress("700 health sciences Drive, Chapin I-1129A, Stony Brook");
                profileRef.push().setValue(gauravbg);
            }
        });


    }
}
