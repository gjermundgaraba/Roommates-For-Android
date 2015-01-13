package com.gjermundbjaanes.apps.roommates;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.gjermundbjaanes.apps.roommates.helpers.Constants.EXPENSES_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.FEED_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.ME_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.TASKS_INDEX;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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

            final MenuItem refreshMenuItem = item;
            refreshMenuItem.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshMenuItem.setEnabled(true);
                }
            }, 2000);

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
