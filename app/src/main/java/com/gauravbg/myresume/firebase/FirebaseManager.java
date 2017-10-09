package com.gauravbg.myresume.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.gauravbg.myresume.firebase.FirebaseManager.DBTable.CONTENT;
import static com.gauravbg.myresume.firebase.FirebaseManager.DBTable.PAGES;
import static com.gauravbg.myresume.firebase.FirebaseManager.DBTable.PROFILES;
import static com.gauravbg.myresume.firebase.FirebaseManager.DBTable.SECTIONS;

/**
 * Created by gauravbg on 8/6/17.
 */

public class FirebaseManager {

    private static FirebaseDatabase mFirebaseDB;
    private static DatabaseReference mProfilesReference;

    public static final String IMAGE_FOLDER_PATH = "Profile_Images";


    public enum DBTable {
        PROFILES("profiles"),
        SECTIONS("sections"),
        PAGES("pages"),
        CONTENT("content");

        private String name;

        DBTable(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


    }


    private FirebaseManager() {
        //
    }

    public static FirebaseDatabase getFirebaseDB() {

        if(mFirebaseDB == null) {
            mFirebaseDB = FirebaseDatabase.getInstance();
        }
        return mFirebaseDB;
    }

    public static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getStorageReference(String path) {
        return getFirebaseStorage().getReference().child(path);
    }


    public static DatabaseReference getDBReference(DBTable tableName) {

        switch (tableName) {
            case PROFILES:
                return getFirebaseDB().getReference().child(PROFILES.getName());

            case SECTIONS:
                return getFirebaseDB().getReference().child(SECTIONS.getName());

            case PAGES:
                return getFirebaseDB().getReference().child(PAGES.getName());

            case CONTENT:
                return getFirebaseDB().getReference().child(CONTENT.getName());

            default:
                return null;

        }


    }
}
