package net.greenreceipt.greenreceipt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.telerik.widget.list.LoadOnDemandBehavior;
import com.telerik.widget.list.RadListView;
import com.telerik.widget.list.SwipeExecuteBehavior;

import Util.DrawerAdapter;
import Util.DrawerItem;
import Util.DrawerOnItemClickListener;
import Util.Helper;


public class ListReceiptActivity extends ActionBarActivity {
    final static String RECEIPT_ID = "ReceiptId";
    RadListView list;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    ListAdapter adapter;
    Spinner filters;
    Spinner sort;
    int filter;
    private String[] nav_options;
    private ListView drawer;
    ProgressDialog spinner;
    String[] options = {
            "Date Range",
            "This Month",
            "This Year",
            "Show All",
            "Upcoming Returns"
    };
    String[] orderBy = {
            "Sort By",
            "Store Name",
            "Date(Ascending)",
            "Date(Descending)",
            "Total(lowest to highest)",
            "Total(highest to lowest)"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);


        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {
//                Model.getInstance().changeDisplayReceipts(filter);
                spinner.dismiss();
                Model.getInstance().changeDisplayReceipts(filter);
                adapter.setItems(Model.getInstance()._displayReceipts);
//                adapter.notifyDataSetChanged();
                adapter.notifyLoadingFinished();
            }

            @Override
            public void getReceiptFailed(String error) {
                spinner.dismiss();
                Helper.AlertBox(ListReceiptActivity.this, "Error", error);
            }
        });
        Model.getInstance().resetCurrentPage();
        Model.getInstance().GetAllReceipt(Model.pageSize,1);
    spinner = ProgressDialog.show(this, null, "Loading...");
        list = (RadListView) findViewById(R.id.list);
        adapter = new ListAdapter(Model.getInstance()._displayReceipts);
        list.setAdapter(adapter);
        list.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int i, MotionEvent motionEvent) {
                Intent detail = new Intent (getBaseContext(), ReceiptDetailActivity.class);
//				activity2.PutExtra ("MyData", "Data from Activity1");
                detail.putExtra(RECEIPT_ID, i);
                startActivity(detail);
            }

            @Override
            public void onItemLongClick(int i, MotionEvent motionEvent) {

            }
        });
        SwipeExecuteBehavior swipeExecuteBehavior = new SwipeExecuteBehavior();
        list.addBehavior(swipeExecuteBehavior);
        LoadOnDemandBehavior loadOnDemandBehavior = new LoadOnDemandBehavior();
        list.addBehavior(loadOnDemandBehavior);

        LoadOnDemandBehavior.LoadOnDemandListener loadOnDemandListener =
                new LoadOnDemandBehavior.LoadOnDemandListener() {

                    @Override
                    public void onLoadStarted() {
                        Model.getInstance().GetAllReceipt(Model.pageSize,1);
                    }

                    @Override
                    public void onLoadFinished() {
                    }
                };

        loadOnDemandBehavior.addListener(loadOnDemandListener);
//        loadOnDemandBehavior.setMode(LoadOnDemandBehavior.LoadOnDemandMode.AUTOMATIC);
//        loadOnDemandBehavior.setMaxRemainingItems(5);

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
        drawer.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem, 2));
        drawer.setOnItemClickListener(new DrawerOnItemClickListener(this,drawerLayout,drawer,3));

        Resources resources = getResources();
        ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_accent_color));
        currentBgColor = bgColorPrimary;
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.list_receipt);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(currentBgColor);
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,
                tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        filters = (Spinner) findViewById(R.id.filters);
        final ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options);
        filters.setAdapter(filterAdapter);

        filters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = position;
                //update result
                Model.getInstance().changeDisplayReceipts(filter);
                adapter.setItems(Model.getInstance()._displayReceipts);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filter = 0;
            }
        });
        filter = getIntent().getIntExtra(Model.RECEIPT_FILTER,0);
//        Model.getInstance().changeDisplayReceipts(filter);
        if(filter >= 0 && filter < options.length)//it's with in the option range
            filters.setSelection(filter);
//        sort = (Spinner) findViewById(R.id.sort);
//        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderBy);
//        sort.setAdapter(orderAdapter);
//        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Comparator<Receipt> comparator=null;
//                switch(position){
//                    case 1:
//                        comparator = new SortByStore();
//                        break;
//                    case 2:
//                        comparator = new SortByDate();
//                        break;
//                    case 3:
//                        comparator = new SortByDate();
//                        comparator = Collections.reverseOrder(comparator);
//                        break;
//                    case 4:
//                        comparator = new SortByTotal();
//                        break;
//                    case 5:
//                        comparator = new SortByTotal();
//                        comparator = Collections.reverseOrder(comparator);
//                        break;
//                }
//                if(comparator!=null) {
//                    Model.getInstance().sortList(comparator);
//
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.list_receipt, menu);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();


        filter = getIntent().getIntExtra(Model.RECEIPT_FILTER,0);
//        Model.getInstance().changeDisplayReceipts(filter);
        if(filter >= 0 && filter < options.length)//it's with in the option range
            filters.setSelection(filter);
        adapter.notifyDataSetChanged();
    }
}
