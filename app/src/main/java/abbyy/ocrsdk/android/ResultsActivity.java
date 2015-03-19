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

import net.greenreceipt.greenreceipt.ListReceiptActivity;
import net.greenreceipt.greenreceipt.Model;
import net.greenreceipt.greenreceipt.R;
import net.greenreceipt.greenreceipt.Receipt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;

import Util.Helper;

import static net.greenreceipt.greenreceipt.Model.RECEIPT_FILTER;


public class ResultsActivity extends Activity {

	String outputPath;
	TextView tv;
    private String parsedStr = "";
	private String totalAmount = "";
    private String totalTax = "";
    private String storeName = "";
    private HashMap<String, Integer> itemList;
    private boolean foundStoreName = false;
    private boolean foundTotalTax = false;
    private boolean foundTotalAmount = false;
    private boolean foundItemList = false;
    ProgressDialog spinner;

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
		
		String imageUrl = "unknown";
		
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
                        getTotalAmount(text.toLowerCase());
                    }

                    if (!foundTotalTax)
                    {
                        getTotalTax(text.toLowerCase());
                    }

                    if (!foundItemList)
                    {
                        //getItemList(text.toLowerCase());
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
            spinner = ProgressDialog.show(ResultsActivity.this,null,"Working...");
            Receipt r = new Receipt();
            r.Store.Company.Name = getStoreName();
            r.CreatedDate = new Date();
            Pair<Double,Double> location = Model.getInstance().getCurrentLocation(this);
            r.Longitude = location.first;
            r.Latitude = location.second;
            r.PurchaseDate = new Date();
            try {
                r.Total = Double.parseDouble(getTotalAmount().trim());
                r.Tax = Double.parseDouble(getTotalTax().trim());
                Model.getInstance().AddReceipt(r,null);
            }
            catch (Exception e)
            {
                spinner.dismiss();
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
