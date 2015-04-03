package net.greenreceipt.greenreceipt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Boya on 12/14/14.
 */
public class Model
{

    static final String ACTION_TRENDING_FAIL = "TrendingFail" ;
    static final String ACTION_TRENDING_SUCCESS = "TrendingSuccess";
    static final String DELETE_BUDGET_ITEM_SUCCESS = "DeleteBItemSuccess";
    static final String DELETE_BUDGET_ITEM_FAIL = "DeleteBItemFail";
    static final String GET_RECEIPT_COUNT_SUCCESS = "GetReceiptCountSuccess";
    static final String GET_RECEIPT_COUNT_FAIL = "GetReceiptCountFail";
    static final String GET_RECEIPT_TOTAL_SUCCESS = "GetReceiptTotalSuccess";
    static final String GET_RECEIPT_TOTAL_FAIL = "GetReceiptTotalFail";
    static final String GET_MONTHLY_RECEIPT_COUNT_SUCCESS = "GetMReceiptCountSuccess";
    static final String GET_MONTHLY_RECEIPT_COUNT_FAIL = "GetMReceiptCountFail";
    static final String GET_MONTHLY_RECEIPT_TOTAL_SUCCESS = "GetMReceiptTotalSuccess";
    static final String GET_MONTHLY_RECEIPT_TOTAL_FAIL = "GetMReceiptTotalFail";
    static final String GET_RECEIPT_IMAGE_SUCCESS = "GetReceiptImageSuccess";
    static final String GET_RECEIPT_IMAGE_FAIL = "GetReceiptImageFail";
    static final String ADD_RECEIPT_SUCCESS = "AddReceiptSuccess";
    static final String ADD_RECEIPT_FAILED = "AddReceiptFailed";

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
    public interface GetCategoryListener
    {
        public void onGetCategorySuccess();
        public void onGetCateogryFailed(String error);
    }
    public interface GetCateogryReportListener
    {
        public void onGetCateogryReportSuccess(CategoryReport report);
        public void onGetCategoryReportFailed(String error);
    }
    public interface GetCurrentBudgetListener
    {
        public void onGetBudgetSuccess();
        public void onGetBudgetFailed(String error);
    }
    public interface CreateBudgetListener
    {
        public void onCreateSuccess();
        public void onCreateFailed(String error);
    }
    public interface GetReceiptImageListener
    {
        public void onGetImageSuccess(ReceiptImage[] images);
        public void onGetImageFailed(String error);
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
    private GetCategoryListener _getCategoryListener;
    private GetCateogryReportListener _getCategoryReportListener;
    private GetCurrentBudgetListener _getCurrentBudgetListener;
    private CreateBudgetListener _createBudgetListener;
    private GetReceiptImageListener _getReceiptImageListener;

    private static Model _instance;

    User _currentUser = null;
    public static String _token;
    List<Receipt> _receipts;
    List<Receipt> _returnReceipts;
    List<Receipt> _displayReceipts;
    Category[] categories;
    Budget currentBudget;
    TrendingReport trendingReport;
    private static int currentPage=1;
    static int pageSize = 25;
    private static Networking networking;

