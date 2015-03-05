package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hotchemi.stringpicker.StringPicker;
import hotchemi.stringpicker.StringPickerDialog;


public class ManualReceiptActivity extends FragmentActivity implements View.OnClickListener{
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
    ImageView image1;
    LinearLayout imageContainer;
    int imageCount=0;

    private final int TAKE_PICTURE = 0;
    private String resultUrl = "result.txt";



//    int paymentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_receipt);
        if(Model.categories != null)
        {
            for(Category c : Model.categories)
                categoryList.add(c.Name);
        }
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
                if(categoryList.size()==0)
                {
                    categoryList.add("");
                }
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

        imageContainer = (LinearLayout) findViewById(R.id.image_container);

        image1 = (ImageView) findViewById(R.id.image1);
        image1.setOnClickListener(this);


    }

    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GreenReceipt");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg" );

        return mediaFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        Uri imageFilePath = null;

        switch (requestCode) {
            case TAKE_PICTURE:
                imageFilePath = getOutputMediaFileUri();
                if(imageCount < 5) {
                    ImageView newView = new ImageView(this);
                    newView.setImageURI(imageFilePath);
                    imageContainer.addView(newView);
                    imageCount++;
                }


                break;

        }

        //Remove output file
        deleteFile(resultUrl);

//        Intent results = new Intent( this, ResultsActivity.class);
//        results.putExtra("IMAGE_PATH", imageFilePath);
//        results.putExtra("RESULT_PATH", resultUrl);
//        startActivity(results);
    }

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

    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);

    }

    //    @Override
//    public void onClick(String s) {
//        category.setText(s);
//    }
}
