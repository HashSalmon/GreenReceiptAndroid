package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.telerik.android.primitives.widget.sidedrawer.RadSideDrawer;


public class SummaryActivity extends ActionBarActivity {

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
//        Toolbar actionBar = (Toolbar) findViewById(R.id.action_bar);
//        setSupportActionBar(actionBar);
        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            String[] TITLES = {"Trending Report", "Category Report", "Budget"};

            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return new TrendingReportFragment();
                else if (position == 1)
                    return new CategoryReportFragment();
                else
                    return new BudgetFragment();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RadSideDrawer drawer = (RadSideDrawer) findViewById(R.id.drawer);
//        drawer.setMainContent(R.layout.main_content);
        drawer.setDrawerContent(R.layout.drawer_content);
        Resources resources = getResources();
        ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_title_background));
        ColorDrawable bgColorSecondary = new ColorDrawable(resources.getColor(R.color.secondary_title_background));
        currentBgColor = bgColorPrimary;
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(currentBgColor);
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,
                tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds ReceiptItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.new_receipt:
                Intent newIntent = new Intent(this , NewReceiptActivity.class);
                startActivity(newIntent);
                return true;
            case R.id.viewReceipts:
                Intent list = new Intent(this , ListReceiptActivity.class);
                startActivity(list);
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this , SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
