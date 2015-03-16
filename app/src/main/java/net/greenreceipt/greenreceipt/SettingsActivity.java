package net.greenreceipt.greenreceipt;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.telerik.android.primitives.widget.sidedrawer.RadSideDrawer;


public class SettingsActivity extends ActionBarActivity {
    Button logout;
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        ActionBar bar = getActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);
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
            case R.id.view_summary:
                Intent summary = new Intent(this , SummaryActivity.class);
                startActivity(summary);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
