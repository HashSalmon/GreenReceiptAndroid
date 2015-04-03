package net.greenreceipt.greenreceipt;

import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import Util.DrawerAdapter;
import Util.DrawerItem;
import Util.DrawerOnItemClickListener;


public class SummaryActivity extends ActionBarActivity {

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private String[] nav_options;
    private ListView drawer;
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
        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_options = getResources().getStringArray(R.array.nav_array);
        DrawerItem[] drawerItem = new DrawerItem[5];

        drawerItem[0] = new DrawerItem(R.drawable.ic_menu_home, nav_options[0]);
        drawerItem[1] = new DrawerItem(R.drawable.ic_action_new, nav_options[1]);
        drawerItem[2] = new DrawerItem(R.drawable.ic_action_labels, nav_options[2]);
        drawerItem[3] = new DrawerItem(R.drawable.ic_action_place, nav_options[3]);
        drawerItem[4] = new DrawerItem(R.drawable.ic_action_settings, nav_options[4]);
        drawer = (ListView) findViewById(R.id.drawer);
        LayoutInflater lf = this.getLayoutInflater();
        View headerView = (View)lf.inflate(R.layout.drawer_header, drawer, false);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        email.setText(Model.getInstance()._currentUser.Email);
        drawer.addHeaderView(headerView);
        drawer.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem, 3));
        drawer.setOnItemClickListener(new DrawerOnItemClickListener(this,drawerLayout,drawer,4));


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,
                tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }
}
