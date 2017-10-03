package com.gauravbg.myresume.firebase;

import android.os.AsyncTask;
import android.util.Log;

import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.entities.Section;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gauravbg on 10/1/17.
 */

public class ProfileReader {

    private final DatabaseReference profileRef = FirebaseDBManager.getDBReference(FirebaseDBManager.DBTable.PROFILES);
    private final DatabaseReference pagesRef = FirebaseDBManager.getDBReference(FirebaseDBManager.DBTable.PAGES);
    private EntityFetchListener listener;
    private final String LOG = getClass().getCanonicalName();
    private HashMap<String, MyResumeEntity> entities = new HashMap<>();
    private static int TASK_COUNT = 0;
    private static int taskCounter = 0;


    private EntityFetchListener localFetchListener = new EntityFetchListener() {

        @Override
        public void onEntityFetched(Map<String, MyResumeEntity> fetchedEntities) {
            taskCounter++;
            entities.putAll(fetchedEntities);
            if(taskCounter == TASK_COUNT && listener!= null) {
                Profile profile = null;
                List<String> pageIds = new ArrayList<>();
                for(Map.Entry<String, MyResumeEntity> entry: entities.entrySet()) {
                    if(entry.getValue().getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                        profile = (Profile)entry.getValue();
                    } else {
                        pageIds.add(entry.getKey());
                    }
                }
                if(profile != null)
                    profile.setPages(pageIds);
                if(listener != null)
                    listener.onEntityFetched(entities);
            }
        }

        @Override
        public void onEntityFetchFailed(DatabaseError databaseError) {
            if(listener != null) {
                listener.onEntityFetchFailed(databaseError);
            }
        }
    };


    public ProfileReader(EntityFetchListener listener) {
        this.listener = listener;
    }


