package com.example.beaconsandroid.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.beaconsandroid.Poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
    List<Poi> pages = new ArrayList<>();
    Map<Poi,Fragment> fragments = new HashMap<>();
    public DemoCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        args.putString(DemoObjectFragment.ARG_OBJECT, pages.get(i).getHtmlContent());
        fragment.setArguments(args);
        fragments.put(pages.get(i),fragment);

        return fragment;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages.get(position).getTitle();
    }

    public void addPoi(Poi poi){
        pages.add(poi);
        notifyDataSetChanged();
    }
    public void removePoi(Poi poi){
        Fragment fragment = fragments.get(poi);
        if(fragment != null) {
            fragment.onDestroy();
        }
        pages.remove(poi);
        fragments.remove(poi);
        notifyDataSetChanged();
    }

}