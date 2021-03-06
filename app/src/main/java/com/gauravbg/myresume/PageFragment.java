package com.gauravbg.myresume;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gauravbg on 8/6/17.
 */

public class PageFragment extends Fragment {

    private Page pageData;
    private boolean isEditMode;
    private final String LOG = getClass().getCanonicalName();
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_fragment_layout, container, false);
        if(getArguments() != null) {
            this.pageData = getArguments().getParcelable(MyResumeEntity.PAGE_TYPE);
            this.isEditMode = getArguments().getBoolean("IS_EDIT_MODE");
        }
        mRootView = rootView;
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (pageData != null && mRootView != null) {
            LinearLayout sectionsContainer = (LinearLayout) mRootView.findViewById(R.id.sections_container);
            Collections.sort(pageData.getSections(), new Comparator<Section>() {
                @Override
                public int compare(Section o1, Section o2) {
                    return o1.getNumber() - o2.getNumber();
                }
            });
            sectionsContainer.removeAllViews();
            for(Section section: pageData.getSections()) {
                sectionsContainer.addView(getSection(section, false, null));
            }
        }

    }

    private Content getDefaultContent(boolean forContactPage) {
        Content content = new Content();
        if(forContactPage) {
            content.setType(Content.ContentType.ADDITIONAL_CONTACT_TYPE);
        } else {
            content.setType(Content.ContentType.TEXT);
        }
        return content;
    }

    private Section getDefaultSection() {
        Section section = new Section();
        List<Content> allContent = new ArrayList<>();
        allContent.add(getDefaultContent(false));
        return section;
    }

    private View getSection(final Section section, final boolean isSubSection, final Section parentSection) {
        LinearLayout sectionLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.section_layout, null);

        if(isEditMode) {
            sectionLayout.findViewById(R.id.editor_layout).setVisibility(View.VISIBLE);

            ImageButton addContentBtn = (ImageButton) sectionLayout.findViewById(R.id.add_content_btn);
            ImageButton addSubsectionBtn = (ImageButton) sectionLayout.findViewById(R.id.add_subsection_btn);
            ImageButton deleteSectionBtn = (ImageButton) sectionLayout.findViewById(R.id.delete_section_btn);

            if(pageData.isContactPage()) {
                addSubsectionBtn.setVisibility(View.GONE);
                deleteSectionBtn.setVisibility(View.GONE);
            }

            addContentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Content newContent = getDefaultContent(pageData.isContactPage());
                    if(section.getContents() == null) {
                        List<Content> allContent = new ArrayList<>();
                        allContent.add(newContent);
                        section.setContents(allContent);
                    } else {
                        section.getContents().add(newContent);
                    }
                    onResume();
                }
            });

            addSubsectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Section newSection = getDefaultSection();
                    if(section.getSubSections() == null) {
                        List<Section> subsections = new ArrayList<>();
                        subsections.add(newSection);
                        section.setSubSections(subsections);
                    } else {
                        section.getSubSections().add(newSection);
                    }
                    onResume();
                }
            });

            deleteSectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSubSection) {
                        parentSection.getSubSections().remove(section);
                    } else {
                        pageData.getSections().remove(section);
                    }
                    onResume();
                }
            });

        } else {
            sectionLayout.findViewById(R.id.editor_layout).setVisibility(View.GONE);
        }
        if (isSubSection) {
            sectionLayout.setPadding(0,0,0,0);
        }
        final EditText title = (EditText) sectionLayout.findViewById(R.id.section_title_et);
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    section.setTitle(title.getText().toString());
                }
            }
        });

        final EditText timeline = (EditText) sectionLayout.findViewById(R.id.section_timeline_et);
        timeline.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    section.setTimeline(timeline.getText().toString());
                }
            }
        });


        if ((section.getTitle() == null || TextUtils.isEmpty(section.getTitle())) && (section.getTimeline() == null || TextUtils.isEmpty(section.getTimeline()))) {
            if(isEditMode && !pageData.isContactPage()) {
                sectionLayout.findViewById(R.id.section_title_container).setVisibility(View.VISIBLE);
            } else {
                sectionLayout.findViewById(R.id.section_title_container).setVisibility(View.GONE);
            }
        } else {
            final ExpandableLayout sectionContentPart = (ExpandableLayout) sectionLayout.findViewById(R.id.expandableLayout);
            sectionLayout.findViewById(R.id.section_title_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionContentPart.toggle();
                }
            });

            if (section.getTitle() != null && !TextUtils.isEmpty(section.getTitle())) {
                if (isSubSection) {
                    title.setAllCaps(false);
                }
                title.setText(section.getTitle());

                if(isEditMode) {
                    title.setFocusable(true);
                } else {
                    title.setFocusable(false);
                }

            } else {
                if (isEditMode) {
                    sectionLayout.findViewById(R.id.section_title_et).setVisibility(View.VISIBLE);
                } else {
                    sectionLayout.findViewById(R.id.section_title_et).setVisibility(View.GONE);
                }
            }


            if (section.getTimeline() != null && !TextUtils.isEmpty(section.getTimeline())) {
                timeline.setText(section.getTimeline());
                if(isEditMode) {
                    timeline.setFocusable(true);
                } else {
                    timeline.setFocusable(false);
                }

            } else {
                if(isEditMode) {
                    sectionLayout.findViewById(R.id.section_timeline_et).setVisibility(View.VISIBLE);
                } else {
                    sectionLayout.findViewById(R.id.section_timeline_et).setVisibility(View.GONE);
                }
            }

        }

        final EditText summary = (EditText) sectionLayout.findViewById(R.id.section_summary_et);
        summary.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    section.setSummary(summary.getText().toString());
                }
            }
        });
        if(section.getSummary() == null && TextUtils.isEmpty(section.getSummary())) {
            if(isEditMode && !pageData.isContactPage()) {
                sectionLayout.findViewById(R.id.section_summary_container).setVisibility(View.VISIBLE);
            } else {
                sectionLayout.findViewById(R.id.section_summary_container).setVisibility(View.GONE);
            }
        } else {
            if(isEditMode) {
                sectionLayout.findViewById(R.id.section_summary).setVisibility(View.GONE);
                if(section.getSummary() != null) {
                    summary.setText(section.getSummary());
                }
            } else {
                sectionLayout.findViewById(R.id.section_summary_et).setVisibility(View.GONE);
                DocumentView summaryDV = (DocumentView) sectionLayout.findViewById(R.id.section_summary);
                summaryDV.setText((CharSequence)(section.getSummary()));
            }
        }

        RelativeLayout contentSection = (RelativeLayout) sectionLayout.findViewById(R.id.section_content_section);
        if (section.getContents() == null) {
            contentSection.setVisibility(View.GONE);
        } else {
            LinearLayout contentContainer  = (LinearLayout) contentSection.findViewById(R.id.section_content_container);
            for (Content content: section.getContents()) {
                contentContainer.addView(getContent(content, section));
            }
        }

        if (section.getSubSections() == null) {
            sectionLayout.findViewById(R.id.section_subsection_container).setVisibility(View.GONE);
        } else {
            LinearLayout subsections = (LinearLayout) sectionLayout.findViewById(R.id.section_subsection_container);
            for (Section subsection: section.getSubSections()) {
                subsections.addView(getSection(subsection, true, section));
            }
        }

        return sectionLayout;
    }

    private View getContent(final Content content, final Section parentSection) {

        LinearLayout contentLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.content_layout, null);
        if(isEditMode) {
            if(content.getType() != Content.ContentType.CONTACT_TYPE) {
                contentLayout.findViewById(R.id.editor_layout).setVisibility(View.VISIBLE);
            }
            ImageButton editContentBtn = (ImageButton) contentLayout.findViewById(R.id.edit_content_btn);
            ImageButton deleteContentBtn = (ImageButton) contentLayout.findViewById(R.id.delete_content_btn);

            if(content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
                editContentBtn.setVisibility(View.GONE);
            }


            editContentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(content.getType() == Content.ContentType.RATING) {
                        content.setType(Content.ContentType.TEXT);
                        content.setValue("");
                    } else {
                        content.setType(Content.ContentType.RATING);
                        content.setValue("");
                        content.setValue2("5");
                    }
                    onResume();
                }
            });

            deleteContentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentSection.getContents().remove(content);
                    onResume();
                }
            });

        } else {
            contentLayout.findViewById(R.id.editor_layout).setVisibility(View.GONE);
        }

        if(content.getType() == Content.ContentType.RATING) {
            contentLayout.findViewById(R.id.text_content_text).setVisibility(View.GONE);
            contentLayout.findViewById(R.id.contact_content_line).setVisibility(View.GONE);
            final EditText contentText = (EditText) contentLayout.findViewById(R.id.rating_content_text);
            contentText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        content.setValue(contentText.getText().toString());
                    }
                }
            });
            contentText.setText(content.getValue());

            RatingBar rating = (RatingBar) contentLayout.findViewById(R.id.rating_content_rating);
            rating.setRating(Integer.parseInt(content.getValue2()));
            if(isEditMode) {
               contentText.setFocusable(true);
               rating.setIsIndicator(false);
            } else {
                rating.setIsIndicator(true);
                contentText.setFocusable(false);
            }
        } else if(content.getType() == Content.ContentType.CONTACT_TYPE || content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
            contentLayout.findViewById(R.id.rating_content_line).setVisibility(View.GONE);
            contentLayout.findViewById(R.id.text_content_text).setVisibility(View.GONE);

            final EditText nameET = (EditText) contentLayout.findViewById(R.id.contact_content_name);
            final EditText textET = (EditText) contentLayout.findViewById(R.id.contact_content_text);
            nameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        content.setValue(nameET.getText().toString());
                    }
                }
            });

            textET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        content.setValue2(textET.getText().toString());
                    }
                }
            });



            if(content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
                if(!isEditMode) {
                    textET.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = textET.getText().toString();
                            if (url.startsWith("http://") || url.startsWith("https://")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }

                        }
                    });
                }


            }

            nameET.setText(content.getValue());
            textET.setText(content.getValue2());

            if(isEditMode) {
                textET.setFocusable(true);
                if(content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
                    nameET.setFocusable(true);
                } else {
                    nameET.setFocusable(false);
                }
            } else {
                textET.setFocusable(false);
                nameET.setFocusable(false);
                if(content.getType() == Content.ContentType.ADDITIONAL_CONTACT_TYPE) {
                    textET.setEnabled(true);
                }
            }

        } else {

            contentLayout.findViewById(R.id.rating_content_line).setVisibility(View.GONE);
            contentLayout.findViewById(R.id.contact_content_line).setVisibility(View.GONE);
            if(isEditMode) {
                final EditText textContent = (EditText) contentLayout.findViewById(R.id.text_content_et);
                textContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            content.setValue(textContent.getText().toString());
                        }
                    }
                });

                contentLayout.findViewById(R.id.text_content_text).setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
                textContent.setText(content.getValue());
            } else {

                contentLayout.findViewById(R.id.text_content_et).setVisibility(View.GONE);
                DocumentView textContent = (DocumentView) contentLayout.findViewById(R.id.text_content_text);
                textContent.setText((CharSequence)(content.getValue()));
            }

        }


        return contentLayout;
    }

}
