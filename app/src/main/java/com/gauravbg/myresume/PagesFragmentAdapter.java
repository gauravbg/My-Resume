package com.gauravbg.myresume;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gauravbg.myresume.entities.MyResumeEntity;
import com.gauravbg.myresume.entities.Page;
import com.gauravbg.myresume.entities.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gauravbg on 10/6/17.
 */

public class PagesFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private Context context;
    private List<Page> pages = new ArrayList<>();
    private Profile profile;

    public PagesFragmentAdapter(Context context, FragmentManager fm, Map<String, MyResumeEntity> allData) {
        super(fm);
        for(Map.Entry<String, MyResumeEntity> entry: allData.entrySet()) {
            if(entry.getValue().getEntityType().equals(MyResumeEntity.PROFILE_TYPE)) {
                profile = (Profile)entry.getValue();
            } else if(entry.getValue().getEntityType().equals(MyResumeEntity.PAGE_TYPE)){
                pages.add((Page)entry.getValue());
                pages.add((Page)entry.getValue());
                pages.add((Page)entry.getValue());
            }
        }

        this.context = context;
    }



    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MyResumeEntity.PAGE_TYPE, pages.get(position));
        return Fragment.instantiate(context, PageFragment.class.getName(), bundle);
    }

    @Override
    public int getCount() {
        return pages.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return pages.get(position).getTitle();
    }
}
