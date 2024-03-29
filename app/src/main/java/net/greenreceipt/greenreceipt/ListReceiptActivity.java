package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;


public class ListReceiptActivity extends Activity implements ListAdapter{
    final static String RECEIPT_ID = "ReceiptId";
    ListView list;
    Spinner filters;
    Spinner sort;
    int filter;
    ProgressDialog spinner;
    String[] options = {
            "Date Range",
            "This Week",
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
        setContentView(R.layout.activity_list_receipt);
//        Toolbar actionBar = (Toolbar) findViewById(R.id.action_bar);
//        setSupportActionBar(actionBar);
//        Model.getInstance().GetAllReceipt();


        Model.getInstance().setGetReceiptListener(new Model.GetReceiptListener() {
            @Override
            public void getReceiptSuccess() {
                spinner.dismiss();
                list.invalidateViews();
                Model.getInstance().changeDisplayReceipts(filter);
            }

            @Override
            public void getReceiptFailed() {
                spinner.dismiss();
                Helper.AlertBox(ListReceiptActivity.this,"Error","Failed to retrieve data.\nPlease check your internet connection.");
            }
        });
        list = (ListView) findViewById(R.id.list);
//        ActionBar bar = getActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);

        list.setAdapter(this);
//        list.setAdapter(new ListReceiptAdapter(this));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent (getBaseContext(), ReceiptDetailActivity.class);
//				activity2.PutExtra ("MyData", "Data from Activity1");
                detail.putExtra(RECEIPT_ID, position);
                startActivity(detail);
            }
        });
        filters = (Spinner) findViewById(R.id.filters);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options);
        filters.setAdapter(adapter);
        filters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = position;
                //update result
                Model.getInstance().changeDisplayReceipts(filter);
                list.invalidateViews();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filter = 0;
            }
        });
        sort = (Spinner) findViewById(R.id.sort);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderBy);
        sort.setAdapter(orderAdapter);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Comparator<Receipt> comparator=null;
                    switch(position){
                        case 1:
                           comparator = new SortByStore();
                            break;
                        case 2:
                            comparator = new SortByDate();
                            break;
                        case 3:
                            comparator = new SortByDate();
                            comparator = Collections.reverseOrder(comparator);
                            break;
                        case 4:
                            comparator = new SortByTotal();
                            break;
                        case 5:
                            comparator = new SortByTotal();
                            comparator = Collections.reverseOrder(comparator);
                            break;
                    }
                if(comparator!=null) {
                    Model.getInstance().sortList(comparator);

                    list.invalidateViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        return Model.getInstance().getDisplayReceiptsCount();
    }

    @Override
    public Object getItem(int position) {
        return Model._displayReceipts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Model._displayReceipts.get(position).Id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Receipt r = Model.getInstance().getReceipt(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
//        Date today = new Date();
        View view = convertView;
        if(view == null)
            view = View.inflate(this, R.layout.listitem, null);
        TextView store = (TextView) view.findViewById(R.id.store);
        if(r.Store!=null)
            if(r.Store.Company != null) {
                store.setText(r.Store.Company.Name);
            }
        else
            store.setText("");
        TextView detail = (TextView) view.findViewById(R.id.detail);
//        if(r.CreatedDate == null)
        detail.setText(sdf.format(r.PurchaseDate)+"\n$"+new DecimalFormat("##.##").format(r.Total));
//        else
//            detail.setText(sdf.format(r.date) + "\n$" + new DecimalFormat("##.##").format(r.getTotal()));
        view.setBackgroundColor(Color.WHITE);
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
        Model.getInstance().GetAllReceipt();
        spinner = ProgressDialog.show(this, null, "Loading...");
        filter = getIntent().getIntExtra(Model.RECEIPT_FILTER,0);
//        Model.getInstance().changeDisplayReceipts(filter);
        if(filter >= 0 && filter < options.length)//it's with in the option range
        filters.setSelection(filter);
//        if(filter < 0)//refresh trigger
//        {
//            Model.getInstance().GetAllReceipt();
//            Model.getInstance().changeDisplayReceipts(0);
//        }

        list.invalidateViews();
    }
}
