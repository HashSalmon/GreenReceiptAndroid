package net.greenreceipt.greenreceipt;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.telerik.android.primitives.widget.sidedrawer.RadSideDrawer;
import com.telerik.widget.list.RadListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeActivity extends ActionBarActivity implements ListAdapter{
    private RadListView summary;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RadSideDrawer drawer = (RadSideDrawer) findViewById(R.id.drawer);
//        drawer.setMainContent(R.layout.main_content);
        drawer.setDrawerContent(R.layout.drawer_content);
        drawer.setDrawerSize(R.dimen.navigation_drawer_width);
//        View main = View.inflate(this,R.layout.main_content,null);
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

        TextView date = (TextView) findViewById(R.id.date);
        TextView greeting = (TextView) findViewById(R.id.greeting);
        if(Model._currentUser!=null)
        greeting.setText("Welcome back, "+Model._currentUser.FirstName+"!");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = sdf.format(new Date());
        date.setText(currentDate);
        summary = (RadListView) findViewById(R.id.summary);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        toggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        if (toggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        switch (item.getItemId())
        {
            case R.id.new_receipt:
                Intent newIntent = new Intent(this , NewReceiptActivity.class);
                startActivity(newIntent);
                return true;
            case R.id.viewReceipts:
//                Intent list = new Intent(this , ListReceiptActivity.class);
//                list.putExtra(Model.RECEIPT_FILTER,4);
//                startActivity(list);
                Intent list = new Intent(this , ListTest.class);
                list.putExtra(Model.RECEIPT_FILTER,4);
                startActivity(list);
                return true;
            case R.id.view_summary:
                Intent summary = new Intent(this , SummaryActivity.class);
                startActivity(summary);

                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this , SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void ViewReceipt(View view)
    {
        Intent viewReceipt = new Intent(this,ListTest.class);
        startActivity(viewReceipt);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(position == 0)
        {
            view = View.inflate(this, R.layout.listitem, null);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText("Total receipts");
            TextView detail = (TextView) view.findViewById((R.id.detail));
            detail.setText(Model.getInstance().getTotalReceiptCount()+"\n$ "+new DecimalFormat("##.##").format(Model.getInstance().getReceiptsTotal()));
            view.setBackgroundColor(Color.WHITE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent list = new Intent(getBaseContext(),ListReceiptActivity.class);
                    list.putExtra(Model.RECEIPT_FILTER,4);
                    startActivity(list);
                }
            });
        }
        else
        {
            view = View.inflate(this, R.layout.listitem, null);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText("This month");
            Pair currentMonth = Model.getInstance().getCurrentMonthReceiptCount();
            TextView detail = (TextView) view.findViewById((R.id.detail));
            detail.setText(currentMonth.first+"\n$ "+new DecimalFormat("##.##").format(currentMonth.second));
            view.setBackgroundColor(Color.WHITE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent list = new Intent(getBaseContext(),ListReceiptActivity.class);
                    list.putExtra(Model.RECEIPT_FILTER,2);
                    startActivity(list);
                }
            });
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {

                ArrayList<Integer> positions = new ArrayList<>();
                positions.add(0);
                positions.add(1);
                Dashboard_adapter adapter = new Dashboard_adapter(positions);
                summary.setAdapter(adapter);

                summary.addItemClickListener(new RadListView.ItemClickListener() {
                    @Override
                    public void onItemClick(int i, MotionEvent motionEvent) {
                        if(i == 0)
                        {
                            Intent all = new Intent(HomeActivity.this,ListTest.class);
                            all.putExtra(Model.RECEIPT_FILTER,4);
                            startActivity(all);
                        }
                    }

                    @Override
                    public void onItemLongClick(int i, MotionEvent motionEvent) {

                    }
                });
            }

            @Override
            public void getReceiptFailed() {

            }
        });

        Model.getInstance().GetCategories();
        Model.getInstance().GetAllReceipt();
        Model.getInstance().GetReturnReceipts();
        Model.getInstance().changeDisplayReceipts(0);
//        summary.invalidateViews();

    }
}
