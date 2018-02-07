package com.gauravbg.myresume.utils;

import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.Link;
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

    public static List<MyResumeEntity> getMyProfileEntities() {
        List<MyResumeEntity> entities = new ArrayList<>();
        entities.add(getMyProfile());
        for(MyResumeEntity entity: getPages()) {
            entities.add(entity);
        }
        return entities;
    }

    private static Profile getMyProfile() {

        // Profile
        Profile profile = new Profile("gauravbg");
        profile.setName("Gaurav Gopalkrishna");
        profile.setEmail("gaurav.gopalkrishna@stonybrook.edu");
        profile.setPhoneNumber("(631)-452-6564");
        profile.setTitle("Computer Science Graduate Student");
        profile.setAddress("700 health sciences Drive, Chapin I-1129A, Stony Brook");
        profile.setImageUrl("https://firebasestorage.googleapis.com/v0/b/myresume-94eda.appspot.com/o/Profile_Images%2FC360_2017-04-02-23-06-14-602.jpg?alt=media&token=4f577b49-c2fd-42ae-a391-5960c3fd38fe");
        List<Link> links = new ArrayList<>();
        Link githubLink = new Link();
        githubLink.setName("GitHub");
        githubLink.setUrl("https://github.com/gauravbg");
        Link linkedInLink = new Link();
        linkedInLink.setName("LinkedIn");
        linkedInLink.setUrl("https://in.linkedin.com/in/gaurav-b-g-30253079");
        links.add(githubLink);
        links.add(linkedInLink);
        profile.setLinks(links);
        return profile;

    }

    private static List<Page> getPages() {

        List<Page> pages = new ArrayList<>();
        pages.add(getProfilePage());
        pages.add(getAcademicPage());
        pages.add(getIndustryExperiencePage());
        pages.add(getAcademicProjects());
        pages.add(getAcheivementsPage());
        pages.add(getContactPage(getMyProfile()));
        return pages;
    }


    private static Page getProfilePage() {

        //Profile Page
        //========================================
        Page profilePage = new Page();
        profilePage.setTitle("Profile");
        profilePage.setPageNumber(0);


        //Summary Section
        Section summarySection = new Section();
        summarySection.setTitle("Professional Summary");
        summarySection.setSummary("I'm a Graduate Student in the Department of Computer Science at Stony Brook University. I have 4 years of industry experience working as Senior Software Engineer at Samsung R & D Institute, India and as a Software Engineering / Data Science Intern working at Qpid Health, Boston.\n" +
                "My interests include Mobile & Web Technologies, Data Science, Big data, Natural Language Processing & Machine Learning. I joined Data Management and Biomedical Data Analytics Lab, SBU in Jan 2017. I am working on Social Media Analytics for Drug Related Public Health Studies project under the guidance of Prof. Fusheng Wang.\n" +
                "I am currently looking for Software Engineering full-time opportunities starting February 2017");
        summarySection.setNumber(0);


        //Programming Content
        List<String> skillsText = Arrays.asList("Java", "Python", "JavaScript", "C++", "SQL", "C");
        List<Content> programmingLangContent = new ArrayList<>();
        for (String skill: skillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            programmingLangContent.add(content);
        }

        Section programmingLangsSection = new Section();
        programmingLangsSection.setNumber(0);
        programmingLangsSection.setTitle("Programming Languages");
        programmingLangsSection.setContents(programmingLangContent);

        //Web Content
        List<String> webskillsText = Arrays.asList("XML", "HTML", "CSS", "REST", "JSON");
        List<Content> webContent = new ArrayList<>();
        for (String skill: webskillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            webContent.add(content);
        }

        Section webSection = new Section();
        webSection.setNumber(1);
        webSection.setTitle("Web");
        webSection.setContents(webContent);

        //DB Content
        List<String> dbskillsText = Arrays.asList("MySQL", "Postgres", "DB2", "SQLite", "NoSQL", "MongoDB");
        List<Content> dbContent = new ArrayList<>();
        for (String skill: dbskillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            dbContent.add(content);
        }

        Section dbSection = new Section();
        dbSection.setNumber(2);
        dbSection.setTitle("Database Systems");
        dbSection.setContents(dbContent);

        //Platforms
        List<String> platformskillsText = Arrays.asList("Android SDK", "Ember", "Java EJB", "Hadoop", "MapReduce", "Apache Spark", "Scikit Learn", "AWS", "D3", "Hibernate", "JUnit", "Espresso", "Mockito", "JAX-RS", "Firebase", "Nodes.js", "OpenGL", "Linux", "Windows");
        List<Content> platformContent = new ArrayList<>();
        for (String skill: platformskillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            platformContent.add(content);
        }

        Section platformSection = new Section();
        platformSection.setNumber(3);
        platformSection.setTitle("Platforms & Frameworks");
        platformSection.setContents(platformContent);


        //Sw Tools
        List<String> swkillsText = Arrays.asList("Eclipse", "Android Studio", "GIT", "Jupyter Notebook", "Perforce", "Gradle", "Maven", "Npm", "Review Board", "Bitbucket", "Wildfly", "Glassfish", "Flyway");
        List<Content> swContent = new ArrayList<>();
        for (String skill: swkillsText) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            swContent.add(content);
        }

        Section swSection = new Section();
        swSection.setNumber(4);
        swSection.setTitle("Software Tools");
        swSection.setContents(swContent);


        //Skills Section
        Section skillsSection = new Section();
        skillsSection.setNumber(1);
        skillsSection.setTitle("Technical Skills");
        List<Section> subSections = new ArrayList<>();
        subSections.add(programmingLangsSection);
        subSections.add(webSection);
        subSections.add(dbSection);
        subSections.add(platformSection);
        subSections.add(swSection);
        skillsSection.setSubSections(subSections);

        List<Section> sections = new ArrayList<>();
        sections.add(summarySection);
        sections.add(skillsSection);
        profilePage.setSections(sections);
        return profilePage;
    }


    private static Page getAcademicPage() {

        //Academic Qualification Page
        Page page = new Page();
        page.setTitle("Academic Qualification");
        page.setPageNumber(1);

        List<Section> sections = new ArrayList<>();

        //Stony Section
        Section stony = new Section();
        stony.setTitle("Stony Brook University");
        stony.setTimeline("Aug 2016 - Present");
        stony.setNumber(0);
        stony.setSummary("Master of Science in Computer Science \nGPA: 3.41 \nStony Brook, NY, USA");

        List<Section> subsections = new ArrayList<>();
        Section coursework = new Section();
        coursework.setTitle("Coursework");
        coursework.setNumber(0);
        //Coursework
        List<String> course = Arrays.asList("Analysis of Algorithms", "Data Visualization", "Database Systems", "Big Data Analytics", "Artificial Intelligence",
                "Natural Language Processing", "Computer Networks", "Computer Graphics", "Computational Biology");
        List<Content> courseContent = new ArrayList<>();
        for (String skill: course) {
            Content content =  new Content();
            content.setType(Content.ContentType.RATING);
            content.setRating(9);
            content.setValue(skill);
            courseContent.add(content);
        }
        coursework.setContents(courseContent);
        subsections.add(coursework);
        stony.setSubSections(subsections);


        //RV Section
        Section rv = new Section();
        rv.setTitle("R.V.College of Engineering");
        rv.setTimeline("Aug 2008 - June 2012");
        rv.setNumber(1);
        rv.setSummary("Bachelor of Engineering in Computer Science \nGPA: 8.54/10 \nBangalore, India");


        sections.add(stony);
        sections.add(rv);
        page.setSections(sections);

        return page;
    }

    private static Page getIndustryExperiencePage() {
        Page page = new Page();
        page.setPageNumber(2);
        page.setTitle("Industry Experience");

        Section samsungSection = new Section();
        samsungSection.setNumber(0);
        samsungSection.setSummary("Senior Software Engineer");
        samsungSection.setTimeline("Aug 2012 - Aug 2016");
        samsungSection.setTitle("Samsung R & D Institute, India");

        List<String> points = Arrays.asList("Collaborated with team in Samsung Korea in Design and Development of Framework to handle the life cycle of all\n" +
                        "the plugin health applications via host application.", "Implemented a custom Staggered grid view widget to support different sized tiles with animations and “View\n" +
                        "Recycling” algorithm for optimizing memory and increased responsiveness of app dashboard.", "Implemented backend for generating holistic summary report from user health data across all health tracker plugins\n" +
                        "and created front end visualizations of data insights.", "Memory Leak Analysis, automated unit testing, RESTful services for Mobile-EMR client application.", "Integration of ANT+ SDK to Samsung Health SDK.",
                "Added API support in Health SDK for ANT+, Bluetooth protocol device connection and data retrieval features.", "Implemented Health Activity Recommender system for Samsung Hackathon Event 2016.", "Worked on converting iWork files conforming to OpenXML standards to MS office format.");
        List<Content> samsungContent = new ArrayList<>();
        for (String point: points) {
            Content content =  new Content();
            content.setType(Content.ContentType.TEXT);
            content.setValue(point);
            samsungContent.add(content);
        }
        samsungSection.setContents(samsungContent);


        Section qpidSection = new Section();
        qpidSection.setNumber(1);
        qpidSection.setSummary("Software Engineer Intern");
        qpidSection.setTimeline("May 2012 - Aug 2017");
        qpidSection.setTitle("QPID Health, Boston");

        List<String> qpidPoints = Arrays.asList("Implemented Role Based Access control framework for QPID Health applications using Apache Shiro Architecture.", "Implemented ember application and backend to modify the underlying security data used by Apache Shiro.", "Implemented Text classifier for clinical evidence quality.", "Data Analysis and Feature extraction for modeling user trust score to aid fraud detection.");
        List<Content> qpidContent = new ArrayList<>();
        for (String point: qpidPoints) {
            Content content =  new Content();
            content.setType(Content.ContentType.TEXT);
            content.setValue(point);
            qpidContent.add(content);
        }
        qpidSection.setContents(qpidContent);

        List<Section> subsections = new ArrayList<>();
        subsections.add(samsungSection);
        subsections.add(qpidSection);

        page.setSections(subsections);
        return page;

    }


    private static Page getAcademicProjects() {
        Page page = new Page();
        page.setTitle("Academic & Personal Projects");
        page.setPageNumber(3);
        Section section = new Section();
        section.setNumber(0);
        List<String> points = Arrays.asList("Drug Abuse Sentiment analytics using Twitter & Reddit text data. (Python, Scikit-Learn, AWS, Apache Spark)", "Data Visualization for Soccer Scouting using D3 (JavaScript, D3, Python, MongoDB)", "Advanced Stock Analysis on Big data using AWS cluster. (Java, Hadoop, MapReduce, AWS)", "Implementation of binary encoded packed suffix array for substring search in transcriptome. (C++)", "“My Resume” android application in google play store. (Android, Java)");
        List<Content> contents = new ArrayList<>();
        for (String point: points) {
            Content content =  new Content();
            content.setType(Content.ContentType.TEXT);
            content.setValue(point);
            contents.add(content);
        }
        section.setContents(contents);
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        page.setSections(sections);
        return page;

    }


    private static Page getAcheivementsPage() {
        Page page = new Page();
        page.setTitle("Achievements & Extracurricular Activities");
        page.setPageNumber(4);
        Section section = new Section();
        section.setNumber(0);
        List<String> points = Arrays.asList("Best Employee of month Award in July 2014, July 2015 & April 2016 at Samsung R&D Institute, India.", "Achieved advanced level certificate in global competitive programming test conducted by Samsung.", "Student Body President for Cultural Activities at R.V. College of Engineering (2010 – 2012).");
        List<Content> contents = new ArrayList<>();
        for (String point: points) {
            Content content =  new Content();
            content.setType(Content.ContentType.TEXT);
            content.setValue(point);
            contents.add(content);
        }
        section.setContents(contents);
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        page.setSections(sections);
        return page;

    }

    public static Page getContactPage(Profile profile) {
        Page page = new Page();
        page.setTitle("Contact");
        page.setPageNumber(5);
        Section section = new Section();
        section.setNumber(0);
        List<Content> contents = new ArrayList<>();

        //address
        Content address = new Content();
        address.setType(Content.ContentType.TEXT);
        address.setValue("Address: " + profile.getAddress());
        contents.add(address);
        //phone
        Content phone = new Content();
        phone.setType(Content.ContentType.TEXT);
        phone.setValue("Phone Number: " + profile.getPhoneNumber());
        contents.add(phone);
        //email
        Content email = new Content();
        email.setType(Content.ContentType.TEXT);
        email.setValue("Email: " + profile.getEmail());
        contents.add(email);
        //other
        for (Link link : profile.getLinks()) {
            Content content = new Content();
            content.setType(Content.ContentType.TEXT);
            content.setValue(link.getName() + ": " + link.getUrl());
            contents.add(content);
        }

        section.setContents(contents);
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        page.setSections(sections);
        return page;
    }


}
