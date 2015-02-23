package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hotchemi.stringpicker.StringPicker;
import hotchemi.stringpicker.StringPickerDialog;


public class ManualReceiptActivity extends FragmentActivity{
    private static final String TAG = StringPickerDialog.class.getSimpleName();
    LinearLayout itemContainer;
    EditText itemName;
    EditText itemPrice;
    EditText storeName;
    EditText date;
    EditText tax;
    TextView returnDate;
    Button add;
    DatePickerDialog picker;
    int mYear;
    int mMonth;
    int mDay;
    Switch alertSwitch;
    ProgressDialog spinner;
    ArrayList<Item> items = new ArrayList<Item>();
    Spinner payment;
    String error;
    TextView itemsPurchased;
    List<String> categoryList = new ArrayList<String>();
    StringPicker categoryPicker;

//    int paymentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_receipt);
        itemsPurchased = (TextView) findViewById(R.id.itemsPurchased);
//        category = (TextView) findViewById(R.id.category);




        final ImageView alertIcon = (ImageView) findViewById(R.id.alertIcon);
        alertIcon.setImageResource(R.drawable.ic_action_time);
//        ImageView categoryIcon = (ImageView) findViewById(R.id.categoryIcon);
//        categoryIcon.setImageResource(R.drawable.ic_action_labels);
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        storeName = (EditText) findViewById(R.id.store);
        alertSwitch = (Switch) findViewById(R.id.alertSwitch);
        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
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
                    dpd.setTitle("Date");
                    dpd.show();
                }
                else
                {
                    returnDate.setText("Return Alert");
                }
            }
        });
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
        returnDate = (TextView) findViewById(R.id.returnAlert);
        add = (Button) findViewById(R.id.addButton);
        if(add!=null)
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkReceipt(storeName, date, tax, items))//there's error
                    {
                        Helper.AlertBox(ManualReceiptActivity.this,"Error", error);
                    }
                    else {
                        double subtotal = 0;
                        for (Item i : items) {
                            subtotal += i.Price;
                        }

                        try {
                            spinner = ProgressDialog.show(ManualReceiptActivity.this, null, "Processing...");
                            spinner.setCanceledOnTouchOutside(true);
                            Receipt receipt = new Receipt();
                            Store store = new Store();
                            store.Company.Name = storeName.getText().toString();
                            receipt.Store = store;
                            receipt.Tax = Double.parseDouble(tax.getText().toString());
                            receipt.SubTotal = subtotal;
                            receipt.CreatedDate = new Date();
                            receipt.PurchaseDate = new Date(date.getText().toString());
                            ;
                            receipt.ReceiptItems.addAll(items);
                            receipt.ReturnReminder = alertSwitch.isChecked();
                            receipt.Total = receipt.SubTotal + receipt.Tax;
                            receipt.CardType = payment.getSelectedItemPosition();
                            if (alertSwitch.isChecked())
                                receipt.ReturnDate = new Date(returnDate.getText().toString());
                            Pair<Double, Double> location = Model.getInstance().getCurrentLocation(ManualReceiptActivity.this);
                            receipt.Longitude = location.first;
                            receipt.Latitude = location.second;


                            Model.getInstance().AddReceipt(receipt);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        ImageView icon = (ImageView) findViewById(R.id.icon);


        //add item dialog
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
                            item.CreatedDate = new Date();
                            item.Category = Model.categories[categoryPicker.getCurrent()];
                            items.add(item);
                            itemContainer.addView(view);
                            itemName.setText("");
                            itemPrice.setText("");
                            itemsPurchased.setTextColor(Color.GRAY);

                        }
                    }
                });
                builder.setTitle("Add Item");
                View add = View.inflate(getBaseContext(),R.layout.add_item,null);
                builder.setView(add);
                itemName = (EditText) add.findViewById(R.id.itemName);
                itemPrice = (EditText) add.findViewById(R.id.price);
                categoryPicker = (StringPicker) add.findViewById(R.id.category);
                categoryPicker.setValues(categoryList);
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
        ImageView paymentIcon = (ImageView) findViewById(R.id.paymentIcon);
        paymentIcon.setImageResource(R.drawable.ic_action_labels);
        payment = (Spinner) findViewById(R.id.payment);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Model.PAYMENT_TYPES);
        payment.setAdapter(adapter);
//        payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                paymentType = position;
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
//    private Pair<Double,Double> getCurrentLocation()
//    {
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        Pair<Double,Double> result = new Pair<Double,Double>(longitude,latitude);
//        return result;
//    }
    private boolean checkReceipt(EditText store, EditText date, EditText tax, ArrayList items)
    {
        boolean result = true;
        error = "";

        if(store.getText().toString().isEmpty()) {
            result = false;
            error = "Store is required!";
            store.setHintTextColor(Color.RED);
        }
        if(date.getText().toString().isEmpty())
        {
            result = false;
            date.setHintTextColor(Color.RED);
            if(!error.isEmpty())
                error+="\nDate is required!";
            else
                error = "Date is required!";
        }
        if(tax.getText().toString().isEmpty())
        {
            result = false;
            tax.setHintTextColor(Color.RED);
            if(!error.isEmpty())
                error+="\nTax is required!";
            else
                error = "Tax is required!";
        }
        if(items.size()==0)
        {
            result = false;
            itemsPurchased.setTextColor(Color.RED);
            if(!error.isEmpty())
                error+="\nYou must enter at least one item!";
            else
                error = "You must enter at least one item!";
        }

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.getInstance().setAddReceiptListener(new Model.AddReceiptListener() {
            @Override
            public void addReceiptSuccess() {
                spinner.dismiss();
                Intent intent = new Intent(getBaseContext(),ListReceiptActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra(Model.RECEIPT_FILTER,4);
                startActivity(intent);
                finish();
            }
            @Override
            public void addReceiptFailed(String error) {
                spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ManualReceiptActivity.this);
                builder.setTitle("Error");
                builder.setMessage(error);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Model.getInstance().setGetCategoryListener(new Model.GetCategoryListener() {
            @Override
            public void onGetCategorySuccess() {
                for(Category c: Model.categories)
                {
                    categoryList.add(c.Name);
                }
            }

            @Override
            public void onGetCateogryFailed(String error) {

            }
        });
        Model.getInstance().GetCategories();
    }

    //    @Override
//    public void onClick(String s) {
//        category.setText(s);
//    }
}
