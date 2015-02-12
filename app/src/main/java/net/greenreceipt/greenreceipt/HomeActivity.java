package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends Activity implements ListAdapter{
ListView summary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.profile);
        TextView date = (TextView) findViewById(R.id.date);
        TextView greeting = (TextView) findViewById(R.id.greeting);
        greeting.setText("Welcome back, "+Model._currentUser.FirstName+"!");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy");
        String currentDate = sdf.format(new Date());
//        if(now.)
//        {
//            if(today.Hour >= 6)
//            {
//                greeting.Text="Good Evening, John!";
//            }
//            else{
//                greeting.Text="Good Afternoon, John!";
//            }
//        }
//        else{
//            greeting.Text="Good Morning, John!";
//        }
        date.setText(currentDate);
        summary = (ListView) findViewById(R.id.summary);
        summary.setAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            case R.id.action_settings:
                Intent settings = new Intent(this , SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        Log.d("state","Resume");
        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {
                summary.invalidateViews();
            }

            @Override
            public void getReceiptFailed() {

            }
        });

        Model.getInstance().GetAllReceipt();
        Model.getInstance().changeDisplayReceipts(0);
        summary.invalidateViews();

    }
}
