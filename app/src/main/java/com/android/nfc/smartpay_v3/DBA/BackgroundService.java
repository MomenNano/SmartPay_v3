package com.android.nfc.smartpay_v3.DBA;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 7/18/2018.
 */

public class BackgroundService extends IntentService {
    public static boolean servicesRun = false;
    ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<>();
    //SharedPreferences sharedPreferences ;
    public BackgroundService() {
        super("ServerRequest");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //sharedPreferences = getApplicationContext().getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
        //the next line is for Testing need to be deleted later
        /*for (int i = 0 ; i < 3 ; i++){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setBillAmount("500");
            paymentInfo.setUniqueId("xxxxxxxxxxx"+i);
            paymentInfo.setCardId("77");
            paymentInfo.setPurchaserId("9");
            paymentInfo.setCompanyId("1");
            paymentInfo.setPurchaserName("Mohamed Al-Ameen");
            paymentInfo.setCompanyBankAccount("123456");
            paymentInfo.setCompanyType(2);
            paymentInfo.setStringDate("12/10/2018");
            paymentInfo.setStringTime("12:10:55");
            paymentInfo.setCompanyName("Area 51");
            paymentInfo.setFlag(0);
            LocalDBA.getInstance(getApplicationContext()).insertPaymentTransaction(paymentInfo);
        }*/
        Toast.makeText(getBaseContext(), "Service is Start"+servicesRun, Toast.LENGTH_LONG).show();
        servicesRun = true;
        Toast.makeText(getBaseContext(), "Service is Start"+servicesRun, Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (servicesRun == true){
            Toast.makeText(getBaseContext(), "Service is running", Toast.LENGTH_SHORT).show();
            if (isNetworkStatusAvailable(getApplicationContext())) {
                Toast.makeText(getBaseContext(), "Network is connected", Toast.LENGTH_SHORT).show();
                paymentInfoArrayList = LocalDBA.getInstance(getApplicationContext()).getUncommittedPaymentTransaction();
                for (int i = 0; i < paymentInfoArrayList.size(); i++) {
                    PaymentInfo paymentInfo = paymentInfoArrayList.get(i);
                    System.out.println("Payment Flag"+paymentInfo.getFlag());
                    if (!paymentInfoArrayList.isEmpty()){
                        sendPaymentInfoToServer(paymentInfo);
                    }
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isNetworkStatusAvailable(getApplicationContext())) {
                Toast.makeText(getBaseContext(), "Network is connected", Toast.LENGTH_SHORT).show();
                paymentInfoArrayList = LocalDBA.getInstance(getApplicationContext()).getNonResponsePaymentTransaction();
                for (int i = 0; i < paymentInfoArrayList.size(); i++) {
                    PaymentInfo paymentInfo = paymentInfoArrayList.get(i);
                    System.out.println("Payment Send Flag"+paymentInfo.getSendFlag());
                    if (!paymentInfoArrayList.isEmpty()){
                        checkPaymentInfoInServer(paymentInfo);
                    }
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendPaymentInfoToServer(final PaymentInfo paymentInfo){
        Toast.makeText(this, "Sending To The Server...", Toast.LENGTH_SHORT).show();
        LocalDBA.getInstance(getApplicationContext()).updatePaymentTransactionSendFlag(
                Integer.valueOf(paymentInfo.getTransactionId()));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.SEND_PAYMENT_INFO_URL
                , new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(getBaseContext(), "Responding", Toast.LENGTH_LONG).show();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString(Configuration.CODE).compareTo("1")==0){
                        LocalDBA.getInstance(getApplicationContext()).updatePaymentTransactionFlag(
                                Integer.valueOf(paymentInfo.getTransactionId()));
                            System.out.println("paymentInfo.getTransactionId()"+paymentInfo.getTransactionId());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Un handel exceptions", Toast.LENGTH_LONG).show();
                }
            }
        },errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                /*params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_ID,"45646");
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_ID,"123");
                params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT,"87432132");
                params.put(Configuration.PAYMENT_TRANSACTION_DATE,"14/10/2018");
                params.put(Configuration.PAYMENT_TRANSACTION_TIME,"12:22:55");
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_CARD_ID,"54513215");*/
                System.out.println(Configuration.PAYMENT_TRANSACTION_COMPANY_ID+":"+paymentInfo.getCompanyId());
                System.out.println(Configuration.PAYMENT_TRANSACTION_PURCHASER_ID+":"+paymentInfo.getPurchaserId());
                System.out.println(Configuration.PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT+":"+paymentInfo.getCompanyBankAccount());
                System.out.println(Configuration.PAYMENT_TRANSACTION_DATE+":"+paymentInfo.getStringDate());
                System.out.println(Configuration.PAYMENT_TRANSACTION_TIME+":"+paymentInfo.getStringTime());
                System.out.println(Configuration.PAYMENT_TRANSACTION_PURCHASER_CARD_ID+":"+paymentInfo.getCardId());
                params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_ID,paymentInfo.getCompanyId());
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_ID,paymentInfo.getPurchaserId());
                params.put(Configuration.PAYMENT_TRANSACTION_BILL_AMOUNT,paymentInfo.getBillAmount());
                params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT,paymentInfo.getCompanyBankAccount());
                params.put(Configuration.PAYMENT_TRANSACTION_DATE,paymentInfo.getStringDate());
                params.put(Configuration.PAYMENT_TRANSACTION_TIME,paymentInfo.getStringTime());
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_CARD_ID,paymentInfo.getCardId());
                params.put(Configuration.PAYMENT_TRANSACTION_UNIQUE_ID,paymentInfo.getUniqueId());
                params.put(Configuration.PAYMENT_TRANSACTION_ID,paymentInfo.getTransactionId());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*2,1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(getBaseContext(), "Server not responding", Toast.LENGTH_LONG).show();

        }
    };
    public void checkPaymentInfoInServer(final PaymentInfo paymentInfo){
        Toast.makeText(this, "Sending To The Server...", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.CHECK_PAYMENT_INFO_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(getBaseContext(), "Responding", Toast.LENGTH_LONG).show();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString(Configuration.CODE).compareTo("1")==0){
                        LocalDBA.getInstance(getApplicationContext()).updatePaymentTransactionFlag(
                                Integer.valueOf(paymentInfo.getTransactionId()));
                            System.out.println("paymentInfo.getTransactionId()"+paymentInfo.getTransactionId());

                    }
                    else if (jsonObject.getString(Configuration.CODE).compareToIgnoreCase("0")==0){
                        sendPaymentInfoToServer(paymentInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Un handel exceptions", Toast.LENGTH_LONG).show();
                }
            }
        }, checkErrorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                /*params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_ID,"45646");
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_ID,"123");
                params.put(Configuration.PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT,"87432132");
                params.put(Configuration.PAYMENT_TRANSACTION_DATE,"14/10/2018");
                params.put(Configuration.PAYMENT_TRANSACTION_TIME,"12:22:55");
                params.put(Configuration.PAYMENT_TRANSACTION_PURCHASER_CARD_ID,"54513215");*/
                params.put(Configuration.PAYMENT_TRANSACTION_UNIQUE_ID,paymentInfo.getUniqueId());
                params.put(Configuration.PAYMENT_TRANSACTION_ID,paymentInfo.getTransactionId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*2,1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    Response.ErrorListener checkErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(getBaseContext(), "Server not responding", Toast.LENGTH_LONG).show();

        }
    };




    public boolean isNetworkStatusAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                Toast.makeText(this, "Internet is Connected", Toast.LENGTH_SHORT).show();
                return  networkInfo.isConnected();
            }
        }
        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        return false;
    }
}
