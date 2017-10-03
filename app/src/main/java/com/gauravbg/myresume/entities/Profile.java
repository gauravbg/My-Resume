package com.gauravbg.myresume.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravbg on 8/6/17.
 */

public class Profile implements MyResumeEntity{


    private String id;
    private String username;
    private String name;
    private String title;
    private String email;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String address;
    private List<String> pages = new ArrayList<>();

    public Profile() {
    }

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

    @Override
    public String getEntityType() {
        return MyResumeEntity.PROFILE_TYPE;

    }
}
