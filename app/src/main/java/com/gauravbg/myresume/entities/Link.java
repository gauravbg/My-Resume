package com.gauravbg.myresume.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gauravbg on 12/28/17.
 */

public class Link implements Parcelable{

    private String name;
    private String url;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Link createFromParcel(Parcel in) {
            return new Link(in);
        }

        public Link[] newArray(int size) {
            return new Link[size];
        }
    };

    public Link(Parcel in){
        this.name = in.readString();
        this.url = in.readString();
    }

    public Link() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
    }
}
