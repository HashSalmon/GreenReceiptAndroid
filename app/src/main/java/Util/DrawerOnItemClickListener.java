package Util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.greenreceipt.greenreceipt.HomeActivity;
import net.greenreceipt.greenreceipt.ListReceiptActivity;
import net.greenreceipt.greenreceipt.MapActivity;
import net.greenreceipt.greenreceipt.Model;
import net.greenreceipt.greenreceipt.NewReceiptActivity;
import net.greenreceipt.greenreceipt.SettingsActivity;
import net.greenreceipt.greenreceipt.SummaryActivity;

/**
 * Created by Boya on 3/16/15.
 */
public class DrawerOnItemClickListener implements AdapterView.OnItemClickListener {
    Context context;
    DrawerLayout layout;
    ListView drawer;
    int currentPosition;
    public DrawerOnItemClickListener(Context context)
    {
        this.context = context;
    }
    public DrawerOnItemClickListener(Context context, DrawerLayout layout, ListView drawer,int position)
    {
        this.context = context;
        this.layout = layout;
        this.drawer = drawer;
        currentPosition = position;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int selected = currentPosition;
        switch (position){

            case 1:
                if(selected!=1) {
                    Intent homeIntent = new Intent(context, HomeActivity.class);
                    context.startActivity(homeIntent);
                }
                    break;

            case 2:
                if(selected!=2) {
                    Intent newIntent = new Intent(context, NewReceiptActivity.class);
                    context.startActivity(newIntent);
                }
                    break;


            case 3:
                if(selected!=3) {
                    Intent list = new Intent(context, ListReceiptActivity.class);
                    list.putExtra(Model.RECEIPT_FILTER, Model.SHOW_ALL);
                    context.startActivity(list);
                }
                    break;

            case 4:
                if(selected!=4) {
                    Intent summary = new Intent(context, SummaryActivity.class);
                    context.startActivity(summary);
                }
                    break;
            case 5:
                if(selected !=5){
                    Intent map = new Intent(context, MapActivity.class);
                    context.startActivity(map);
                }
                    break;
            case 6:
                if(selected!=6) {
                    Intent settings = new Intent(context, SettingsActivity.class);
                    context.startActivity(settings);
                }
                    break;


        }
        drawer.setItemChecked(position,true);
        layout.closeDrawers();

    }
}
