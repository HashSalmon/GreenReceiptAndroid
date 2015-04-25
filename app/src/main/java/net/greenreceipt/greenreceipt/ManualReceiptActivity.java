package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
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
import java.io.FileInputStream;
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
    int Id = 0;
    boolean switchOn=true;
    ArrayList<String> picturePaths;
    ReceiptImage[] images;
    ArrayList<Integer> imageIds = new ArrayList<>();
    private ColorDrawable currentBgColor;
    private ActionBar actionBar;

    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;
    private String resultUrl = "result.txt";
    Bitmap bitmap;
    Receipt original;

//    int paymentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_receipt);
        if(picturePaths == null)
        picturePaths = new ArrayList<>();
        //set up action bar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(Model.getInstance().categories != null)
        {
            for(Category c : Model.getInstance().categories)
                categoryList.add(c.Name);
        }
        itemsPurchased = (TextView) findViewById(R.id.itemsPurchased);

        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        lastFour = (EditText) findViewById(R.id.lastFour);
        storeName = (EditText) findViewById(R.id.store);
        alertSwitch = (Switch) findViewById(R.id.alertSwitch);

        //popup date selector when switched on
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
        //handles add receipt
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
                            //new receipt
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
//                                receipt.picturePath = ManualReceiptActivity.this.picturePaths;
                                receipt.ReceiptItems=items;
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
                            //edit receipt
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
//                                receipt.picturePath = ManualReceiptActivity.this.picturePaths;
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
                            //pictures
                            Model.getInstance().AddReceipt(receipt,images);
                            if (bitmap != null && !bitmap.isRecycled())
                            {
                                bitmap.recycle();
                                bitmap = null;
                                System.gc();
                            }




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
                                    //handle delete
                                    int index = itemContainer.indexOfChild((View) v.getParent().getParent());
                                    itemContainer.removeViewAt(index);
                                    items.remove(index);
                                }
                            });
                            //create item and add to list and view
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

                //set category picker values
                if(categoryList.size()==0)
                {
                    categoryList.add("");
                }
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
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Intent all = new Intent(ManualReceiptActivity.this,PictureListActivity.class);
                all.putStringArrayListExtra("paths",picturePaths);
                if(original!=null)
                all.putExtra("id",original.Id);
                all.putExtra("imageIds",imageIds);
//                if(images!=null)
//                all.putExtra("images",gson.toJson(images,ReceiptImage[].class));
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
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir+ File.separator + "image"+picturePaths.size()+".jpg" );

        return mediaFile;
    }

    /**
     * handle take picture finished
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(imageFilePath.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=8;
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = BitmapFactory.decodeFile(imageFilePath.getPath(),o2);
                        image1.setImageBitmap(Helper.RotateBitmap(bitmap, 90));
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = BitmapFactory.decodeFile(imageFilePath.getPath(),o2);
                        image1.setImageBitmap(Helper.RotateBitmap(bitmap, 180));
                        break;
                    // etc.
                }
//                image1.setImageURI(imageFilePath);
                break;
            case SELECT_FILE: {
                Uri imageUri = data.getData();

                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cur = managedQuery(imageUri, projection, null, null, null);
                cur.moveToFirst();
                picturePaths.add(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)));
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

    /**
     * validation for receipt
     * @param store
     * @param date
     * @param tax
     * @param items
     * @return
     */
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
        //register callback
        Model.getInstance().setAddReceiptListener(new Model.AddReceiptListener() {
            @Override
            public void addReceiptSuccess() {
                if(spinner!=null)
                spinner.dismiss();
                for(String s:picturePaths)
                {
                    File f = new File(s);
                    f.delete();
                }
                Intent intent = new Intent(getBaseContext(),ListReceiptActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra(Model.RECEIPT_FILTER,Model.SHOW_ALL);
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
        Model.getInstance().setAddReceiptListener(null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Model.getInstance().setAddReceiptListener(null);


//        if (bitmap != null && !bitmap.isRecycled())
//        {
//            bitmap.recycle();
//            bitmap = null;
//            System.gc();
//        }
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);

    }

    /**
     * Prefill the fields
     */
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
        picturePaths = getIntent().getStringArrayListExtra("path");
        if(picturePaths == null)
            picturePaths = new ArrayList<>();
//        String imageString = getIntent().getStringExtra("images");
//        if(imageString!=null)
//            images = gson.fromJson(imageString,ReceiptImage[].class);
        if(receiptString != null) {
            try {
                original = gson.fromJson(receiptString, Receipt.class);
                imageIds = getIntent().getIntegerArrayListExtra("imageIds");
                if(imageIds==null || imageIds.isEmpty()) {//load from server
                    Model.getInstance().setGetReceiptImageListener(new Model.GetReceiptImageListener() {
                        @Override
                        public void onGetImageSuccess(ReceiptImage[] images) {
                            ManualReceiptActivity.this.images = images;
                            if (picturePaths.isEmpty() && images.length > 0) {
                                byte[] decodedString = Base64.decode(images[0].Base64Image, Base64.NO_WRAP);
                                BitmapFactory.Options o2 = new BitmapFactory.Options();
                                o2.inSampleSize=8;
                                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length,o2);
                                float degree = Exif.getOrientation(decodedString);
                                bitmap = Helper.RotateBitmap(bitmap, degree);
                                image1.setImageBitmap(bitmap);
                            }
                            for (ReceiptImage image : images) {
                                imageIds.add(image.Id);
                            }
                        }

                        @Override
                        public void onGetImageFailed(String error) {

                        }
                    });
                    Model.getInstance().GetReceiptImages(original.Id);
                }
                else//load from cache
                {
                    byte[] imageBytes = loadImageBytesFromCache(original.Id,imageIds.get(0));
                    float degree = Exif.getOrientation(imageBytes);
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize=8;
                    bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length,o2);
                    bitmap = Helper.RotateBitmap(bitmap, degree);
                    image1.setImageBitmap(bitmap);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//                image = new ReceiptImage();
//                if(images!=null && images.length>0)
//                {
//                    byte[] decodedString = Base64.decode(images[0].Base64Image,Base64.NO_WRAP);
//                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    image1.setImageBitmap(bitmap);
//                }
                if(picturePaths.size()>0) {
                    byte[] imageBytes = Model.getInstance().getByteArrayFromImage(picturePaths.get(0));
//                    image.Base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
//                    image.FileName = "image.jpg";
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize=8;
                    bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,o2);
                    float degree = Exif.getOrientation(imageBytes);
                    bitmap = Helper.RotateBitmap(bitmap, degree);
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


                    itemContainer.addView(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int finalJ = itemContainer.indexOfChild(v);
                            editItem(finalJ);
                        }
                    });
                    itemsPurchased.setTextColor(Color.GRAY);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * make items editable
     * @param itemIndex
     */
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
    public void captureImageFromSdCard( View view ) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, SELECT_FILE);
    }
    public void captureImageFromCamera( View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);
    }
    private byte[] loadImageBytesFromCache(int ReceiptId, int ImageId)
    {
        try
        {
            String filename = "Receipt"+ReceiptId+"Image"+ ImageId+".jpg";
            File image = new File(getCacheDir(),filename);
            byte[] imageBytes = new byte[(int)image.length()];
            FileInputStream fis = new FileInputStream(image);
            fis.read(imageBytes,0,(int)image.length());
            fis.close();
            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
