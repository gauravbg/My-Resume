package com.gauravbg.myresume;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.gauravbg.myresume.entities.Content;
import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Section;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by gauravbg on 8/6/17.
 */

public class PageFragment extends Fragment {

    private Page pageData;
    private final String LOG = getClass().getCanonicalName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_fragment_layout, container, false);
        if(getArguments() != null) {
            this.pageData = getArguments().getParcelable(MyResumeEntity.PAGE_TYPE);
        }

        if (pageData != null) {
            LinearLayout sectionsContainer = (LinearLayout) rootView.findViewById(R.id.sections_container);
            Collections.sort(pageData.getSections(), new Comparator<Section>() {
                @Override
                public int compare(Section o1, Section o2) {
                    return o1.getNumber() - o2.getNumber();
                }
            });
            for(Section section: pageData.getSections()) {
                sectionsContainer.addView(getSection(section, false));
            }
        }

        return rootView;
    }

    private View getSection(Section section, boolean isSubSection) {
        LinearLayout sectionLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.section_layout, null);
        if (isSubSection) {
            sectionLayout.setPadding(0,0,0,0);
        }
        if (section.getTitle() == null && section.getTimeline() == null) {
            sectionLayout.findViewById(R.id.section_title_container).setVisibility(View.GONE);
        } else {
            final ExpandableLayout sectionContentPart = (ExpandableLayout) sectionLayout.findViewById(R.id.expandableLayout);
            sectionLayout.findViewById(R.id.section_title_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionContentPart.toggle();
                }
            });

            if (section.getTitle() != null) {
                TextView title = (TextView) sectionLayout.findViewById(R.id.section_title);
                if (isSubSection) {
                    title.setAllCaps(false);
                }
                title.setText(section.getTitle());
            }

            if (section.getTimeline() != null) {
                TextView timeline = (TextView) sectionLayout.findViewById(R.id.section_timeline);
                timeline.setText(section.getTimeline());
            }

        }

        if(section.getSummary() == null) {
            sectionLayout.findViewById(R.id.section_summary_container).setVisibility(View.GONE);
        } else {
            DocumentView documentView = (DocumentView) sectionLayout.findViewById(R.id.section_summary);
            documentView.setText((CharSequence)(section.getSummary()));
        }

        RelativeLayout contentSection = (RelativeLayout) sectionLayout.findViewById(R.id.section_content_section);
        if (section.getContents() == null) {
            contentSection.setVisibility(View.GONE);
        } else {
            LinearLayout contentContainer  = (LinearLayout) contentSection.findViewById(R.id.section_content_container);
            for (Content content: section.getContents()) {
                contentContainer.addView(getContent(content));
            }
        }

        if (section.getSubSections() == null) {
            sectionLayout.findViewById(R.id.section_subsection_container).setVisibility(View.GONE);
        } else {
            LinearLayout subsections = (LinearLayout) sectionLayout.findViewById(R.id.section_subsection_container);
            for (Section subsection: section.getSubSections()) {
                subsections.addView(getSection(subsection, true));
            }
        }

        return sectionLayout;
    }

    private View getContent(Content content) {

        LinearLayout contentLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.content_layout, null);
        if(content.getType() == Content.ContentType.RATING) {
            contentLayout.findViewById(R.id.text_content_text).setVisibility(View.GONE);
            TextView contentText = (TextView) contentLayout.findViewById(R.id.rating_content_text);
            contentText.setText(content.getValue());

            RatingBar rating = (RatingBar) contentLayout.findViewById(R.id.rating_content_rating);
            rating.setRating((float) (content.getRating()/2.0));
        } else {
            contentLayout.findViewById(R.id.rating_content_line).setVisibility(View.GONE);
            DocumentView documentView = (DocumentView) contentLayout.findViewById(R.id.text_content_text);
            documentView.setText((CharSequence)(content.getValue()));
        }

        return contentLayout;
    }

    public void invalidateRecursive(ViewGroup layout) {
        int count = layout.getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = layout.getChildAt(i);
            if(child instanceof ViewGroup)
                invalidateRecursive((ViewGroup) child);
            else
                child.invalidate();
        }
    }
}
