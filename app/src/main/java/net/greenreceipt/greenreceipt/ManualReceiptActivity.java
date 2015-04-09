package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Util.Helper;
import hotchemi.stringpicker.StringPicker;
import hotchemi.stringpicker.StringPickerDialog;


public class ManualReceiptActivity extends ActionBarActivity implements View.OnClickListener{
    private static final String TAG = StringPickerDialog.class.getSimpleName();
    LinearLayout itemContainer;
    EditText itemName;
    EditText itemPrice;
    EditText storeName;
    EditText date;
    EditText tax;
    EditText lastFour;
    TextView returnDate;
    Button add;
    DatePickerDialog picker;
    int mYear;
    int mMonth;
    int mDay;
    Switch alertSwitch;
    ProgressDialog spinner;
    List<Item> items = new ArrayList<Item>();
    Spinner payment;
    String error;
    TextView itemsPurchased;
    List<String> categoryList = new ArrayList<String>();
    StringPicker categoryPicker;
    ImageView image1;
    ImageView camera;
    String mode = "Add";
    int imageCount=0;
    int Id = 0;
    boolean switchOn=true;
    ArrayList<String> picturePaths;

    private ColorDrawable currentBgColor;
    private ActionBar actionBar;

    private final int TAKE_PICTURE = 0;
    private String resultUrl = "result.txt";
//    private String picturePath;
    Bitmap bitmap;
    Receipt original;

//    int paymentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_receipt);
        if(picturePaths == null)
        picturePaths = new ArrayList<String>();
        Resources resources = getResources();
        ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_accent_color));
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

        if(Model.getInstance().categories != null)
        {
            for(Category c : Model.getInstance().categories)
                categoryList.add(c.Name);
        }
        itemsPurchased = (TextView) findViewById(R.id.itemsPurchased);
//        category = (TextView) findViewById(R.id.category);