    public void fetchProfile(String profileId) {
        TASK_COUNT = 0;
        taskCounter = 0;
        profileRef.child(profileId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TASK_COUNT++;
                if(dataSnapshot.hasChild("pages")) {
                    List<String> pages = (List<String>) dataSnapshot.child("pages").getValue();
                    for(String pageId: pages) {
                        TASK_COUNT++;
                        fetchPage(pageId);
                    }
                }
                ProfileDataParser dataParser = new ProfileDataParser(localFetchListener);
                dataParser.execute(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(listener != null)
                    listener.onEntityFetchFailed(databaseError);

            }
        });

    }

    private void fetchPage(String pageId) {

        pagesRef.child(pageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PageDataParser dataParser = new PageDataParser(localFetchListener);
                dataParser.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(listener != null)
                    listener.onEntityFetchFailed(databaseError);

            }
        });
    }


    private class ProfileDataParser extends AsyncTask<DataSnapshot, Integer, Profile> {

        private EntityFetchListener listener;
        private final String LOG = this.getClass().getCanonicalName();

        public ProfileDataParser(EntityFetchListener listener) {
            this.listener = listener;
        }

        @Override
        protected Profile doInBackground(DataSnapshot... params) {
            Log.d(LOG, "doInBackground called");
            DataSnapshot profileSnapShot= params[0];
            Profile profile = new Profile();
            profile.setId(profileSnapShot.getKey());
            for(DataSnapshot child: profileSnapShot.getChildren()) {
                if(child.getKey().equals("pages")) {
                    //do nothing
                } else {
                    String value = (String)child.getValue();
                    switch(child.getKey()) {
                        case "name": profile.setName(value); break;
                        case "address": profile.setAddress(value); break;
                        case "email": profile.setEmail(value); break;
                        case "phoneNumber": profile.setPhoneNumber(value); break;
                        case "title": profile.setTitle(value); break;
                        case "username": profile.setUsername(value); break;
                        default: break;

                    }
                }


            }

            return profile;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            super.onPostExecute(profile);
            Log.d(LOG, "onPostExecute called");
            if(listener != null) {
                HashMap<String, MyResumeEntity> entities = new HashMap<>();
                entities.put(profile.getId(), profile);
                listener.onEntityFetched(entities);
            }
        }

    }


    private class PageDataParser extends AsyncTask<DataSnapshot, Integer, Page> {

        private EntityFetchListener listener;
        private final String LOG = this.getClass().getCanonicalName();

        public PageDataParser(EntityFetchListener listener) {
            this.listener = listener;
        }

        @Override
        protected Page doInBackground(DataSnapshot... params) {
            Log.d(LOG, "doInBackground called");
            DataSnapshot pageSnapShot= params[0];
            Page page = new Page();
            page.setId(pageSnapShot.getKey());
            for(DataSnapshot child: pageSnapShot.getChildren()) {
                if(child.getKey().equals("sections")) {
                    List<HashMap<String, Object>> sectionsMap = (List<HashMap<String, Object>>) child.getValue();
                    List<Section> sections = new ArrayList<>();
                    for(HashMap<String, Object> sectionMap: sectionsMap) {
                        sections.add(getSection(sectionMap));
                    }
                    page.setSections(sections);
                } else {
                    switch(child.getKey()) {
                        case "pageNumber": page.setPageNumber(((Long)child.getValue()).intValue()); break;
                        case "title": page.setTitle((String)child.getValue()); break;
                        default: break;

                    }
                }


            }

            return page;
        }

        @Override
        protected void onPostExecute(Page page) {
            super.onPostExecute(page);
            Log.d(LOG, "onPostExecute called");
            if(listener != null) {
                HashMap<String, MyResumeEntity> entities = new HashMap<>();
                entities.put(page.getId(), page);
                listener.onEntityFetched(entities);
            }
        }


        private Section getSection(HashMap<String, Object> sectionMap) {

            Section section = new Section();
            for(Map.Entry<String, Object> entry: sectionMap.entrySet()) {
                switch (entry.getKey()) {
                    case "id": section.setId((String)entry.getValue()); break;
                    case "title": section.setTitle((String)entry.getValue()); break;
                    case "number": section.setNumber(((Long)entry.getValue()).intValue()); break;
                    case "timeline": section.setTimeline((String)entry.getValue()); break;
                    case "summary": section.setSummary((String)entry.getValue()); break;
                    case "subSections":
                        List<HashMap<String, Object>> subSectionsMap = (List<HashMap<String, Object>>) entry.getValue();
                        List<Section> subSections = new ArrayList<>();
                        for(HashMap<String, Object> subSectionMap: subSectionsMap) {
                            subSections.add(getSection(subSectionMap));
                        }
                        section.setSubSections(subSections);
                        break;
                    case "contents":
                        List<HashMap<String, Object>> contentsMap = (List<HashMap<String, Object>>) entry.getValue();
                        List<Content> contents = new ArrayList<>();
                        for(HashMap<String, Object> contentMap: contentsMap) {
                            contents.add(getContent(contentMap));
                        }
                        section.setContents(contents);
                        break;
                    default: break;
                }
            }
            return section;
        }


        private Content getContent(HashMap<String, Object> contentMap) {
            Content content = new Content();
            for(Map.Entry<String, Object> entry: contentMap.entrySet()) {
                switch (entry.getKey()) {
                    case "id": content.setId((String)entry.getValue()); break;
                    case "value": content.setValue((String)entry.getValue()); break;
                    case "type":
                        switch((String)entry.getValue()) {
                            case "RATING": content.setType(Content.ContentType.RATING); break;
                            case "TEXT": content.setType(Content.ContentType.TEXT); break;
                            default: break;
                        }
                        break;
                    default: break;
                }

            }
            return content;
        }

    }


    public interface EntityFetchListener {

        void onEntityFetched(Map<String, MyResumeEntity> entities);

        void onEntityFetchFailed(DatabaseError databaseError);
    }


}