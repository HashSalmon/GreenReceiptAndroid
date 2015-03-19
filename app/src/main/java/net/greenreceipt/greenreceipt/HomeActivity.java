package net.greenreceipt.greenreceipt;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Util.DrawerAdapter;
import Util.DrawerItem;
import Util.DrawerOnItemClickListener;


public class HomeActivity extends ActionBarActivity{
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private String[] nav_options;
    private ListView drawer;
    private LinearLayout total;
    private LinearLayout monthly;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_options = getResources().getStringArray(R.array.nav_array);
        DrawerItem[] drawerItem = new DrawerItem[5];

        drawerItem[0] = new DrawerItem(R.drawable.ic_menu_home, nav_options[0]);
        drawerItem[1] = new DrawerItem(R.drawable.ic_action_new, nav_options[1]);
        drawerItem[2] = new DrawerItem(R.drawable.ic_action_labels, nav_options[2]);
        drawerItem[3] = new DrawerItem(R.drawable.ic_action_location_searching, nav_options[3]);
        drawerItem[4] = new DrawerItem(R.drawable.ic_action_settings, nav_options[4]);
        drawer = (ListView) findViewById(R.id.drawer);
        LayoutInflater lf = this.getLayoutInflater();
        View headerView = (View)lf.inflate(R.layout.drawer_header, drawer, false);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        email.setText(Model._currentUser.Email);
        drawer.addHeaderView(headerView);
        drawer.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem, 0));
        drawer.setOnItemClickListener(new DrawerOnItemClickListener(this, drawerLayout, drawer, 1));

//        drawer.setMainContent(R.layout.main_content);
//        drawer.setDrawerContent(R.layout.drawer_content);
//        drawer.setDrawerSize(R.dimen.navigation_drawer_width);
//        View main = View.inflate(this,R.layout.main_content,null);

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

        TextView date = (TextView) findViewById(R.id.date);
        TextView greeting = (TextView) findViewById(R.id.greeting);
        if(Model._currentUser!=null)
        greeting.setText("Welcome back, "+Model._currentUser.FirstName+"!");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = sdf.format(new Date());
        date.setText(currentDate);

        total = (LinearLayout) findViewById(R.id.totalReceipts);
        monthly = (LinearLayout) findViewById(R.id.monthly);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(HomeActivity.this,ListReceiptActivity.class);
                list.putExtra(Model.RECEIPT_FILTER,4);
                startActivity(list);
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(HomeActivity.this,ListReceiptActivity.class);
                list.putExtra(Model.RECEIPT_FILTER,2);
                startActivity(list);
            }
        });

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess()
            {
                TextView tTotal = (TextView) total.findViewById(R.id.detail);
                tTotal.setText(Model.getInstance().getTotalReceiptCount() + "\n$ " + new DecimalFormat("##.##").format(Model.getInstance().getReceiptsTotal()));
                TextView mTotal = (TextView) monthly.findViewById(R.id.mDetail);
                Pair currentMonth = Model.getInstance().getCurrentMonthReceiptCount();
                mTotal.setText(currentMonth.first + "\n$ " + new DecimalFormat("##.##").format(currentMonth.second));

            }

            @Override
            public void getReceiptFailed() {

            }
        });


        Model.getInstance().GetCategories();
        Model.getInstance().GetAllReceipt(Model.pageSize,1);
        Model.getInstance().GetReturnReceipts();
        Model.getInstance().changeDisplayReceipts(0);
//        summary.invalidateViews();

    }
}
