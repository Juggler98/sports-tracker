package com.example.sportstracker.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.sportstracker.fragments.StatsTabFragment;

import java.util.ArrayList;

public class StatsPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<String> pageTitles = new ArrayList<>();

    public StatsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return StatsTabFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

    @Override
    public int getCount() {
        return pageTitles.size();
    }

    public void addPage(String title) {
        pageTitles.add(title);
    }

}