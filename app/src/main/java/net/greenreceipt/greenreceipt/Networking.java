package net.greenreceipt.greenreceipt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Boya on 12/14/14.
 */
public class Networking {

//    private static String BASE_URL = "https://www.greenreceipt.net/";
    private static String BASE_URL = "https://www.greenreceipt.net/";
    private static int timeoutConnection = 3000;
    private static int timeoutSocket = 5000;
    String error;
    public Token login(String email, String password) {

        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "Token");
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("grant_type", "password"));
            data.add(new BasicNameValuePair("username", email));
            data.add(new BasicNameValuePair("password", password));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if (responseString == null || response.getStatusLine().getStatusCode() != 200)
            {
//                Token login = new Token();
                error = "Bad credentials";
//                return login;
                return null;
            }


            Gson gson = new Gson();
            Token login = gson.fromJson(responseString, Token.class);

            return login;
        }
        catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static boolean Register(String email, String firstname, String lastname, String password, String confirm, String username)
    {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/Account/Register");
            request.addHeader("Content-Type", "application/json");
            JSONObject json = new JSONObject();
            json.put("FirstName", firstname);
            json.put("LastName", lastname);
            json.put("Password", password);
            json.put("ConfirmPassword", confirm);
            json.put("Email", email);
            json.put("Username", username);
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);
            HttpResponse response = client.execute(request);

            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Receipt addReceipt(Receipt r,ReceiptImage image)
    {

        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/Receipt");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String jsonString = gson.toJson(r);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
                return null;

            Receipt receipt = gson.fromJson(responseString,Receipt.class);
            if(image!=null) {
                image.ReceiptId = receipt.Id;
                postReceiptImage(image);
            }
            return receipt;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }
    public Receipt[] getAllReceipts()
    {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/Receipt");
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
                return null;

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Receipt[] receipts = gson.fromJson(responseString,Receipt[].class);
            return receipts;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Receipt[] getReturnReceipts()
    {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/Receipt/ReturnReceipts");
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
                return null;

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Receipt[] receipts = gson.fromJson(responseString,Receipt[].class);
            return receipts;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean deleteReceipt(long id)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpDelete request = new HttpDelete(BASE_URL + "api/Receipt?id="+id);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);

            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()!=200)//failed
            {
                InputStream responseContent = response.getEntity().getContent();
                Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
                String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
                error = responseString;
                return false;
            }
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Category[] getCategories()
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/Category/GetCategories");
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
            {
                error = "Failed to retrieve data";
                return null;
            }

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Category[] categories = gson.fromJson(responseString,Category[].class);
            return categories;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public CategoryReport getCategoryReport(String startDate, String endDate)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/CategoryReport?startDate="+startDate+"&endDate="+endDate);
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
            {
                error = "Failed to retrieve data";
                return null;
            }

            Gson gson = new Gson();
            CategoryReport categoryReport = gson.fromJson(responseString, CategoryReport.class);
            return categoryReport;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }
    public Budget getCurrentBudget()
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/Budget/CurrentBudget");
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
            {
                error = "Bad call";
                return null;
            }

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Budget budget = gson.fromJson(responseString, Budget.class);
            return budget;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }
    public boolean createBudget(Budget b)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/Budget");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String jsonString = gson.toJson(b);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()!=200)
            {
                InputStream responseContent = response.getEntity().getContent();
                Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
                String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
                error = responseString;
            }
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }

    public boolean saveBudgetItem(List<BudgetItem> items)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/BudgetItem");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String jsonString = gson.toJson(items);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()!=200)
            {
                InputStream responseContent = response.getEntity().getContent();
                Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
                String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
                error = responseString;
            }
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }
    public boolean deleteBudgetItem(List<Integer> ids)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/BudgetItem/DeleteBudgetItems");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String jsonString = gson.toJson(ids);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()!=200)
            {
                InputStream responseContent = response.getEntity().getContent();
                Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
                String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
                error = responseString;
            }
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }
    public boolean postReceiptImage(ReceiptImage image)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/Image/ReceiptImage");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String jsonString = gson.toJson(image);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()!=200)
            {
                InputStream responseContent = response.getEntity().getContent();
                Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
                String responseString = responseScanner.hasNext() ? responseScanner.next() : null;
                error = responseString;
            }
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }
    public TrendingReport getTrendingReport(String startDate, String endDate)
    {
        try {
            error = "";
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(BASE_URL + "api/TrendingReport?startDate="+startDate+"&endDate="+endDate);
            request.addHeader("Authorization","Bearer "+Model._token);
            HttpResponse response = client.execute(request);

            InputStream responseContent = response.getEntity().getContent();
            Scanner responseScanner = new Scanner(responseContent).useDelimiter("\\A");
            String responseString = responseScanner.hasNext() ? responseScanner.next() : null;

            if(responseString == null || response.getStatusLine().getStatusCode() != 200)
            {
                error = "Failed to retrieve data";
                return null;
            }

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            TrendingReport trendingReport = gson.fromJson(responseString, TrendingReport.class);
            return trendingReport;
        }
        catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }







}