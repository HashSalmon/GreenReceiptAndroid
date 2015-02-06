package net.greenreceipt.greenreceipt;

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
        public void addReceiptFailed();
    }
    public interface GetReceiptListener
    {
        public void getReceiptSuccess();
        public void getReceiptFailed();
    }

    private OnLoginListener _loginListener;
    private RegisterUserListener _registerUserListener;
    private AddReceiptListener _receiptListener;
    private GetReceiptListener _getReceiptListener;

    private static Model _instance;
    private static User _currentUser = null;
    public static String _token;
//    private static File _receiptFile;
    private static File _userFile;
    static List<Receipt> _receipts;
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
    }
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
    public void login(final String email, final String password)
    {
        AsyncTask<String,Integer,Networking.tokenObject> loginTask = new AsyncTask<String, Integer, Networking.tokenObject>() {
            @Override
            protected Networking.tokenObject doInBackground(String... params) {
                return Networking.login(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Networking.tokenObject tokenObject) {
                super.onPostExecute(tokenObject);
                if(tokenObject != null)
                {
                    _currentUser = new User();
                    _token = tokenObject.access_token;
                    _currentUser.Email = email;
                    _currentUser.Username = tokenObject.userName;
                    _loginListener.onLoginSuccess();
                }
                else
                {
                    _loginListener.onLoginFailed("Login Failed!");
                }
            }
        };
        loginTask.execute(email,password);
    }
//    public File getReceiptFile()
//    {
//        return _receiptFile;
//    }
//    public void setReceiptFile(File file)
//    {
//        _receiptFile = file;
//    }
    public boolean userLoggedIn()
    {
        return _currentUser != null;
    }

    public int getReceiptsCount()
    {
        return _receipts.size();
    }
    public Receipt getReceipt(int index)
    {
        return _receipts.get(index);
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
                return Networking.addReceipt(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(_receiptListener!=null)
                {
                    if (aBoolean)
                        _receiptListener.addReceiptSuccess();
                    else
                        _receiptListener.addReceiptFailed();
                }
            }
        };
        addTask.execute(r);
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
                    _getReceiptListener.getReceiptSuccess();
                }
                else
                    _getReceiptListener.getReceiptFailed();

            }
        };
        getTask.execute();
    }
    public int getReceiptIndex(int id)
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
        for(Receipt r: _receipts)
        {
            if(android.text.format.DateFormat.format("M", r.PurchaseDate).equals(month))
            {
                count++;
                total+=r.Total;
            }
        }
        Pair result = new Pair(count,total);
        return result;
    }
//    private void loadReceipts(File receiptFile)
//    {
//        _receipts.clear();
//        try{
//            FileReader textReader = new FileReader(receiptFile);
//            BufferedReader bufferedReader = new BufferedReader(textReader);
//            String libraryJson = bufferedReader.readLine();
//		    Gson gson = new Gson();
//
//            Receipt[] receipts = gson.fromJson(libraryJson,Receipt[].class);
//            Collections.addAll(_receipts, receipts);
//
//            bufferedReader.close();
//        }
//        catch (Exception e)
//        {
////            add();
////				addBook("Mistborn");
////				addBook("Words of Radiance");
////				addBook("Green Eggs and Ham");
//        }
//    }
//    private void saveReceipts(File receiptFile)
//    {
//        try{
//            FileWriter textWriter = new FileWriter(receiptFile);
//
//            BufferedWriter bufferedWriter = new BufferedWriter(textWriter);
//
//			Gson gson = new Gson();
//            String receiptsJson = gson.toJson(_receipts.toArray(new Receipt[_receipts.size()]));
//            bufferedWriter.write(receiptsJson);
//            bufferedWriter.close();
//        }
//        catch (Exception e)
//        {
////				add();
//        }
//    }
//    public void addReceipt(Receipt r)
//    {
//        _receipts.add(r);
////        saveReceipts(_receiptFile);
//    }
    public double getReceiptsTotal()
    {
        double result=0;
        for(Receipt r : _receipts)
        {
            result += r.Total;
        }
        return result;
    }

}
