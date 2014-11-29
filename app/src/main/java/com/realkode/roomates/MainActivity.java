package com.realkode.roomates;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;

import static com.realkode.roomates.Helpers.Constants.EXPENSES_INDEX;
import static com.realkode.roomates.Helpers.Constants.FEED_INDEX;
import static com.realkode.roomates.Helpers.Constants.ME_INDEX;
import static com.realkode.roomates.Helpers.Constants.TASKS_INDEX;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = setUpActionBar();

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
                invalidateOptionsMenu();
            }
        });

        addTabsToActionBar(actionBar);

    }

    private void addTabsToActionBar(ActionBar actionBar) {
        for (int sectionIndex = 0; sectionIndex < sectionsPagerAdapter.getCount(); sectionIndex++) {
            assert actionBar != null;

            ActionBar.Tab tab = actionBar.newTab();
            tab.setText(sectionsPagerAdapter.getPageTitle(sectionIndex));
            tab.setTabListener(this);

            actionBar.addTab(tab);
        }
    }

    private ActionBar setUpActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        return actionBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int currentItem = viewPager.getCurrentItem();

        switch (currentItem) {
            case FEED_INDEX:
                getMenuInflater().inflate(R.menu.feed_menu, menu);
                break;
            case ME_INDEX:
                getMenuInflater().inflate(R.menu.me_menu, menu);
                break;
            case TASKS_INDEX:
                getMenuInflater().inflate(R.menu.tasks_menu, menu);
                break;
            case EXPENSES_INDEX:
                getMenuInflater().inflate(R.menu.expenses_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (!User.loggedInAndMemberOfAHousehold()) {
            return true;
        }

        if (itemId == R.id.action_refresh) {
            refreshFragment();
            return true;
        } else if (itemId == R.id.action_new) {
            newInFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newInFragment() {
        AddBehaviourFragment fragment = (AddBehaviourFragment) sectionsPagerAdapter.getItem(viewPager.getCurrentItem());
        fragment.add();
    }

    private void refreshFragment() {
        ToastMaker.makeShortToast(R.string.refreshing, this);
        RefreshableFragment fragment = (RefreshableFragment) sectionsPagerAdapter.getItem(viewPager.getCurrentItem());
        fragment.refreshFragment();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}
