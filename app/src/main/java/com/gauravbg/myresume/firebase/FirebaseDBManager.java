package com.gauravbg.myresume.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gauravbg on 8/6/17.
 */

public class FirebaseDBManager {

    private static FirebaseDatabase mFirebaseDB;
    private static DatabaseReference mProfilesReference;


    public enum DBTable {
        PROFILES,
        SECTIONS
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

        switch(tableName) {
            case PROFILES:
                return getFirebaseDB().getReference().child("profiles");

            case SECTIONS:
                return getFirebaseDB().getReference().child("sections");


            default:
                return null;

        }


    }
}
