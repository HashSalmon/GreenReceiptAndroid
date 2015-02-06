package net.greenreceipt.greenreceipt;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class SummaryActivity extends FragmentActivity {

    FragmentPagerAdapter adapter;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        adapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if(i == 0 )
                   return new TrendingReportFragment();
                else if(i == 1)
                    return new CategoryReportFragment();
                else
                    return new BudgetFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };
        bar.addTab(bar.newTab()
                .setText("Trending\nReport")
                .setTabListener(tabListener));
        bar.addTab(bar.newTab()
                .setText("Category\nReport")
                .setTabListener(tabListener));
        bar.addTab(bar.newTab()
                .setText("Budget")
                .setTabListener(tabListener));

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