//        ImageView categoryIcon = (ImageView) findViewById(R.id.categoryIcon);
//        categoryIcon.setImageResource(R.drawable.ic_action_labels);
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        lastFour = (EditText) findViewById(R.id.lastFour);
        storeName = (EditText) findViewById(R.id.store);
        alertSwitch = (Switch) findViewById(R.id.alertSwitch);

        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked && switchOn)
                {
                    switchOn = true;
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
                    dpd.setTitle("Return Date");
                    dpd.show();
                }
                else if(!isChecked && switchOn)
                {
                    switchOn = true;
                    returnDate.setText("Return Alert");
                }
                else
                {
                    switchOn = true;
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
                dpd.setTitle("Return Date");
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
                        Helper.AlertBox(ManualReceiptActivity.this, "Error", error);
                    }
                    else {
                        double subtotal = 0;
                        for (Item i : items) {
                            subtotal += i.Price;
                        }

                        try {
                            Receipt receipt = new Receipt();
                            List<ReceiptImage> images = new ArrayList<ReceiptImage>();
                            if(mode.equals("Add")) {
                                spinner = ProgressDialog.show(ManualReceiptActivity.this, null, "Processing...");
                                spinner.setCanceledOnTouchOutside(true);
                                receipt.Id = Id;
                                Store store = new Store();
                                store.Company.Name = storeName.getText().toString();
                                receipt.Store = store;
                                receipt.Tax = Double.parseDouble(tax.getText().toString());
                                receipt.SubTotal = subtotal;
                                receipt.CreatedDate = new Date();
                                receipt.PurchaseDate = new Date(date.getText().toString());
                                receipt.picturePath = ManualReceiptActivity.this.picturePaths;
                                receipt.ReceiptItems.addAll(items);
                                receipt.ReturnReminder = alertSwitch.isChecked();
                                receipt.Total = receipt.SubTotal + receipt.Tax;
                                receipt.CardType = payment.getSelectedItemPosition();
                                receipt.LastFourCardNumber = lastFour.getText().toString();
                                if (alertSwitch.isChecked())
                                    receipt.ReturnDate = new Date(returnDate.getText().toString());
                                Pair<Double, Double> location = Model.getInstance().getCurrentLocation(ManualReceiptActivity.this);
                                receipt.Longitude = location.first;
                                receipt.Latitude = location.second;

                                if (picturePaths.size()>0) {
                                    for(String s:picturePaths) {
                                        ReceiptImage image = new ReceiptImage();
                                        byte[] imageBytes = Model.getInstance().getByteArrayFromImage(s);
                                        image.Base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                                        image.FileName = "image.jpg";
                                        images.add(image);
                                    }

                                }
                            }
                            else
                            {
                                receipt = original;
                                spinner = ProgressDialog.show(ManualReceiptActivity.this, null, "Processing...");
                                spinner.setCanceledOnTouchOutside(true);

                                Store store = new Store();
                                store.Company.Name = storeName.getText().toString();
                                receipt.Store = store;
                                receipt.Tax = Double.parseDouble(tax.getText().toString());
                                receipt.SubTotal = subtotal;
                                receipt.PurchaseDate = new Date(date.getText().toString());
                                receipt.picturePath = ManualReceiptActivity.this.picturePaths;
                                receipt.ReceiptItems=items;
                                receipt.ReturnReminder = alertSwitch.isChecked();
                                receipt.Total = receipt.SubTotal + receipt.Tax;
                                receipt.CardType = payment.getSelectedItemPosition();
                                receipt.LastFourCardNumber = lastFour.getText().toString();
                                if (alertSwitch.isChecked())
                                    receipt.ReturnDate = new Date(returnDate.getText().toString());

                                if (picturePaths.size()>0) {
                                    for(String s:picturePaths) {
                                        ReceiptImage image = new ReceiptImage();
                                        byte[] imageBytes = Model.getInstance().getByteArrayFromImage(s);
                                        image.Base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                                        image.FileName = "image.jpg";
                                        images.add(image);
                                    }

                                }
                            }
                            Model.getInstance().AddReceipt(receipt,images);



                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        ImageView icon = (ImageView) findViewById(R.id.icon);


        //add item dialog
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
                            View view = View.inflate(getBaseContext(), R.layout.purchaseitem, null);
                            TextView name = (TextView) view.findViewById(R.id.store);
                            name.setText(itemName.getText().toString());
                            TextView price = (TextView) view.findViewById(R.id.detail);
                            price.setText(itemPrice.getText().toString());
                            view.setBackgroundColor(Color.WHITE);
                            ImageButton remove = (ImageButton) view.findViewById(R.id.remove);
                            remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int index = itemContainer.indexOfChild((View) v.getParent().getParent());
                                    itemContainer.removeViewAt(index);
                                    items.remove(index);
                                }
                            });
                            final Item item = new Item();
                            item.ItemName = itemName.getText().toString();
                            item.Price = Double.parseDouble(itemPrice.getText().toString());
                            item.CreatedDate = new Date();
                            item.Category = Model.getInstance().categories[categoryPicker.getCurrent()];
                            items.add(item);
                            itemContainer.addView(view);
                            itemName.setText("");
                            itemPrice.setText("");
                            itemsPurchased.setTextColor(Color.GRAY);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int finalJ = itemContainer.indexOfChild(v);
                                    editItem(finalJ);
                                }
                            });

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
//        ImageView paymentIcon = (ImageView) findViewById(R.id.paymentIcon);
        payment = (Spinner) findViewById(R.id.payment);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Model.PAYMENT_TYPES);
        payment.setAdapter(adapter);


        image1 = (ImageView) findViewById(R.id.image1);
        camera = (ImageView) findViewById(R.id.camera);
        camera.setOnClickListener(this);

        TextView viewall = (TextView) findViewById(R.id.viewAll);
        viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent all = new Intent(ManualReceiptActivity.this,PictureListActivity.class);
                all.putStringArrayListExtra("paths",picturePaths);
                startActivity(all);
            }
        });
        preFill();

    }

    private  Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
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
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image"+picturePaths.size()+".jpg" );

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
                if(picturePaths == null)
                    picturePaths = new ArrayList<>();
                picturePaths.add(imageFilePath.getPath());
                image1.setImageURI(imageFilePath);
                break;

        }

        //Remove output file
        deleteFile(resultUrl);

