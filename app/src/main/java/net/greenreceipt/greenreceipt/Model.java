package net.greenreceipt.greenreceipt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Boya on 12/14/14.
 */
public class Model
{

    public interface OnLoginListener
    {
        public void onLoginSuccess();
        public void onLoginFailed(String error);
    }
    public interface RegisterUserListener
    {
        public void userRegistered();
        public void userRegisterFailed();
    }
    public interface AddReceiptListener
    {
        public void addReceiptSuccess();
        public void addReceiptFailed(String error);
    }
    public interface GetReceiptListener
    {
        public void getReceiptSuccess();
        public void getReceiptFailed();
    }
    public interface ReturnReceiptListener
    {
        public void returnDetected();
    }
    public interface OnDeleteReceiptListener{
        public void deleteSuccess();
        public void deleteFailed(String error);
    }
    public static final String RECEIPT_FILTER="filter";
    static final int SHOW_RETURN_RECEIPTS = 5;
    static final String[] PAYMENT_TYPES = {
            "Payment Type",
            "Amex",
            "Visa",
            "MasterCard",
            "Discover",
            "Cash"
    };

    static final int RETURN_ALERT_NOTIFICATION = 1;


    private OnLoginListener _loginListener;
    private RegisterUserListener _registerUserListener;
    private AddReceiptListener _receiptListener;
    private GetReceiptListener _getReceiptListener;
    private ReturnReceiptListener _returnReceiptListener;
    private OnDeleteReceiptListener _onDeleteReceiptListener;

    private static Model _instance;
    static User _currentUser = null;
    public static String _token;
    private static File _userFile;
    static List<Receipt> _receipts;
    static List<Receipt> _returnReceipts;
    static List<Receipt> _displayReceipts;
    private static Networking networking;
    public static Model getInstance()
    {
        if (_instance == null)
        {
            _instance = new Model ();
        }
        return _instance;
    }
    private Model()
    {
        _receipts = new ArrayList<Receipt>();
        _returnReceipts = new ArrayList<Receipt>();
        _displayReceipts = new ArrayList<Receipt>();
        networking = new Networking();
    }


    /*
    **********************Listeners**********************
     */
    public void setOnLoginListener(OnLoginListener listener)
    {
        _loginListener = listener;
    }
    public void setRegisterUserListener(RegisterUserListener listener)
    {
        _registerUserListener = listener;
    }
    public void setAddReceiptListener(AddReceiptListener listener)
    {
        _receiptListener = listener;
    }
    public void setGetReceiptListener(GetReceiptListener listener)
    {
        _getReceiptListener = listener;
    }
    public void setReturnReceiptListener(ReturnReceiptListener listener)
    {
        _returnReceiptListener = listener;
    }
    public void setOnDeleteReceiptListener(OnDeleteReceiptListener listener)
    {
        _onDeleteReceiptListener = listener;
    }



