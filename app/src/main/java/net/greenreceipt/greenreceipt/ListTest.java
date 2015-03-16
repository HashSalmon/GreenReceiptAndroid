package net.greenreceipt.greenreceipt;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.telerik.android.primitives.widget.sidedrawer.RadSideDrawer;
import com.telerik.widget.list.RadListView;


public class ListTest extends ActionBarActivity {
    RadListView list;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        Model.getInstance().GetAllReceipt();
        Model.getInstance().changeDisplayReceipts(4);
        list = (RadListView) findViewById(R.id.list);
        ListAdapter adapter = new ListAdapter(Model._displayReceipts);
        list.setAdapter(adapter);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
    protected void onResume() {
        super.onResume();
        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {
//                Model.getInstance().changeDisplayReceipts(filter);
            }

            @Override
            public void getReceiptFailed() {
                Helper.AlertBox(ListTest.this,"Error","Failed to retrieve data.\nPlease check your internet connection.");
            }
        });
    }
}
