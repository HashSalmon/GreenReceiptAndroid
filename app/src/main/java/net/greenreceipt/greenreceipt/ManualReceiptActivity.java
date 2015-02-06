package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hotchemi.stringpicker.StringPickerDialog;


public class ManualReceiptActivity extends FragmentActivity{
    private static final String TAG = StringPickerDialog.class.getSimpleName();
    LinearLayout itemContainer;
    EditText itemName;
    EditText itemPrice;
    EditText storeName;
    EditText date;
    EditText tax;
    Button add;
    DatePickerDialog picker;
    int mYear;
    int mMonth;
    int mDay;
    Switch alertSwitch;
    ProgressDialog spinner;
    ArrayList<Item> items = new ArrayList<Item>();
    TextView category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_receipt);

//        category = (TextView) findViewById(R.id.category);
        Model.getInstance().setAddReceiptListener(new Model.AddReceiptListener() {
            @Override
            public void addReceiptSuccess() {
                spinner.dismiss();
                Intent intent = new Intent(getBaseContext(),ListReceiptActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();

            }

            @Override
            public void addReceiptFailed() {
                spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ManualReceiptActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Receipt add failed!");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        final ImageView alertIcon = (ImageView) findViewById(R.id.alertIcon);
        alertIcon.setImageResource(R.drawable.ic_action_time);
//        ImageView categoryIcon = (ImageView) findViewById(R.id.categoryIcon);
//        categoryIcon.setImageResource(R.drawable.ic_action_labels);
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        storeName = (EditText) findViewById(R.id.store);
        alertSwitch = (Switch) findViewById(R.id.alertSwitch);
        date = (EditText) findViewById(R.id.date);
        date.setFocusable(false);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(ManualReceiptActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                            {
                                date.setText((monthOfYear + 1) + "/"+ dayOfMonth + "/" + year);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                dpd.setIcon(R.drawable.ic_action_time);
                dpd.setTitle("Date");
                dpd.show();
            }
        });
        tax = (EditText) findViewById(R.id.tax);
        final TextView returnDate = (TextView) findViewById(R.id.returnAlert);
        add = (Button) findViewById(R.id.addButton);
        if(add!=null)
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double subtotal = 0;
                    for(Item i : items)
                    {
                        subtotal+=i.Price;
                    }
                    try {
                        spinner = ProgressDialog.show(ManualReceiptActivity.this,null,"Working...");
                        Receipt receipt = new Receipt();
                        Store store = new Store();
                        store.Company.Name = storeName.getText().toString();
                        receipt.Store = store;
                        receipt.Tax = Double.parseDouble(tax.getText().toString());
                        receipt.SubTotal = subtotal;
                        receipt.CreatedDate = new Date();
                        receipt.PurchaseDate = new Date(date.getText().toString());;
                        receipt.ReceiptItems.addAll(items);
                        receipt.ReturnReminder = alertSwitch.isChecked();
                        receipt.Total = receipt.SubTotal+receipt.Tax;
                        if (alertSwitch.isChecked())
                            receipt.ReturnDate = new Date(returnDate.getText().toString());

//                        Model.getInstance().addReceipt(receipt);
                        Model.getInstance().AddReceipt(receipt);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        ImageView icon = (ImageView) findViewById(R.id.icon);

        icon.setImageResource(R.drawable.ic_action_new);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManualReceiptActivity.this);
                builder.setPositiveButton("Add",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //make sure there's valid info to add
                        if(!itemName.getText().toString().isEmpty() && !itemPrice.getText().toString().isEmpty())
                        {
                            View view = View.inflate(getBaseContext(), R.layout.listitem, null);
                            TextView name = (TextView) view.findViewById(R.id.store);
                            name.setText(itemName.getText().toString());
                            TextView price = (TextView) view.findViewById(R.id.detail);
                            price.setText(itemPrice.getText().toString());
                            view.setBackgroundColor(Color.WHITE);
                            Item item = new Item();
                            item.ItemName = itemName.getText().toString();
                            item.Price = Double.parseDouble(itemPrice.getText().toString());
                            items.add(item);
                            itemContainer.addView(view);
                            itemName.setText("");
                            itemPrice.setText("");
                        }
                    }
                });
                builder.setTitle("Add Item");
                View add = View.inflate(getBaseContext(),R.layout.add_item,null);
                builder.setView(add);
                itemName = (EditText) add.findViewById(R.id.itemName);
                itemPrice = (EditText) add.findViewById(R.id.price);
                AlertDialog addDialog = builder.create();
                addDialog.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(ManualReceiptActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                            {
                                returnDate.setText((monthOfYear + 1) + "/"+ dayOfMonth + "/" + year);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                dpd.setIcon(R.drawable.ic_action_time);
                dpd.setTitle("Set Return Date");
                dpd.show();
            }
        });

//        category.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StringPickerDialog dialog = new StringPickerDialog();
//                Bundle bundle = new Bundle();
//                String[] values = new String[] {"a","b","c","d","e","f"};
//                bundle.putStringArray(getString(R.string.string_picker_dialog_values), values);
//                dialog.setArguments(bundle);
//                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//                dialog.show(getSupportFragmentManager(),"");
//            }
//        });

    }

//    @Override
//    public void onClick(String s) {
//        category.setText(s);
//    }
}
