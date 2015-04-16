package net.greenreceipt.greenreceipt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;


public class ReceiptDetailActivity extends ActionBarActivity implements ListAdapter {

    ListView list;
    Receipt receipt;
    Switch alert;
    int mYear;
    int mMonth;
    int mDay;
    ProgressDialog spinner;
    boolean deleted = false;
    boolean editing = false;
    private ActionBar actionBar;
    private ColorDrawable currentBgColor;
    private BroadcastReceiver receiver;
    Bitmap decodedByte;
    ImageView picture;
    ReceiptImage[] receiptImages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        tb.inflateMenu(R.menu.receipt_detail);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        ActionBar bar = getActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);
        int id = getIntent().getIntExtra(ListReceiptActivity.RECEIPT_ID,-1);
        receipt = Model.getInstance().getReceipt(id);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(this);
        Model.getInstance().setOnDeleteReceiptListener(new Model.OnDeleteReceiptListener() {
            @Override
            public void deleteSuccess() {
                if(spinner!=null)
                    spinner.dismiss();
                deleted = true;
                Intent list = new Intent(getBaseContext(),ListReceiptActivity.class);
                list.putExtra(Model.RECEIPT_FILTER,4);
                startActivity(list);
                finish();
            }

            @Override
            public void deleteFailed(String error) {
                spinner.dismiss();

            }
        });
        Model.getInstance().setGetReceiptImageListener(new Model.GetReceiptImageListener() {
            @Override
            public void onGetImageSuccess(ReceiptImage[] images) {
                if(images.length > 0) {
                    receiptImages = images;
                    byte[] decodedString = Base64.decode(images[0].Base64Image,Base64.NO_WRAP);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    picture.setImageBitmap(decodedByte);
                    picture.invalidate();

                    list.invalidateViews();
                }
            }

            @Override
            public void onGetImageFailed(String error) {

            }
        });
        Model.getInstance().GetReceiptImages(receipt.Id);
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
            case R.id.edit:
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Intent intent = new Intent(this,ManualReceiptActivity.class);
                intent.putExtra("receipt",gson.toJson(receipt,Receipt.class));
                intent.putExtra("mode","Edit");
//                intent.putExtra("images",gson.toJson(receiptImages,ReceiptImage[].class));
                startActivity(intent);
                editing = true;
                finish();
                return true;
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
            view =View.inflate(this, R.layout.receipt_header, null);
            TextView store = (TextView) view.findViewById(R.id.store);
            store.setText(receipt.Store.Company.Name);
            TextView detail = (TextView) view.findViewById(R.id.detail);
            Date date = receipt.PurchaseDate;
            detail.setText(sdf.format(date)+"\n$"+new DecimalFormat("##.##").format(receipt.Total));
            view.setBackgroundColor(Color.WHITE);
            picture = (ImageView) view.findViewById(R.id.image);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent all = new Intent(ReceiptDetailActivity.this,PictureListActivity.class);
                    all.putExtra("id",receipt.Id);
                    startActivity(all);
                }
            });
            if(decodedByte!=null)
                picture.setImageBitmap(decodedByte);
            else
            {

            }
//            if(receipt.Store.Company.Name.equals("ARBY'S")) {
//                image.setImageResource(R.drawable.arby);
//                image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ReceiptDetailActivity.this,FullScreenImageActivity.class);
//                        intent.putExtra("resource",R.drawable.arby);
//                        startActivity(intent);
//                    }
//                });
//            }
//            else
//            {
//                image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ReceiptDetailActivity.this,FullScreenImageActivity.class);
//                        intent.putExtra("resource",R.drawable.ic_action_camera);
//                        startActivity(intent);
//                    }
//                });
//            }
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
                    alert.setChecked(false);
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
            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
            view =View.inflate(this, R.layout.barcode, null);
            ImageView barcode = (ImageView) view.findViewById(R.id.barcode);
            if(receipt.Barcode!=null)
            {
                Bitmap bitmap = null;
                TextView number = (TextView) view.findViewById(R.id.number);
                number.setText(receipt.Barcode);
                try {

                    bitmap = encodeAsBitmap(receipt.Barcode, BarcodeFormat.CODE_128, 600, 100);
                    barcode.setImageBitmap(bitmap);
//                    bitmap.recycle();

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            else {
//            barcode.setImageResource(R.drawable.barcode);
            view = barcode;
            }

        }
        else{
            Item item = receipt.getItem(position - 2);

            view =View.inflate(this, R.layout.listitem_nodivider, null);
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
        if(!deleted && !editing)
        Model.getInstance().AddReceipt(receipt,null);//update if not deleted
//        if(decodedByte!=null)
//        decodedByte.recycle();
    }
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
