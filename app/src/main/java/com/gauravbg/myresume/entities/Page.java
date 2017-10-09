package com.gauravbg.myresume.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Page implements MyResumeEntity, Parcelable{

    private String id;
    private String title;
    private int pageNumber;
    private List<Section> sections = new ArrayList<>();

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }


    };

    public Page(Parcel in) {

        this.id = in.readString();
        this.title = in.readString();
        this.pageNumber = in.readInt();
        this.sections = in.createTypedArrayList(Section.CREATOR);

    }

    public Page() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String getEntityType() {
        return MyResumeEntity.PAGE_TYPE;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.pageNumber);
        dest.writeTypedList(this.sections);

    }


}
