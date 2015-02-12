package net.greenreceipt.greenreceipt;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ReceiptDetailActivity extends Activity implements ListAdapter {

    ListView list;
    Receipt receipt;
    Switch alert;
    int mYear;
    int mMonth;
    int mDay;
    ProgressDialog spinner;
    boolean deleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        int id = getIntent().getIntExtra(ListReceiptActivity.RECEIPT_ID,-1);
        receipt = Model.getInstance().getReceipt(id);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(this);
        Model.getInstance().setOnDeleteReceiptListener(new Model.OnDeleteReceiptListener() {
            @Override
            public void deleteSuccess() {
                spinner.dismiss();
                deleted = true;
                Intent list = new Intent(getBaseContext(),ListReceiptActivity.class);
                list.putExtra(Model.RECEIPT_FILTER,-1);
                startActivity(list);
            }

            @Override
            public void deleteFailed(String error) {
                spinner.dismiss();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.receipt_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.delete:
                spinner = ProgressDialog.show(this,null,"Deleting...");
                Model.getInstance().DeleteReceipt(receipt.Id);
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
        return receipt.getItemCount()+ 8;
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
        if(position == 0)//header
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText(receipt.Store.Company.Name);
            TextView detail = (TextView) view.findViewById(R.id.detail);
            Date date = receipt.PurchaseDate;
            detail.setText(sdf.format(date)+"\n$"+new DecimalFormat("##.##").format(receipt.Total));
            view.setBackgroundColor(Color.WHITE);
        }

        else if(position == 1)//alert
        {
            view =View.inflate(this, R.layout.icon_item, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_action_time);
            final TextView text = (TextView) view.findViewById(R.id.category);
            alert = (Switch) view.findViewById(R.id.returnSwitch);
            alert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    receipt.ReturnReminder = isChecked;
                    if(isChecked) {//if turn on alert, force user to pick a date
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dpd = new DatePickerDialog(ReceiptDetailActivity.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        text.setText("Return Date: " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;
                                        receipt.ReturnDate = new Date(mYear, mMonth, mDay);
                                    }
                                }, mYear, mMonth, mDay);
                        dpd.setIcon(R.drawable.ic_action_time);
                        dpd.setTitle("Date");
                        dpd.show();

                    }
                    else
                    {
                        text.setText("Return Alert:");
                    }

                }
            });

            if(alert!=null) {
                alert.setVisibility(View.VISIBLE);
                text.setFocusable(false);
                if(receipt.ReturnReminder) {//there's a return date
                    String returnDate = sdf.format(receipt.ReturnDate);
                    text.setText("Return Alert Date: "+returnDate);

                }
                else
                {
                    text.setText("Return Alert: ");
                }
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dpd = new DatePickerDialog(ReceiptDetailActivity.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        text.setText("Return Date: "+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;
                                        receipt.ReturnDate = new Date(mYear,mMonth,mDay);
                                    }
                                }, mYear, mMonth, mDay);
                        dpd.setIcon(R.drawable.ic_action_time);
                        dpd.setTitle("Date");
                        dpd.show();
                    }
                });
//                alert.setChecked(receipt.ReturnReminder);
            }


        }
        else if(position == getCount()-6)//SubTotal
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView subtotal = (TextView) view.findViewById(R.id.store);
            subtotal.setText("Subtotal");
            TextView detail = (TextView) view.findViewById(R.id.detail);
            Date date = receipt.CreatedDate;
            detail.setText("$"+receipt.SubTotal);
            subtotal.setTextSize(10);
            detail.setTextSize(10);
            subtotal.setTextColor(Color.GRAY);
            detail.setTextColor(Color.GRAY);
        }
        else if(position == getCount()-5)//Tax
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView tax = (TextView) view.findViewById(R.id.store);
            tax.setText("Tax");
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setText("$"+receipt.Tax);
            tax.setTextSize(10);
            detail.setTextSize(10);
            tax.setTextColor(Color.GRAY);
            detail.setTextColor(Color.GRAY);
        }
        else if(position == getCount() -4)//total
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText("Total");
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setText("$" + new DecimalFormat("##.##").format(receipt.Total));
            store.setTextSize(10);
            detail.setTextSize(10);
            store.setTextColor(Color.GRAY);
            detail.setTextColor(Color.GRAY);
        }

        else if(position == getCount()-3)//card
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView card = (TextView) view.findViewById(R.id.store);
            card.setText("Payment: "+getCardType(receipt.CardType));
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setText(receipt.LastFourCardNumber);
            card.setTextSize(10);
            detail.setTextSize(10);
            card.setTextColor(Color.GRAY);
            detail.setTextColor(Color.GRAY);
        }
        else if(position == getCount()-2)//discount
        {
            view =View.inflate(this, R.layout.listitem, null);
            TextView discount = (TextView) view.findViewById(R.id.store);
            discount.setText("Discount");
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setText("$" + new DecimalFormat("##.##").format(receipt.Discount));
            discount.setTextSize(10);
            detail.setTextSize(10);
            discount.setTextColor(Color.GRAY);
            detail.setTextColor(Color.GRAY);
        }
        else if(position == getCount()-1)//barcode
        {
            ImageView barcode = new ImageView(this);
            barcode.setImageResource(R.drawable.barcode);
            view = barcode;

        }
        else{
            Item item = receipt.getItem(position - 2);

            view =View.inflate(this, R.layout.listitem, null);
            view.setPadding(5, 0, 0, 5);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText(item.ItemName);
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setText("$"+item.Price);
            view.setBackgroundColor(Color.WHITE);
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
    private String getCardType(int cardType)
    {
        String result = "Unknown";
        switch (cardType) {
            case (0):
                result= "Unkown";
            break;
            case(1):
                result= "Amex";
            break;
            case(2):
                result="Visa";
            break;
            case(3):
                result= "Mastercard";
            break;
            case(4):
                result= "Discover";
            case(5):
                result = "Cash";

        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.invalidateViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!deleted)
        Model.getInstance().AddReceipt(receipt);//update if not deleted
    }
}
