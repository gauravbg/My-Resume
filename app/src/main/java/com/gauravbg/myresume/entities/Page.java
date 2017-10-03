package com.gauravbg.myresume.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Page implements MyResumeEntity{

    private String id;
    private String title;
    private int pageNumber;
    private List<Section> sections = new ArrayList<>();


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
}
