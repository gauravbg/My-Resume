package com.gauravbg.myresume.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Content implements MyResumeEntity, Parcelable{

    private String id;
    private String value;
    private long rating;
    private ContentType type;


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public Content(Parcel in){
        this.id = in.readString();
        this.value = in.readString();
        this.rating = in.readLong();
        this.type = ContentType.valueOf(in.readString());
    }

    public Content() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public enum ContentType {
        TEXT,
        RATING
    }

    @Override
    public String getEntityType() {
        return MyResumeEntity.CONTENT_TYPE;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.value);
        dest.writeLong(this.rating);
        dest.writeString(this.type.name());
    }
}