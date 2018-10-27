package com.android.nfc.smartpay_v3.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcEvent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PurchaserMainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback , NfcAdapter.OnNdefPushCompleteCallback{
    private NfcAdapter nfcAdapter;
    private TextView PurchaserMessage;
    //private NdefMessage ndefMessage;
    boolean allGood = false;
    protected String externalType = "nfclab.com:SmartPay";
    PaymentInfo paymentInfo = new PaymentInfo();
    AlertDialog alert;
    LayoutInflater inflater;
    View alertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaser_main);
        alert = new AlertDialog.Builder(this).create();
        alertView = View.inflate(getBaseContext(),R.layout.progress_dialog,null);
        inflater = getLayoutInflater();
        alert.setView(alertView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        PurchaserMessage = (TextView) findViewById(R.id.PurchaserMessage);

    }

    protected void checkNFCSupporting() {
        // Check whether NFC is available on device
        if (nfcAdapter == null) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void checkForNFCAdapter() {
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please Enable your NFC.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else if (!nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(this, "Please Enable Android Beam.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        } else {
            Toast.makeText(this, "Ready to pay", Toast.LENGTH_SHORT).show();
        }
    }

    /*private NdefMessage createNdefMessage() {
        Gson gson = new Gson();

        String msg = gson.toJson(paymentInfo);

        //Send paymentInfo to the server
        sendPurchasrePaymentInfo(msg);
        //Setup NdefRecord
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], msg.getBytes());
        //Setup NdefMessage
        NdefMessage message = new NdefMessage(textRecord);
        return message;
    }*/

    protected void sendPurchasrePaymentInfo(String msg) {
        final String PurchaserPaymentInfoURL = "http://.ngrok.io/";
        final RequestQueue requestQueue = Volley.newRequestQueue(PurchaserMainActivity.this);

        JSONObject messageJsonRequest = null;
        try {
            messageJsonRequest = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, PurchaserPaymentInfoURL,messageJsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").contains("true")){

                                Toast.makeText(getApplicationContext(),"send Successfully",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error, Try Again",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                        Log.d("sucess","sucessfull login");
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_LONG).show();
                        Log.d("error","error in login");

                        requestQueue.stop();

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForNFCAdapter();
        checkNFCSupporting();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            receiveIntent(getIntent());
        }
    }

    private void receiveIntent(Intent intent) {
        NdefMessage messages = getNdefMessages(getIntent());
        if (messages == null){
            return;
        }

        NdefRecord record = messages.getRecords()[0];
        String payload = new String(record.getPayload());
        PurchaserMessage.setText(payload);
        allGood = true;
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);

        paymentInfo = gson.fromJson(payload, PaymentInfo.class);
        paymentInfo.setCardId(sharedPreferences.getString(Configuration.KEY_CARD_NO,"null"));
        paymentInfo.setPurchaserId(sharedPreferences.getString(Configuration.KEY_PURCHASER_ID,"null"));
        paymentInfo.setFlag(0);
        paymentInfo.setPurchaserName(sharedPreferences.getString(Configuration.KEY_PREFERENCE_USERNAME,"null"));
        paymentInfo.setSendFlag(0);

        if (allGood) {
            //NdefMessage ndefMessage = createNdefMessage();
            nfcAdapter.setNdefPushMessageCallback(this, this);

        }
        nfcAdapter.setOnNdefPushCompleteCallback(this,this);
        Log.d("create Message","createMessage");

    }

    protected NdefMessage getNdefMessages(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null){
         NdefMessage msg = (NdefMessage) rawMsgs[0];
            return msg;
        }

        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.d("create Message","createMessage");
        Gson gson = new Gson();

        String msg = gson.toJson(paymentInfo);
        showProgressBar(msg);
        //Send paymentInfo to the server
        //Setup NdefRecord
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], msg.getBytes());
        //Setup NdefMessage
        NdefMessage message = new NdefMessage(textRecord);

        return message;
    }


    @Override
    public void onNdefPushComplete(NfcEvent event) {
        System.out.print("LOL");
        Log.d("sent","sent successfully");
        LocalDBA.getInstance(getApplicationContext()).insertPaymentTransaction(paymentInfo);
    }

    /*protected void sendToServer(){
        final String Transactionurl = "";
        final RequestQueue requestQueue = Volley.newRequestQueue(PurchaserMainActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Transactionurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JsonResponse = new JSONObject(response);
                            if(JsonResponse.getString("status").contains("true")){

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong username or password",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("sucess","sucessfull login");
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_LONG).show();
                        Log.d("error","error in login");
                        requestQueue.stop();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userName",username.getText().toString());
                params.put("password",password.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }*/
    public void showProgressBar(String str){
        ProgressBar progressBar = alertView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        TextView msg = alertView.findViewById(R.id.progress_msg);
        msg.setText(str);
        alert.show();
    }
    public void dismissProgressBar(String str){
        ProgressBar progressBar = alertView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        TextView msg = alertView.findViewById(R.id.progress_msg);
        msg.setText(str);
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alert.dismiss();
            }
        });
    }
    public void dismiss(){
        alert.dismiss();
    }
}
