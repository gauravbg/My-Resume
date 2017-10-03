package com.gauravbg.myresume.utils;

import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;
import com.gauravbg.myresume.entities.Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gauravbg on 10/2/17.
 */

public class MyProfileEntityCreator {

    public List<MyResumeEntity> getMyProfileEntities() {
        List<MyResumeEntity> entities = new ArrayList<>();
        entities.add(getMyProfile());
        for(MyResumeEntity entity: getPages()) {
            entities.add(entity);
        }
        return entities;
    }

    private Profile getMyProfile() {

        // Profile
        Profile profile = new Profile("gauravbg");
        profile.setName("Gaurav Gopalkrishna");
        profile.setEmail("gaurav.gopalkrishna@stonybrook.edu");
        profile.setPhoneNumber("(631)-452-6564");
        profile.setTitle("Computer Science gradute student");
        profile.setAddress("700 health sciences Drive, Chapin I-1129A, Stony Brook");
        return profile;

    }

    private List<Page> getPages() {

        List<Page> pages = new ArrayList<>();

        //Profile Page
        //========================================
        Page profilePage = new Page();
        profilePage.setTitle("Profile");
        profilePage.setPageNumber(0);


        //Summary Section
        Section summarySection = new Section();
        summarySection.setTitle("Professional Summary");
        summarySection.setSummary("This is my professsional Summary. Am a graduate student at Stony Brook University. I am currently looking for full-time opportunities");
        summarySection.setNumber(0);


        //Skills Content
        List<String> skillsText = Arrays.asList("Java", "Python", "JavaScript");
        List<Content> programmingLangContent = new ArrayList<>();
        for (String skill: skillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setValue(skill);
            programmingLangContent.add(content);
        }

        //Skills Section
        Section skillsSection = new Section();
        skillsSection.setNumber(1);
        skillsSection.setTitle("Technical Skills");
        skillsSection.setContents(programmingLangContent);

        Section programmingLangsSection = new Section();
        programmingLangsSection.setNumber(0);
        programmingLangsSection.setTitle("Programming Languages");
        programmingLangsSection.setContents(programmingLangContent);

        List<Section> subSections = new ArrayList<>();
        subSections.add(programmingLangsSection);
        skillsSection.setSubSections(subSections);

        List<Section> sections = new ArrayList<>();
        sections.add(summarySection);
        sections.add(skillsSection);
        profilePage.setSections(sections);

        pages.add(profilePage);
        return pages;
    }

}
