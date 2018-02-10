package com.gauravbg.myresume;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.firebase.FirebaseManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gauravbg on 2/10/18.
 */

public class SearchActivity extends AppCompatActivity {

    private EditText searchET;
    private ImageButton searchBtn;
    private RecyclerView allProfileRV;

    private final DatabaseReference mProfileRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PROFILES);
    private FirebaseRecyclerAdapter<Profile, ProfileViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        getSupportActionBar().setTitle("Search Users");

        searchET = (EditText) findViewById(R.id.search_et);
        searchBtn = (ImageButton) findViewById(R.id.search_button);
        allProfileRV = (RecyclerView) findViewById(R.id.all_profile_rc);
        allProfileRV.setHasFixedSize(true);
        allProfileRV.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FirebaseRecyclerAdapter<Profile, ProfileViewHolder>(Profile.class, R.layout.profile_view_layout, ProfileViewHolder.class, mProfileRef) {
            @Override
            protected void populateViewHolder(ProfileViewHolder viewHolder, Profile model, int position) {
                viewHolder.setupView(model.getName(), model.getImageUrl(), model.getTitle(), model.getId());
            }
        };

        allProfileRV.setAdapter(adapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProfile(searchET.getText().toString());
            }
        });

        //setupDevProfile();


    }


    private void searchProfile(String userName) {

        Query searchQuery = mProfileRef.orderByChild("name").startAt(userName).endAt(userName + "\uf8ff");
        adapter = new FirebaseRecyclerAdapter<Profile, ProfileViewHolder>(Profile.class, R.layout.profile_view_layout, ProfileViewHolder.class, searchQuery) {
            @Override
            protected void populateViewHolder(ProfileViewHolder viewHolder, Profile model, int position) {
                viewHolder.setupView(model.getName(), model.getImageUrl(), model.getTitle(), model.getId());
            }
        };

        allProfileRV.setAdapter(adapter);
    }


    private void setupDevProfile() {
        EditText devName = (EditText) findViewById(R.id.dev_name_et);
        EditText devTitle = (EditText) findViewById(R.id.dev_title_et);
    }


    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setupView(String name, String url, String title, final String uid) {
            EditText nameET = (EditText)mView.findViewById(R.id.name_et);
            EditText titleET = (EditText)mView.findViewById(R.id.title_et);
            CircleImageView imageIV = (CircleImageView) mView.findViewById(R.id.profile_image);

            nameET.setText(name);
            titleET.setText(title);

            Glide.with(imageIV.getContext()).load(url).into(imageIV);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra(MainActivity.UID, uid);
                    intent.putExtra(MainActivity.IS_MY_PROFILE, false);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
