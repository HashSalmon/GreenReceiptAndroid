package net.greenreceipt.greenreceipt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
    private BroadcastReceiver receiver;
    private int totalCount;
    private double totalAmt;
    private int monthCount;
    private double monthAmt;
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
        drawerItem[3] = new DrawerItem(R.drawable.ic_action_place, nav_options[3]);
        drawerItem[4] = new DrawerItem(R.drawable.ic_action_settings, nav_options[4]);
        drawer = (ListView) findViewById(R.id.drawer);
        LayoutInflater lf = this.getLayoutInflater();
        View headerView = (View)lf.inflate(R.layout.drawer_header, drawer, false);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        email.setText(Model.getInstance()._currentUser.Email);
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
        if(Model.getInstance()._currentUser!=null)
        greeting.setText("Welcome back, "+Model.getInstance()._currentUser.FirstName+"!");
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().equals(Model.GET_RECEIPT_COUNT_SUCCESS))
                {
                    totalCount = intent.getIntExtra("count",0);
                    TextView tTotal = (TextView) total.findViewById(R.id.detail);
                    tTotal.setText(totalCount+"\n$"+new DecimalFormat("##.##").format(totalAmt));
                }
                else if(intent.getAction().equals(Model.GET_RECEIPT_TOTAL_SUCCESS))
                {
                    totalAmt = intent.getDoubleExtra("total",0);
                    TextView tTotal = (TextView) total.findViewById(R.id.detail);
                    tTotal.setText(totalCount+"\n$"+new DecimalFormat("##.##").format(totalAmt));
                }
                else if(intent.getAction().equals(Model.GET_MONTHLY_RECEIPT_COUNT_SUCCESS))
                {
                    monthCount = intent.getIntExtra("count",0);
                    TextView mTotal = (TextView) monthly.findViewById(R.id.mDetail);
                    mTotal.setText(monthCount+ "\n$ " + new DecimalFormat("##.##").format(monthAmt));
                }
                else
                {
                    monthAmt = intent.getDoubleExtra("total",0);
                    TextView mTotal = (TextView) monthly.findViewById(R.id.mDetail);
                    mTotal.setText(monthCount+ "\n$ " + new DecimalFormat("##.##").format(monthAmt));
                }

            }
        };

        IntentFilter filter = new IntentFilter(Model.GET_RECEIPT_COUNT_SUCCESS);
        registerReceiver(receiver, filter);
        IntentFilter filter1 = new IntentFilter(Model.GET_RECEIPT_TOTAL_SUCCESS);
        registerReceiver(receiver, filter1);
        IntentFilter filter2 = new IntentFilter(Model.GET_MONTHLY_RECEIPT_COUNT_SUCCESS);
        registerReceiver(receiver, filter2);
        IntentFilter filter3 = new IntentFilter(Model.GET_MONTHLY_RECEIPT_TOTAL_SUCCESS);
        registerReceiver(receiver, filter3);



        Model.getInstance().GetCategories();
//        Model.getInstance().GetAllReceipt(Model.pageSize,1);
        Model.getInstance().GetReturnReceipts();
        Model.getInstance().GetTotalReceiptCount(this);
        Model.getInstance().GetTotalReceiptTotal(this);
        Model.getInstance().GetMonthReceiptCount(this);
        Model.getInstance().GetMonthReceiptTotal(this);
//        Model.getInstance().changeDisplayReceipts(0);
//        summary.invalidateViews();

    }



}
