package net.greenreceipt.greenreceipt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

    public class tokenObject {

        public String access_token;
        public String token_type;
        public long expires_in;
        public String userName;
        public String issued;
        public String expires;
    }
//    private static String BASE_URL = "https://www.greenreceipt.net/";
    private static String BASE_URL = "https://www.greenreceipt.net/";
    private static int timeoutConnection = 3000;
    private static int timeoutSocket = 5000;
    public static tokenObject login(String email, String password) {
        try {
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
                return null;

            Gson gson = new Gson();
            tokenObject login = gson.fromJson(responseString, tokenObject.class);

            return login;
        } catch (Exception e) {
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
    public static boolean addReceipt(Receipt r)
    {

        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(BASE_URL + "api/Receipt");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization","Bearer "+Model._token);
            Gson gson = new Gson();
            String jsonString = gson.toJson(r);
            StringEntity entity = new StringEntity(jsonString);
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Receipt[] getAllReceipts()
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

}