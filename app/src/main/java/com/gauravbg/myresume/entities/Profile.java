package com.gauravbg.myresume.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravbg on 8/6/17.
 */

public class Profile implements MyResumeEntity, Parcelable {


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public Profile(Parcel in){

        this.id = in.readString();
        this.username = in.readString();
        this.name =  in.readString();
        this.title =  in.readString();
        this.email =  in.readString();
        this.phoneNumber =  in.readString();
        this.alternatePhoneNumber =  in.readString();
        this.address =  in.readString();
        this.imageUrl = in.readString();
        this.pages = in.createStringArrayList();
        this.links = in.createTypedArrayList(Link.CREATOR);
    }

    public Profile() {

    }

    private String id;
    private String username;
    private String name;
    private String title;
    private String email;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String address;
    private String imageUrl;

    private List<String> pages = new ArrayList<>();

    private List<Link> links = new ArrayList<>();

    public Profile(String username) {
        this.setUsername(username);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String getEntityType() {
        return MyResumeEntity.PROFILE_TYPE;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.alternatePhoneNumber);
        dest.writeString(this.address);
        dest.writeString(this.imageUrl);
        dest.writeStringList(this.pages);
        dest.writeTypedList(this.links);

    }
}
