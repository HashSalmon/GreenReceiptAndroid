package net.greenreceipt.greenreceipt;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import Util.DrawerAdapter;
import Util.DrawerItem;
import Util.DrawerOnItemClickListener;


public class SettingsActivity extends ActionBarActivity {
    Button logout;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private String[] nav_options;
    private ListView drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        View content = root.getChildAt(0);
//        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.activity_settings, null);
//
//        root.removeAllViews();
//        toolbarContainer.addView(content);
//        root.addView(toolbarContainer);
//
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        setContentView(R.layout.activity_settings);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
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
        drawer.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem, 5));
        drawer.setOnItemClickListener(new DrawerOnItemClickListener(this,drawerLayout,drawer,6));

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,
                tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

//        Toolbar actionBar = (Toolbar) findViewById(R.id.action_bar);
//        setSupportActionBar(actionBar);
        logout = (Button) findViewById(R.id.logoutButton);
        if(logout!=null)
        {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().Logout(getApplicationContext());
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            super.onBackPressed();
        }
    }
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
//        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
//        tb.setTitleTextColor(Color.WHITE);
//
////        actionBar.setHomeButtonEnabled(true);
////        actionBar.setDisplayHomeAsUpEnabled(true);
////        toggle = new ActionBarDrawerToggle(this,drawerLayout,
////                tb,
////                R.string.navigation_drawer_open,
////                R.string.navigation_drawer_close);
////        drawerLayout.setDrawerListener(toggle);
////        toggle.syncState();
//        root.addView(tb, 0); // insert at top
//        tb.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

}
