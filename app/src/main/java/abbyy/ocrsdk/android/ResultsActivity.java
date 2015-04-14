package abbyy.ocrsdk.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.greenreceipt.greenreceipt.Item;
import net.greenreceipt.greenreceipt.ListReceiptActivity;
import net.greenreceipt.greenreceipt.ManualReceiptActivity;
import net.greenreceipt.greenreceipt.Model;
import net.greenreceipt.greenreceipt.R;
import net.greenreceipt.greenreceipt.Receipt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Util.Helper;

import static net.greenreceipt.greenreceipt.Model.RECEIPT_FILTER;


public class ResultsActivity extends Activity {

	String outputPath;
	TextView tv;
    private String parsedStr = "";
	private String totalAmount = "";
    private String subTotal = "";
    private String totalTax = "";
    private String storeName = "";
    private HashMap<String, Integer> itemList;
    private boolean foundStoreName = false;
    private boolean foundTotalTax = false;
    private boolean foundTotalAmount = false;
    private boolean foundSubTotal = false;
    private boolean foundItemList = false;
    ProgressDialog spinner;
    String imageUrl;
    private ArrayList<Item> itemsList = new ArrayList<>();
    private double sum = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Model.getInstance().setAddReceiptListener(new Model.AddReceiptListener() {
            @Override
            public void addReceiptSuccess() {
                spinner.dismiss();
                Intent intent = new Intent(getBaseContext(),ListReceiptActivity.class);
                intent.putExtra(RECEIPT_FILTER,4);
                startActivity(intent);
                finish();
            }

            @Override
            public void addReceiptFailed(String error) {
                spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
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
		tv = new TextView(this);
		setContentView(tv);
		
		imageUrl = "unknown";
		
		Bundle extras = getIntent().getExtras();
		if( extras != null) {
			imageUrl = extras.getString("IMAGE_PATH" );
			outputPath = extras.getString( "RESULT_PATH" );
		}
		
		// Starting recognition process
		new AsyncProcessTask(this).execute(imageUrl, outputPath);
	}

	public void updateResults(Boolean success) {
		if (!success)
			return;
		try {
			StringBuffer contents = new StringBuffer();

			FileInputStream fis = openFileInput(outputPath);
			try {
				Reader reader = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufReader = new BufferedReader(reader);
				String text = null;
				while ((text = bufReader.readLine()) != null)
                {
                    if (!foundStoreName)
                    {
                        getStoreName(text.toLowerCase());
                    }

                    if (!foundTotalAmount)
                    {
                        isItem(text.toLowerCase());
                        getTotalAmount(text.toLowerCase());
                    }

                    if (!foundTotalTax)
                    {
                        getTotalTax(text.toLowerCase());
                    }



                    contents.append(text).append(System.getProperty("line.separator"));
				}
			} finally {
				fis.close();
			}



			createReceipt(null);
		} catch (Exception e) {
			createReceipt("Error: " + e.getMessage());
		}
	}
    public void isItem(String str)
    {
//        try {
//
//            String pattern = "(\\w+\\s)+(\\s)+\\$?(\\d)+.(\\d)+";
//
//            Pattern r = Pattern.compile(pattern);
//            Matcher match = r.matcher(str);
//
//            if (match.find()) {
//                String itemNamePattern = "(\\w+\\s)+";
//                Pattern p1 = Pattern.compile(itemNamePattern);
//                Matcher m1 = p1.matcher(str);
//
//                if (m1.find()) {
//                    String itemName = str.substring(m1.start(), m1.end()).trim();
//                    String itemPrice = str.substring(m1.end()).trim();
//                    double price = Double.parseDouble(itemPrice.substring(1));
//                    Item item = new Item();
//                    item.ItemName = itemName;
//                    item.Price = price;
//                    itemsList.add(item);
//                    sum += sum;
//                    System.out.println("Item Name: " + itemName + "\n" + "Item Price: " + itemPrice);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            Helper.AlertBox(this,"Error",e.getMessage());
//        }
        // BestBuy, Costco, Walmart, 7-11, Smith, Liquor store, Nordstrom rack, Mimis cafe
        try {
            String generalPattern = "(\\w+\\s)+(\\s)*\\$?(\\d)+.(\\d)+(\\s)?(R)?((\\s)*\\(?(\\d)+.(\\d)+\\)?)?";

            String smithPattern = "(\\d)+.(\\d)+(\\s)*\\((\\d)+.(\\d)+\\)(\\s)*lb(\\s)*@(\\s)*(\\d)+.(\\d)+(\\s)*\\/lb(\\s)*WT(\\s)*(\\d)+(\\s)*(\\w+\\s)+(\\d)+.(\\d)+(\\s)";

            String pattern = generalPattern + "|" + smithPattern;

            Pattern r = Pattern.compile(pattern);
            Matcher match = r.matcher(str);

            if (match.find()) {
                String itemNamePattern = "((\\d)+.(\\d)+(\\s)*\\((\\d)+.(\\d)+\\)(\\s)*lb(\\s)*@(\\s)*(\\d)+.(\\d)+(\\s)*\\/lb(\\s)*WT(\\s)*(\\d)+(\\s)*(\\w+\\s)+)"
                        + "|((\\w+\\s)+(:)?(\\w+\\s)+)";
                Pattern p1 = Pattern.compile(itemNamePattern);
                Matcher m1 = p1.matcher(str);

                if (m1.find()) {
                    String itemName = str.substring(m1.start(), m1.end()).trim();
                    String itemPrice = str.substring(m1.end()).trim();

                    double price = 0.0;
                    try {
                        price = Double.parseDouble(itemPrice.substring(1));
                    } catch (NumberFormatException e) {
                        String pricePattern = "(\\d)+.(\\d)+";
                        Pattern p2 = Pattern.compile(pricePattern);
                        Matcher m2 = p2.matcher(itemPrice);

                        if (m2.find()) {
                            itemPrice = itemPrice.substring(m2.start(), m2.end()).trim();
                            price = Double.parseDouble(itemPrice);
                        }
                    }

                    Item item = new Item();
                    item.ItemName = itemName;
                    item.Price = price;
                    itemsList.add(item);
                    sum += sum;
                    System.out.println("Item Name: " + itemName + "\n" + "Item Price: " + itemPrice);
                }
            }
        }
        catch (Exception e)
        {
            Helper.AlertBox(this,"Error",e.getMessage());
        }
    }
    public String getStoreName()
    {
        return this.storeName;
    }

    private void getStoreName(String contents)
    {
        for (String s : StoreNames.storeNames())
        {
            if (contents.contains(s))
            {
                this.storeName = s;
                this.foundStoreName = true;
            }
        }
    }

    public String getTotalAmount()
    {
        return this.totalAmount;
    }

    private void getTotalAmount(String contents)
    {

        if (contents.contains("total") && !contents.contains("subtotal"))
        {
            for (int i = 0; i < contents.length(); i++)
            {
                if (contents.charAt(i) == '1' || contents.charAt(i) == '2' || contents.charAt(i) == '3' ||
                        contents.charAt(i) == '4' || contents.charAt(i) == '5' || contents.charAt(i) == '6' ||
                        contents.charAt(i) == '7' || contents.charAt(i) == '8' || contents.charAt(i) == '9')
                {
                    this.totalAmount = contents.substring(i);
                    this.foundTotalAmount = true;
                    break;
                }
            }
        }
    }
    private void getSubtotal(String contents)
    {

        if (contents.contains("total") && !contents.contains("subtotal"))
        {
            for (int i = 0; i < contents.length(); i++)
            {
                if (contents.charAt(i) == '1' || contents.charAt(i) == '2' || contents.charAt(i) == '3' ||
                        contents.charAt(i) == '4' || contents.charAt(i) == '5' || contents.charAt(i) == '6' ||
                        contents.charAt(i) == '7' || contents.charAt(i) == '8' || contents.charAt(i) == '9')
                {
                    this.subTotal = contents.substring(i);
                    this.foundSubTotal = true;
                    break;
                }
            }
        }
    }

    public String getTotalTax()
    {
        return this.totalTax;
    }

    private void getTotalTax(String contents)
    {

        if (contents.contains("tax"))
        {
            for (int i = 0; i < contents.length(); i++)
            {
                if (contents.charAt(i) == '1' || contents.charAt(i) == '2' || contents.charAt(i) == '3' ||
                    contents.charAt(i) == '4' || contents.charAt(i) == '5' || contents.charAt(i) == '6' ||
                    contents.charAt(i) == '7' || contents.charAt(i) == '8' || contents.charAt(i) == '9')
                {
                    this.totalTax = contents.substring(i);
                    this.foundTotalTax = true;
                    break;
                }
            }
        }

    }

    public HashMap<String, Integer> getItemList(String contents)
    {
        return this.itemList;
    }

	
	public void createReceipt(String text)
	{
        if(text!=null)
		tv.post( new MessagePoster( text ) );
        else
        {
            try {
//                spinner = ProgressDialog.show(ResultsActivity.this,null,"Working...");
                Receipt r = new Receipt();
                r.Store.Company.Name = getStoreName();
                r.CreatedDate = new Date();
                Pair<Double,Double> location = Model.getInstance().getCurrentLocation(this);
                r.Longitude = location.first;
                r.Latitude = location.second;
                r.PurchaseDate = new Date();

                r.total = getTotalAmount().trim();
                r.tax = getTotalTax().trim();
                r.ReceiptItems = itemsList;
                ArrayList<String> images = new ArrayList<>();
                images.add(imageUrl);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                String receiptString = gson.toJson(r,Receipt.class);
                Intent newReceiptIntent = new Intent(this, ManualReceiptActivity.class);
                newReceiptIntent.putExtra("receipt",receiptString);
                newReceiptIntent.putExtra("path",images);
                startActivity(newReceiptIntent);
                finish();
//            Model.getInstance().AddReceipt(r,image);
            }
            catch (Exception e)
            {
//                spinner.dismiss();
                Helper.AlertBox(this,"Error",e.getMessage());
            }


        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_results, menu);
		return true;
	}

	class MessagePoster implements Runnable {
		public MessagePoster( String message )
		{
			_message = message;
		}

		public void run() {
			tv.append( _message + "\n" );
			setContentView( tv );
		}

		private final String _message;
	}
}
