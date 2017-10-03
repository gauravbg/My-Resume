package com.gauravbg.myresume.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.gauravbg.myresume.firebase.FirebaseDBManager.DBTable.CONTENT;
import static com.gauravbg.myresume.firebase.FirebaseDBManager.DBTable.PAGES;
import static com.gauravbg.myresume.firebase.FirebaseDBManager.DBTable.PROFILES;
import static com.gauravbg.myresume.firebase.FirebaseDBManager.DBTable.SECTIONS;

/**
 * Created by gauravbg on 8/6/17.
 */

public class FirebaseDBManager {

    private static FirebaseDatabase mFirebaseDB;
    private static DatabaseReference mProfilesReference;


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


    private FirebaseDBManager() {
        //
    }

    public static FirebaseDatabase getFirebaseDB() {

        if(mFirebaseDB == null) {
            mFirebaseDB = FirebaseDatabase.getInstance();
        }
        return mFirebaseDB;
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
