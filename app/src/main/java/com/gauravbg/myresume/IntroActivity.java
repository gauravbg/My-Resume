package com.gauravbg.myresume;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gauravbg.myresume.firebase.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mehdi.sakout.fancybuttons.FancyButton;

public class IntroActivity extends AppCompatActivity {

    private FancyButton searchButton;
    private FancyButton createViewButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private FirebaseUser mUser;
    private boolean profileExists =  false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        createViewButton = (FancyButton) findViewById(R.id.create_view_button);
        searchButton = (FancyButton) findViewById(R.id.search_button);

        mAuth = FirebaseAuth.getInstance();

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    createViewButton.setText(getResources().getString(R.string.view_resume));
                } else {
                    createViewButton.setText(getResources().getString(R.string.create_resume));
                }
            }
        };

        createViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null) {
                    Intent intent = new Intent(IntroActivity.this, AccountSetupActivity.class);
                    IntroActivity.this.startActivity(intent);
                } else {
                    if(profileExists) {
                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                        if(mUser != null) {
                            intent.putExtra(MainActivity.UID, mUser.getUid());
                            intent.putExtra(MainActivity.IS_MY_PROFILE, true);
                            IntroActivity.this.startActivity(intent);
                        }
                    }

                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SearchActivity.class);
                IntroActivity.this.startActivity(intent);
//                if(mUser != null) {
//                    intent.putExtra(MainActivity.UID, mUser.getUid());
//                    intent.putExtra(MainActivity.IS_MY_PROFILE, false);
//                    IntroActivity.this.startActivity(intent);
//                } else {
//                    //Do Nothing
//                }
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = mAuth.getCurrentUser();
        if(mUser != null) {
            DatabaseReference profileRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PROFILES);
            profileRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        mAuth.signOut();
                        profileExists = false;
                        mUser = null;
                        createViewButton.setText(getResources().getString(R.string.create_resume));
                    } else {
                        profileExists = true;
                        createViewButton.setText(getResources().getString(R.string.view_resume));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            profileExists = false;
            createViewButton.setText(getResources().getString(R.string.create_resume));
        }
    }
}
