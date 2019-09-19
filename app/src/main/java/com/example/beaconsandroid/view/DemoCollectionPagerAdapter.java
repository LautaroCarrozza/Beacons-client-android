package com.example.beaconsandroid.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.beaconsandroid.Poi;

import java.util.ArrayList;
import java.util.List;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
    List<Poi> pages = new ArrayList<>();

    public DemoCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        args.putString(DemoObjectFragment.ARG_OBJECT, pages.get(i).getHtmlContent());
        fragment.setArguments(args);

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
        pages.remove(poi);
        notifyDataSetChanged();
    }

}