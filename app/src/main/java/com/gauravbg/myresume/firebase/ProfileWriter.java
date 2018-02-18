package com.gauravbg.myresume.firebase;

import android.util.Log;

import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.Link;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.entities.Section;
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

    private DatabaseReference.CompletionListener completionListener;



    //TODO: Needs profile to be the first entity. Fix this Logic
    public void writeProfile(List<MyResumeEntity> entities, String uid) {

        completionListener = new DatabaseReference.CompletionListener() {

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
        for(MyResumeEntity entity: entities) {
            if(entity.getEntityType() == MyResumeEntity.PAGE_TYPE) {
                Page page = (Page) entity;
                if(page.getId() != null) {
                    pagesRef.child(page.getId()).removeValue();
                }
            } else if(entity.getEntityType() == MyResumeEntity.PROFILE_TYPE) {
                ((Profile)entity).getPages().clear();
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
                Page page = (Page) entity;
                if(page.isContactPage()) {
                    Section defaultSection = page.getSections().get(0);
                    profile.getLinks().clear();
                    List<Link> links = new ArrayList<>();
                    for(Content content: defaultSection.getContents()) {
                        if(content.getValue().equals("Email")) {
                            this.profile.setEmail(content.getValue2());
                        } else if(content.getValue().equals("Address")) {
                            this.profile.setAddress(content.getValue2());
                        } else if(content.getValue().equals("Phone Number")) {
                            this.profile.setPhoneNumber(content.getValue2());
                        }

                        if(content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
                            Link link = new Link();
                            link.setName(content.getValue());
                            link.setUrl(content.getValue2());
                            links.add(link);
                        }
                    }
                    profile.setLinks(links);
                }
                pagesRef.push().setValue(entity, completionListener);
            }
        }

    }



    public interface EntitySaveListener {

        void onEntitySaved(DatabaseReference databaseReference);

        void onEntitySaveFailed(DatabaseError databaseError);
    }
}
