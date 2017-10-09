package com.gauravbg.myresume;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;

/**
 * Created by gauravbg on 8/6/17.
 */

public class PageFragment extends Fragment {

    private Page pageData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_fragment_layout, container, false);
        if(getArguments() != null) {
            this.pageData = getArguments().getParcelable(MyResumeEntity.PAGE_TYPE);
        }
        TextView tv =  (TextView) rootView.findViewById(R.id.tv);
        tv.setText("This is Page: " + pageData.getTitle());
        return rootView;
    }
}
