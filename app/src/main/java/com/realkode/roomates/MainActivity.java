package com.realkode.roomates;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.realkode.roomates.Expenses.ExpensesFragment;
import com.realkode.roomates.Feed.FeedFragment;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.Me.MeFragment;
import com.realkode.roomates.Tasks.TaskListFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager            mViewPager;
    private Menu                 optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the four
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                invalidateOptionsMenu();

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.

        int currentItem = mViewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                getMenuInflater().inflate(R.menu.feed_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.me_menu, menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.tasks_menu, menu);
                break;
            case 3:
                getMenuInflater().inflate(R.menu.expenses_menu, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
//            setRefreshActionButtonState(true);

            System.out.println("Refreshing?");
            ToastMaker.makeShortToast("Refreshing...", this);
            RefreshableFragment fragment = (RefreshableFragment)mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
            fragment.refreshFragment();
            return true;
        }
        else if (id == R.id.action_new) {
            int currentItem = mViewPager.getCurrentItem();
            switch (currentItem) {
                case 0:
                    // Add Note
                    FeedFragment feedFragment = (FeedFragment)mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                    feedFragment.startCreateNewNoteDialog();
                    break;
                case 1:
                    break;
                case 2:
                    TaskListFragment taskListFragment = (TaskListFragment)mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                    taskListFragment.startCreateNewTaskListDialog();
                    break;
                case 3:
                    ExpensesFragment expensesFragment = (ExpensesFragment)mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                    expensesFragment.createNewExpense();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    // Making refreshbutton spin when loading
    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FeedFragment feedFragment;
        private MeFragment meFragment;
        private TaskListFragment taskListFragment;
        private ExpensesFragment expensesFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Creating the fragment instances.
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (feedFragment == null) {
                    feedFragment = new FeedFragment();
                }
                return feedFragment;
            }
            else if (position == 1) {
                if (meFragment == null) {
                    meFragment = new MeFragment();
                }
                return meFragment;
            }
            else if (position == 2) {
                if (taskListFragment == null) {
                    taskListFragment = new TaskListFragment();
                }
                return taskListFragment;
            }
            else if (position == 3) {
                if (expensesFragment == null) {
                    expensesFragment = new ExpensesFragment();
                }
                return expensesFragment;
            }
            else {
                throw new IllegalArgumentException("Invalid section number");
            }
        }
        // Number of fragments that should be present
        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        // Set the title for each fragment
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return getString(R.string.title_section4).toUpperCase();
            }
            return null;
        }
    }

}