    /*
    ****************Server calls******************
     */
    public void Login(final String email, final String password)
    {
        AsyncTask<String,Integer,Token> loginTask = new AsyncTask<String, Integer, Token>() {
            @Override
            protected Token doInBackground(String... params) {
                return networking.login(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Token tokenObject) {
                super.onPostExecute(tokenObject);
                if(tokenObject != null)
                {
                    _currentUser = new User();
                    _token = tokenObject.access_token;
                    _currentUser.Email = email;
                    _currentUser.FirstName = tokenObject.FirstName;
                    _currentUser.LastName = tokenObject.LastName;

                    _loginListener.onLoginSuccess();
                }
                else
                {
                    _loginListener.onLoginFailed("Login Failed!");
                }
            }
        };
        loginTask.execute(email, password);
    }
    public void Logout(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("GreenReceipt", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Intent login = new Intent(context,LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(login);
    }


    public void Register(final String email, String firstname, String lastname, final String password, String confirm, String username)
    {
        AsyncTask<String,Integer,Boolean> registerTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return Networking.Register(params[0],params[1],params[2],params[3],params[4],params[5]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                    _registerUserListener.userRegistered();
                else
                    _registerUserListener.userRegisterFailed();
            }
        };
        registerTask.execute(email,firstname,lastname,password,confirm,username);
    }
    public void AddReceipt(Receipt r)
    {
        AsyncTask<Receipt,Integer,Boolean> addTask = new AsyncTask<Receipt, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Receipt... params)
            {
                return networking.addReceipt(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(_receiptListener!=null)
                {
                    if (aBoolean)
                        _receiptListener.addReceiptSuccess();
                    else
                        _receiptListener.addReceiptFailed(networking.error);
                }
            }
        };
        addTask.execute(r);
    }

    public void DeleteReceipt(long id)
    {
        AsyncTask<Long,Integer,Boolean> deleteTask = new AsyncTask<Long, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                return networking.deleteReceipt(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if(aBoolean)
                    if(_onDeleteReceiptListener!=null)
                    _onDeleteReceiptListener.deleteSuccess();
                else
                    if(_onDeleteReceiptListener!=null)
                        _onDeleteReceiptListener.deleteFailed(networking.error);


            }
        };
        deleteTask.execute(id);
    }
    public void GetAllReceipt()
    {
        AsyncTask<Object,Integer,Receipt[]> getTask = new AsyncTask<Object, Integer, Receipt[]>() {
            @Override
            protected Receipt[] doInBackground(Object... params)
            {
                return Networking.getAllReceipts();
            }

            @Override
            protected void onPostExecute(Receipt[] receipts)
            {
                super.onPostExecute(receipts);
                if(receipts!=null)
                {
                    _receipts.clear();
                    for(Receipt r : receipts)
                        _receipts.add(r);
                    GetReturnReceipts();
                    if(_getReceiptListener!=null)
                    _getReceiptListener.getReceiptSuccess();
                }
                else
                {
                    if (_getReceiptListener != null)
                        _getReceiptListener.getReceiptFailed();
                }

            }
        };
        getTask.execute();
    }
    public void GetReturnReceipts()
    {
        AsyncTask<Void,Integer,Receipt[]> returnTask = new AsyncTask<Void, Integer, Receipt[]>() {
            @Override
            protected Receipt[] doInBackground(Void... params) {
                return Networking.getReturnReceipts();
            }

            @Override
            protected void onPostExecute(Receipt[] receipts) {
                super.onPostExecute(receipts);
                if(receipts!=null)
                {
                    _returnReceipts.clear();
                    for(Receipt r: receipts)
                        _returnReceipts.add(r);
                    if(_returnReceiptListener!=null && _returnReceipts.size()!=0)
                        _returnReceiptListener.returnDetected();
                }
            }
        };
        returnTask.execute();
    }


    /*
    *****************None server call below*********************
     */
    public int getDisplayReceiptsCount()
    {
        return _displayReceipts.size();
    }
    public Receipt getReceipt(int index)
    {
        return _displayReceipts.get(index);
    }
    public int getReceiptById(int id)
    {
        int index = 0;
        for(Receipt r:_receipts)
        {
            if(r.Id == id)
            {
                return index;
            }
            else
                index++;
        }
        return -1;
    }
    public Pair<Integer,Double> getCurrentMonthReceiptCount()
    {
        int count=0;
        double total=0;
        String month = ""+Calendar.MONTH;
        Calendar c = Calendar.getInstance();
        String year = c.get(Calendar.YEAR)+"";
        for(Receipt r: _receipts)
        {
            if(android.text.format.DateFormat.format("M", r.PurchaseDate).equals(month) && android.text.format.DateFormat.format("yyyy", r.PurchaseDate).equals(year))
            {
                count++;
                total+=r.Total;
            }
        }
        Pair result = new Pair(count,total);
        return result;
    }

    public double getReceiptsTotal()
    {
        double result=0;
        for(Receipt r : _receipts)
        {
            result += r.Total;
        }
        return result;
    }
    public int getTotalReceiptCount()
    {
        return _receipts.size();
    }


    public List<Receipt> getCurrentMonthReceipt()
    {
        List<Receipt> result = new ArrayList<Receipt>();
        String month = ""+Calendar.MONTH;
        Calendar c = Calendar.getInstance();
        String year = c.get(Calendar.YEAR)+"";
        for(Receipt r: _receipts)
        {
            if(android.text.format.DateFormat.format("M", r.PurchaseDate).equals(month) && android.text.format.DateFormat.format("yyyy", r.PurchaseDate).equals(year))
            {
                result.add(r);
            }
        }
        return result;
    }

    public void changeDisplayReceipts(int display)
    {
        switch (display){

            case 1://week
                break;
            case 2://month
                _displayReceipts = getCurrentMonthReceipt();
                break;
            case 3://year
                break;
            case 4:
                _displayReceipts = _receipts;
                break;
            case 5://display return
                _displayReceipts = _returnReceipts;
                break;
            default:
                break;//do nothing
        }
    }
    public Pair<Double,Double> getCurrentLocation(Context context)
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Pair<Double,Double> result = new Pair<Double,Double>(longitude,latitude);
        return result;
    }

}