    private static final Object _modelLock = new Object();
    public static Model getInstance()
    {
        if (_instance == null)
        {
            synchronized (_modelLock)
            {
                if(_instance == null)
                {
                    _instance = new Model();
                }
            }
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
    public void setGetCategoryListener(GetCategoryListener listener)
    {
        _getCategoryListener = listener;
    }
    public void setGetCategoryReportListener(GetCateogryReportListener listener)
    {
        _getCategoryReportListener = listener;
    }
    public void setGetCurrentBudgetListener(GetCurrentBudgetListener listener)
    {
        _getCurrentBudgetListener = listener;
    }
    public void setCreateBudgetListener(CreateBudgetListener listener)
    {
        _createBudgetListener = listener;
    }
    public void setGetReceiptImageListener(GetReceiptImageListener listener)
    {
        _getReceiptImageListener = listener;
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
    public void AddReceipt(Receipt r, ReceiptImage image)
    {
        AsyncTask<Object,Integer,Boolean> addTask = new AsyncTask<Object, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params)
            {
                return networking.addReceipt((Receipt)params[0],(ReceiptImage)params[1]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(_receiptListener!=null)
                {
                    if (result)
                        _receiptListener.addReceiptSuccess();
                    else
                        _receiptListener.addReceiptFailed(networking.error);
                }
            }
        };
        addTask.execute(r,image);
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
    public void GetAllReceipt(int pageSize,final int pageCount)
    {
        AsyncTask<Integer,Integer,Receipt[]> getTask = new AsyncTask<Integer, Integer, Receipt[]>() {
            @Override
            protected Receipt[] doInBackground(Integer... params)
            {
                return networking.getAllReceipts(params[0],params[1],params[2]);
            }

            @Override
            protected void onPostExecute(Receipt[] receipts)
            {
                super.onPostExecute(receipts);
                if(receipts!=null)
                {
//                    _receipts.clear();
                    if(receipts.length>0)
                    {
                        Model.currentPage++;
                        for (Receipt r : receipts)
                            _receipts.add(r);
                    }
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
        getTask.execute(pageSize,Model.currentPage,pageCount);
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

    public void GetCategories()
    {
        AsyncTask<Void, Integer, Category[]> getCategoryTask = new AsyncTask<Void, Integer, Category[]>() {
            @Override
            protected Category[] doInBackground(Void... params) {
                return networking.getCategories();
            }

            @Override
            protected void onPostExecute(Category[] categories) {
                super.onPostExecute(categories);
                if(categories!=null)
                {
                    Model.getInstance().categories = categories;
                    if(_getCategoryListener !=null)
                        _getCategoryListener.onGetCategorySuccess();
                }
                else
                {
                    if(_getCategoryListener!=null)
                        _getCategoryListener.onGetCateogryFailed(networking.error);
                }
            }
        };
        getCategoryTask.execute();
    }
    public void GetCategoryReport(String startDate, String endDate)
    {
        AsyncTask<String,Integer,CategoryReport> getReportTask = new AsyncTask<String, Integer, CategoryReport>() {
            @Override
            protected CategoryReport doInBackground(String... params) {
                return networking.getCategoryReport(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(CategoryReport categoryReport) {
                super.onPostExecute(categoryReport);
                if(categoryReport !=null)
                {
                    if(_getCategoryReportListener != null)
                        _getCategoryReportListener.onGetCateogryReportSuccess(categoryReport);
                }
                else
                {
                    if(_getCategoryReportListener != null)
                        _getCategoryReportListener.onGetCategoryReportFailed(networking.error);
                }
            }
        };
        getReportTask.execute(startDate,endDate);
    }
    public void GetTrendingReport(final String startDate, final String endDate, final Context c)
    {
        final AsyncTask<String,Integer,TrendingReport> getReportTask = new AsyncTask<String, Integer, TrendingReport>() {
            @Override
            protected TrendingReport doInBackground(String... params) {
                return networking.getTrendingReport(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(TrendingReport trendingReport) {
                super.onPostExecute(trendingReport);
                if(trendingReport !=null) {
                    Model.getInstance().trendingReport = trendingReport;
                    Intent success = new Intent();
                    success.setAction(Model.ACTION_TRENDING_SUCCESS);
                    c.sendBroadcast(success);
                }
                else
                {
                    Intent fail = new Intent();
                    fail.setAction(Model.ACTION_TRENDING_FAIL);
                    c.sendBroadcast(fail);
                }
            }
        };
        getReportTask.execute(startDate,endDate);
    }

    public void GetCurrentBudget()
    {
        AsyncTask<Void,Integer,Budget> getBudgetTask = new AsyncTask<Void, Integer, Budget>() {
            @Override
            protected Budget doInBackground(Void... params) {
                return networking.getCurrentBudget();
            }

            @Override
            protected void onPostExecute(Budget budget) {
                super.onPostExecute(budget);
                if(budget!=null)
                {
                    currentBudget = budget;
                    if(_getCurrentBudgetListener!=null)
                        _getCurrentBudgetListener.onGetBudgetSuccess();
                }
                else
                    if(_getCurrentBudgetListener!=null)
                        _getCurrentBudgetListener.onGetBudgetFailed(networking.error);
            }
        };
        getBudgetTask.execute();
    }
    public void CreateBudget(Budget b, Context c)
    {
        AsyncTask<Budget,Integer,Boolean> createTask = new AsyncTask<Budget, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Budget... params) {
                return networking.createBudget(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                    if(_createBudgetListener!=null)
                        _createBudgetListener.onCreateSuccess();
                else
                        if(_createBudgetListener!=null)
                            _createBudgetListener.onCreateFailed(networking.error);
            }
        };
        createTask.execute(b);
        Intent intent = new Intent();

    }

    public void SaveBudgetItems(List<BudgetItem> items, final Context c)
    {
        AsyncTask<List,Integer,Boolean> saveTask = new AsyncTask<List, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(List... params) {
                return networking.saveBudgetItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                {
                    Intent intent = new Intent();
                    c.sendBroadcast(intent);
                }

            }
        };
        saveTask.execute(items);
    }
    public void DeleteBudgetItem(final int id, final Context c)
    {
        AsyncTask<Integer,Integer,Boolean> deleteTask = new AsyncTask<Integer, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                return networking.deleteBudgetItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                {
                    Intent success = new Intent();
                    success.setAction(Model.DELETE_BUDGET_ITEM_SUCCESS);
                    success.putExtra("id",id);
                    c.sendBroadcast(success);
                }
                else
                {
                    Intent success = new Intent();
                    success.setAction(Model.DELETE_BUDGET_ITEM_FAIL);
                    c.sendBroadcast(success);
                }
            }
        };
        deleteTask.execute(id);
    }
    public void DeleteBudgetItems(final List<Integer> id, final Context c)
    {
        AsyncTask<List,Integer,Boolean> deleteTask = new AsyncTask<List, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(List... params) {
                return networking.deleteBudgetItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                {
                    Intent success = new Intent();
                    success.setAction(Model.DELETE_BUDGET_ITEM_SUCCESS);
                    c.sendBroadcast(success);
                }
                else
                {
                    Intent success = new Intent();
                    success.setAction(Model.DELETE_BUDGET_ITEM_FAIL);
                    c.sendBroadcast(success);
                }
            }
        };
        deleteTask.execute(id);
    }
    public void GetReceiptImages(int id)
    {
        AsyncTask<Integer,Integer,ReceiptImage[]> task = new AsyncTask<Integer, Integer, ReceiptImage[]>() {
            @Override
            protected ReceiptImage[] doInBackground(Integer... params) {
                return networking.getReceiptImages(params[0]);
            }

            @Override
            protected void onPostExecute(ReceiptImage[] receiptImages) {
                super.onPostExecute(receiptImages);
                if(receiptImages!=null)
                {
                    if(_getReceiptImageListener!=null)
                        _getReceiptImageListener.onGetImageSuccess(receiptImages);
                }
                else
                {
                    if(_getReceiptImageListener!=null)
                        _getReceiptImageListener.onGetImageFailed(networking.error);
                }
            }
        };
        task.execute(id);
    }
    public void GetTotalReceiptCount(final Context c)
    {
        AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                return networking.getReceiptCount(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if(integer > -1)
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_RECEIPT_COUNT_SUCCESS);
                    intent.putExtra("count",integer);
                    c.sendBroadcast(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_RECEIPT_COUNT_FAIL);
                    c.sendBroadcast(intent);
                }

            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date start = new Date(0);
        Date end = new Date();
        task.execute(sdf.format(start),sdf.format(end));
    }
    public void GetMonthReceiptCount(final Context c)
    {
        AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                return networking.getReceiptCount(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if(integer > -1)
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_MONTHLY_RECEIPT_COUNT_SUCCESS);
                    intent.putExtra("count",integer);
                    c.sendBroadcast(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_MONTHLY_RECEIPT_COUNT_FAIL);
                    c.sendBroadcast(intent);
                }

            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month++;
        int year = calendar.get(Calendar.YEAR);
        Date end = new Date();
        task.execute(month+"/01/"+year,sdf.format(end));
    }
    public void GetTotalReceiptTotal(final Context c)
    {
        final AsyncTask<String,Integer,Double> task = new AsyncTask<String, Integer, Double>() {
            @Override
            protected Double doInBackground(String... params) {
                return networking.getReceiptTotal(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Double aDouble) {
                super.onPostExecute(aDouble);
                if(aDouble > -1)
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_RECEIPT_TOTAL_SUCCESS);
                    intent.putExtra("total",aDouble);
                    c.sendBroadcast(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_RECEIPT_TOTAL_FAIL);
                    c.sendBroadcast(intent);
                }
            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date start = new Date(0);
        Date end = new Date();
        task.execute(sdf.format(start),sdf.format(end));
    }
    public void GetMonthReceiptTotal(final Context c)
    {
        final AsyncTask<String,Integer,Double> task = new AsyncTask<String, Integer, Double>() {
            @Override
            protected Double doInBackground(String... params) {
                return networking.getReceiptTotal(params[0],params[1]);
            }

            @Override
            protected void onPostExecute(Double aDouble) {
                super.onPostExecute(aDouble);
                if(aDouble > -1)
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_MONTHLY_RECEIPT_TOTAL_SUCCESS);
                    intent.putExtra("total",aDouble);
                    c.sendBroadcast(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(GET_MONTHLY_RECEIPT_TOTAL_FAIL);
                    c.sendBroadcast(intent);
                }
            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month++;
        int year = calendar.get(Calendar.YEAR);
        Date end = new Date();
        task.execute(month+"/01/"+year,sdf.format(end));

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
        int m = Calendar.MONTH;
        m++;
        String month = ""+m;
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
        SimpleDateFormat format = new SimpleDateFormat("M");
        int m = Calendar.MONTH;
        m++;
        String month = ""+m;
        month = format.format(new Date());
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
    public List<Receipt> getCurrentYearReceipt()
    {
        List<Receipt> result = new ArrayList<Receipt>();

        Calendar c = Calendar.getInstance();
        String year = c.get(Calendar.YEAR)+"";
        for(Receipt r: _receipts)
        {
            if(android.text.format.DateFormat.format("yyyy", r.PurchaseDate).equals(year))
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
                _displayReceipts = getCurrentYearReceipt();
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
    public List<Receipt> sortList(Comparator<Receipt> comparator)
    {
        List<Receipt> result = _displayReceipts;
        Collections.sort(result, comparator);
        return result;
    }
    public byte[] getByteArrayFromImage(String filePath) throws FileNotFoundException, IOException {

        File file = new File(filePath);
        System.out.println(file.exists() + "!!");

        FileInputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

        //InputStream in = resource.openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
                //no doubt here is 0
                /*Writes len bytes from the specified byte array starting at offset
                off to this byte array output stream.*/
                System.out.println("read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            Log.d("error", "error");
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
    public boolean removeBudgetItem(int id)
    {
        if(currentBudget!=null && currentBudget.BudgetItems != null)
        {
            for(BudgetItem b :currentBudget.BudgetItems)
                if(b.Id == id)
                   return currentBudget.BudgetItems.remove(b);
        }
        return false;
    }
    public void resetCurrentPage()
    {
        currentPage = 1;
        _receipts.clear();

    }
    public int getCategoryIndex(String name)
    {
        for(int i = 0; i<categories.length; i++)
        {
            if(categories[i].Name.equals(name))
                return i;
        }
        return -1;
    }


}
