package com.gauravbg.myresume.firebase;

import android.util.Log;

import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravbg on 10/2/17.
 */

public class ProfileWriter {

    private final DatabaseReference profileRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PROFILES);
    private final DatabaseReference pagesRef = FirebaseManager.getDBReference(FirebaseManager.DBTable.PAGES);
    private EntitySaveListener listener;
    private final String LOG = getClass().getCanonicalName();
    private int saveEntityCount = 0;
    private int saveEntityCounter = 0;
    private Profile profile;
    private String profileId;

    public ProfileWriter(ProfileWriter.EntitySaveListener listener) {
        this.listener = listener;
    }

    private DatabaseReference.CompletionListener completionListener = new DatabaseReference.CompletionListener() {

        private List<String> pageIds = new ArrayList<>();
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            if(databaseError != null) {
                if(listener != null) {
                    listener.onEntitySaveFailed(databaseError);
                }
            } else {
                saveEntityCounter++;
                if(saveEntityCounter == saveEntityCount-1) {
                    pageIds.add(databaseReference.getKey());
                    profile.setPages(pageIds);
                    profileRef.child(profileId).setValue(profile, completionListener);
                } else if(saveEntityCounter == saveEntityCount) {
                    if(listener != null) {
                        listener.onEntitySaved(databaseReference);
                    }
                } else {
                    pageIds.add(databaseReference.getKey());
                }
            }

        }

    };



    //TODO: Needs profile to be the first entity. Fix this Logic
    public void writeProfile(List<MyResumeEntity> entities, String uid) {

        for(MyResumeEntity entity: entities) {
            if(entity.getEntityType() == MyResumeEntity.PAGE_TYPE) {
                Page page = (Page) entity;
                pagesRef.child(page.getId()).removeValue();
            }
        }
        saveEntityCount = 0;
        saveEntityCounter = 0;
        profileId = uid;
        saveEntityCount = entities.size();

        for(MyResumeEntity entity: entities) {
            if(entity.getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                this.profile = (Profile) entity;
            } else {
                pagesRef.push().setValue(entity, completionListener);
            }
        }

    }



    public interface EntitySaveListener {

        void onEntitySaved(DatabaseReference databaseReference);

        void onEntitySaveFailed(DatabaseError databaseError);
    }
}
