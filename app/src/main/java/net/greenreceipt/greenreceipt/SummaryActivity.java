package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;


public class SummaryActivity extends Activity {

    PagerSlidingTabStrip tabs;
    ViewPager pager;
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