//        Intent results = new Intent( this, ResultsActivity.class);
//        results.putExtra("IMAGE_PATH", imageFilePath);
//        results.putExtra("RESULT_PATH", resultUrl);
//        startActivity(results);
    }

    private boolean checkReceipt(EditText store, EditText date, EditText tax, List items)
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
                if(spinner!=null)
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
    protected void onPause() {

        super.onPause();
        if(bitmap != null)
            bitmap.recycle();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);

    }
    public void preFill()
    {
//        ReceiptImage image = null;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        String receiptString = getIntent().getStringExtra("receipt");
        String currentMode = getIntent().getStringExtra("mode");
        if(currentMode!=null) {
            mode = currentMode;
            add.setText(mode);
        }
        picturePaths = getIntent().getStringArrayListExtra("images");
        if(picturePaths == null)
            picturePaths = new ArrayList<>();
        if(receiptString != null) {
            try {
                original = gson.fromJson(receiptString, Receipt.class);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//                image = new ReceiptImage();
                if(picturePaths.size()>0) {
                    byte[] imageBytes = Model.getInstance().getByteArrayFromImage(picturePaths.get(0));
//                    image.Base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
//                    image.FileName = "image.jpg";
                    bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    image1.setImageBitmap(bitmap);
                }

                Id = original.Id;
                storeName.setText(original.Store.Company.Name);
                tax.setText(original.Tax + "");
                date.setText(sdf.format(original.PurchaseDate));
                if(original.ReturnReminder)
                {
                    returnDate.setText(sdf.format(original.ReturnDate));
                    switchOn = false;
                    alertSwitch.setChecked(original.ReturnReminder);
                }



                lastFour.setText(original.LastFourCardNumber);
                payment.setSelection(original.CardType);
                items = original.ReceiptItems;
                for(int j = 0; j < items.size(); j++){
                    final int finalJ = j;
                    Item i = items.get(finalJ);
                    View view = View.inflate(getBaseContext(), R.layout.purchaseitem, null);
                    final TextView name = (TextView) view.findViewById(R.id.store);
                    name.setText(i.ItemName);
                    TextView price = (TextView) view.findViewById(R.id.detail);
                    price.setText(i.Price + "");
                    ImageButton delete = (ImageButton) view.findViewById(R.id.remove);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = itemContainer.indexOfChild((View) v.getParent().getParent());
                            itemContainer.removeViewAt(index);
                            items.remove(index);
                        }
                    });
                    view.setBackgroundColor(Color.WHITE);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editItem(finalJ);
                        }
                    });
                    itemContainer.addView(view);
                    itemsPurchased.setTextColor(Color.GRAY);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void editItem(int itemIndex)
    {
        final int finalJ = itemIndex;
        AlertDialog.Builder builder = new AlertDialog.Builder(ManualReceiptActivity.this);
        builder.setPositiveButton("Done",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make sure there's valid info to add
                if(!itemName.getText().toString().isEmpty() && !itemPrice.getText().toString().isEmpty())
                {
                    View view = itemContainer.getChildAt(finalJ);
                    TextView name = (TextView) view.findViewById(R.id.store);
                    name.setText(itemName.getText().toString());
                    TextView price = (TextView) view.findViewById(R.id.detail);
                    price.setText(itemPrice.getText().toString());
                    view.setBackgroundColor(Color.WHITE);
                    Item item = new Item();
                    item.ItemName = itemName.getText().toString();
                    item.Price = Double.parseDouble(itemPrice.getText().toString());
                    item.CreatedDate = new Date();
                    item.Category = Model.getInstance().categories[categoryPicker.getCurrent()];
                    items.set(finalJ, item);
                    itemName.setText("");
                    itemPrice.setText("");
                    itemsPurchased.setTextColor(Color.GRAY);
                    itemContainer.invalidate();

                }
            }
        });
        Item i = items.get(finalJ);
        builder.setTitle("Edit Item");
        View add = View.inflate(getBaseContext(),R.layout.add_item,null);
        builder.setView(add);
        itemName = (EditText) add.findViewById(R.id.itemName);
        itemName.setText(i.ItemName);
        itemPrice = (EditText) add.findViewById(R.id.price);
        itemPrice.setText(i.Price+"");
        categoryPicker = (StringPicker) add.findViewById(R.id.category);

        categoryPicker.setValues(categoryList);
        if(categoryList.size()==0)
        {
            categoryList.add("");
        }
        if(i.Category!=null)
        {
            int index = Model.getInstance().getCategoryIndex(i.Category.Name);
            if(index>-1)
                categoryPicker.setCurrent(index);
        }

        AlertDialog addDialog = builder.create();
        addDialog.show();
    }

}
