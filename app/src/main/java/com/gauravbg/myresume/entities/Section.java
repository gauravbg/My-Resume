package com.gauravbg.myresume.entities;

import java.util.List;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Section implements MyResumeEntity{


    private String id;
    private String title;
    private int number;
    private String timeline;
    private String summary;
    private List<Section> subSections;
    private List<Content> contents;


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
        title = title;
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
        timeline = timeline;
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
}
