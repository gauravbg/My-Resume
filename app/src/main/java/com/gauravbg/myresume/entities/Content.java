package com.gauravbg.myresume.entities;

/**
 * Created by gauravbg on 8/22/17.
 */

public class Content implements MyResumeEntity{

    private String id;
    private String value;
    private ContentType type;


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

    public enum ContentType {
        TEXT,
        RATING
    }

    @Override
    public String getEntityType() {
        return MyResumeEntity.CONTENT_TYPE;

    }
}
