package com.realkode.roomates;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.realkode.roomates.Expenses.ExpensesFragment;
import com.realkode.roomates.Feed.FeedFragment;
import com.realkode.roomates.Me.MeFragment;
import com.realkode.roomates.Tasks.Fragment.TaskListFragment;

import java.util.Locale;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private MainActivity mainActivity;
    private FeedFragment feedFragment;
    private MeFragment meFragment;
    private TaskListFragment taskListFragment;
    private ExpensesFragment expensesFragment;

    public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
        super(fm);
        this.mainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (feedFragment == null) {
                feedFragment = new FeedFragment();
            }
            return feedFragment;
        } else if (position == 1) {
            if (meFragment == null) {
                meFragment = new MeFragment();
            }
            return meFragment;
        } else if (position == 2) {
            if (taskListFragment == null) {
                taskListFragment = new TaskListFragment();
            }
            return taskListFragment;
        } else if (position == 3) {
            if (expensesFragment == null) {
                expensesFragment = new ExpensesFragment();
            }
            return expensesFragment;
        } else {
            throw new IllegalArgumentException("Invalid section number");
        }
    }

    @Override
    public int getCount() {
        return MainActivity.NUMBER_OF_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mainActivity.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mainActivity.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mainActivity.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return mainActivity.getString(R.string.title_section4).toUpperCase();
        }
        return null;
    }
}
