package com.gauravbg.myresume.entities;

import android.os.Parcelable;

/**
 * Created by gauravbg on 10/2/17.
 */

public interface MyResumeEntity extends Parcelable{


    String PROFILE_TYPE = "profile_type";
    String SECTION_TYPE = "section_type";
    String PAGE_TYPE = "page_type";
    String CONTENT_TYPE = "content_type";
    String CONTACT_PAGE_TYPE = "contact_page_type";

    String getEntityType();

}
