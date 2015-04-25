package net.greenreceipt.greenreceipt;

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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import Util.DrawerAdapter;
import Util.DrawerItem;
import Util.DrawerOnItemClickListener;

/**
 * Created by Boya on 4/18/15.
 */
public class MapActivity extends ActionBarActivity
{

    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private String[] nav_options;
    private ListView drawer;
    GoogleMap map;
    @Override
    protected void onStart() {
        super.onStart();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("View on Map");
        //setup drawer and actionbar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_options = getResources().getStringArray(R.array.nav_array);
        DrawerItem[] drawerItem = new DrawerItem[nav_options.length];

        drawerItem[0] = new DrawerItem(R.drawable.ic_menu_home, nav_options[0]);
        drawerItem[1] = new DrawerItem(R.drawable.ic_action_new, nav_options[1]);
        drawerItem[2] = new DrawerItem(R.drawable.ic_action_labels, nav_options[2]);
        drawerItem[3] = new DrawerItem(R.drawable.ic_action_place, nav_options[3]);
        drawerItem[4] = new DrawerItem(R.drawable.ic_action_location_searching, nav_options[4]);
        drawerItem[5] = new DrawerItem(R.drawable.ic_action_settings, nav_options[5]);
        drawer = (ListView) findViewById(R.id.drawer);
        LayoutInflater lf = this.getLayoutInflater();
        View headerView = (View)lf.inflate(R.layout.drawer_header, drawer, false);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        email.setText(Model.getInstance()._currentUser.Email);
        drawer.addHeaderView(headerView);
        drawer.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem, 4));
        drawer.setOnItemClickListener(new DrawerOnItemClickListener(this, drawerLayout, drawer, 5));

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
        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {
                if (map != null) {
                    //put marker on map
                    LatLng marker = null;
                    for(Receipt r:Model.getInstance()._receipts){
                        marker = new LatLng(r.Latitude, r.Longitude);
                        Marker TP = map.addMarker(new MarkerOptions().position(marker).title(r.Store.Company.Name+" "+r.Total));


                    }
                    if(marker!=null)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,8));
                }
            }

            @Override
            public void getReceiptFailed(String error) {

            }
        });
        Model.getInstance().GetAllReceipt(Model.pageSize,1);
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


}
