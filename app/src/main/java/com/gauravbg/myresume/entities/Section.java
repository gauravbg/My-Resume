package com.gauravbg.myresume.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Section implements MyResumeEntity, Parcelable{


    private String id;
    private String title;
    private int number;
    private String timeline;
    private String summary;
    private List<Section> subSections;
    private List<Content> contents;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    public Section(Parcel in){

        this.id = in.readString();
        this.title = in.readString();
        this.number = in.readInt();
        this.timeline = in.readString();
        this.summary = in.readString();
        this.subSections = in.createTypedArrayList(Section.CREATOR);
        this.contents = in.createTypedArrayList(Content.CREATOR);

    }

    public Section() {

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Section> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<Section> sections) {
        this.subSections = sections;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @Override
    public String getEntityType() {
        return MyResumeEntity.SECTION_TYPE;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.number);
        dest.writeString(this.timeline);
        dest.writeString(this.summary);
        dest.writeTypedList(this.subSections);
        dest.writeTypedList(this.contents);


    }
}
