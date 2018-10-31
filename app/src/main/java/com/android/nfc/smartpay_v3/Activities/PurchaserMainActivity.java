package com.android.nfc.smartpay_v3.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcEvent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.Adapters.SlidePagerAdapter;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PurchaserMainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback , NfcAdapter.OnNdefPushCompleteCallback{
    private NfcAdapter nfcAdapter;
    private TextView PurchaserMessage;
    //private NdefMessage ndefMessage;
    boolean allGood = false;
    boolean passwordCheck = false;
    boolean balanceCheck = false;
    static int pos = 0;
    protected String externalType = "nfclab.com:SmartPay";
    PaymentInfo paymentInfo = new PaymentInfo();
    AlertDialog progressbaralert;
    AlertDialog noBalanceAlert;
    AlertDialog paymentSuccessAlert;
    AlertDialog paymentInfoAlert;
    AlertDialog passwordAlert;
    LayoutInflater inflater;
    View alertView;
    View paymentSuccess;
    View noBalanceView;
    View passwordView;
    SlidePagerAdapter slidePagerAdapter;
    ViewPager slidePager;
    public int [] companiesIcon ={
      R.mipmap.cafe_icon,
      R.mipmap.resturant_icon,
      R.mipmap.game_icon,
      R.mipmap.supermarket,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaser_main);
        progressbaralert = new AlertDialog.Builder(this).create();
        paymentSuccessAlert = new AlertDialog.Builder(this).create();
        paymentInfoAlert = new AlertDialog.Builder(this).create();
        noBalanceAlert = new AlertDialog.Builder(this).create();
        passwordAlert = new AlertDialog.Builder(this).create();
        alertView = View.inflate(this,R.layout.progress_dialog,null);
        paymentSuccess = View.inflate(getBaseContext(),R.layout.payment_successfully_done_dialog,null);
        noBalanceView = View.inflate(getBaseContext(),R.layout.payment_no_balance_dialog,null);
        passwordView = View.inflate(getBaseContext(),R.layout.password_dialog,null);
        inflater = getLayoutInflater();
        progressbaralert.setView(alertView);
        paymentSuccessAlert.setView(paymentSuccess);
        paymentInfoAlert.setView(paymentSuccess);
        noBalanceAlert.setView(noBalanceView);
        passwordAlert.setView(passwordView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        slidePager = (ViewPager) findViewById(R.id.pay_view_pager);
        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE, Context.MODE_PRIVATE);
        slidePagerAdapter = new SlidePagerAdapter(getBaseContext(),sharedPreferences,true,false);
        slidePager.setAdapter(slidePagerAdapter);
        slidePager.setOnPageChangeListener(viewPagerListener);
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
        //showPaymentInfo();
        NdefRecord record = messages.getRecords()[0];
        String payload = new String(record.getPayload());
        allGood = true;
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
        paymentInfo = gson.fromJson(payload, PaymentInfo.class);
        showPaymentInfo();
        paymentInfo.setPurchaserId(sharedPreferences.getString(Configuration.KEY_PURCHASER_ID,"null"));
        paymentInfo.setFlag(0);
        paymentInfo.setPurchaserName(sharedPreferences.getString(Configuration.KEY_PREFERENCE_USERNAME,"null"));
        paymentInfo.setSendFlag(0);
        ArrayList<PaymentInfo> paymentInfos = LocalDBA.getInstance(getApplicationContext()).getAllPaymentTransaction();
        int paymentTransaction;
        if (paymentInfos.size()>0) {
            paymentTransaction = Integer.valueOf(paymentInfos.get(paymentInfos.size() - 1).getTransactionId()) + 1;
        }
        else {
            paymentTransaction = 1;
        }
        paymentInfo.setUniqueId(Build.SERIAL+String.valueOf(paymentTransaction));

        /*if (allGood && passwordCheck && balanceCheck) {
            //NdefMessage ndefMessage = createNdefMessage();
            nfcAdapter.setNdefPushMessageCallback(this, this);

        }*/
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
        //showProgressBar(msg);
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
        LocalDBA localDBA = LocalDBA.getInstance(getApplicationContext());
        Log.d("sent","sent successfully");
        localDBA.getInstance(getApplicationContext()).insertPaymentTransaction(paymentInfo);
        localDBA.updateBalance(localDBA.getAllCards().get(pos).getCardBalance() - Double.valueOf(paymentInfo.getBillAmount())
                , localDBA.getAllCards().get(pos).getCardId());
        showPaymentSucessDialog();
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
        progressbaralert.show();
    }
    public void dismissProgressBar(String str){
        ProgressBar progressBar = alertView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        TextView msg = alertView.findViewById(R.id.progress_msg);
        msg.setText(str);
        progressbaralert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                progressbaralert.dismiss();
            }
        });
    }
    public void dismiss(){
        progressbaralert.dismiss();
    }


    public void showPaymentSucessDialog(){
        ImageView imageView = paymentSuccess.findViewById(R.id.success_icon);
        imageView.setVisibility(View.VISIBLE);
        TextView textView = paymentSuccess.findViewById(R.id.success_tv);
        textView.setVisibility(View.VISIBLE);
        TextView message = paymentSuccess.findViewById(R.id.sucess_message);
        TextView date = paymentSuccess.findViewById(R.id.date);
        TextView time = paymentSuccess.findViewById(R.id.time);
        TextView billAmount = paymentSuccess.findViewById(R.id.bill_amount);
        TextView sellerName = paymentSuccess.findViewById(R.id.seller_name);
        ImageView selelrIcon = paymentSuccess.findViewById(R.id.seller_icon);
        date.setText(paymentInfo.getStringDate());
        time.setText(paymentInfo.getStringTime());
        billAmount.setText(paymentInfo.getBillAmount()+" SDG");
        sellerName.setText(paymentInfo.getCompanyName());
        selelrIcon.setImageResource(companiesIcon[paymentInfo.getCompanyType()]);
        message.setText("Your Payment Transaction Was SuccessFull");
        paymentSuccessAlert.show();

        paymentSuccessAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                paymentSuccessAlert.dismiss();
            }
        });
    }
    public void showPaymentInfo(){
        ImageView imageView = paymentSuccess.findViewById(R.id.success_icon);
        imageView.setVisibility(View.GONE);
        TextView textView = paymentSuccess.findViewById(R.id.success_tv);
        textView.setVisibility(View.GONE);
        TextView message = paymentSuccess.findViewById(R.id.sucess_message);
        TextView date = paymentSuccess.findViewById(R.id.date);
        TextView time = paymentSuccess.findViewById(R.id.time);
        TextView billAmount = paymentSuccess.findViewById(R.id.bill_amount);
        TextView sellerName = paymentSuccess.findViewById(R.id.seller_name);
        ImageView selelrIcon = paymentSuccess.findViewById(R.id.seller_icon);
        date.setText(paymentInfo.getStringDate());
        time.setText(paymentInfo.getStringTime());
        billAmount.setText(paymentInfo.getBillAmount()+" SDG");
        sellerName.setText(paymentInfo.getCompanyName());
        selelrIcon.setImageResource(companiesIcon[paymentInfo.getCompanyType()]);
        message.setText("Your Payment Information");
        textView.setVisibility(View.GONE);
        paymentInfoAlert.show();
        paymentInfoAlert.setCancelable(true);
        paymentInfoAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                paymentInfoAlert.dismiss();
                if (LocalDBA.getInstance(getApplicationContext()).getAllCards().get(pos).getCardBalance() < Double.valueOf(paymentInfo.getBillAmount())) {
                    balanceCheck = false;
                    showNoBalanceAlert();
                }
                else{
                    balanceCheck = true;
                    showPasswordAlert();
                }

            }
        });
    }

            public void showNoBalanceAlert() {
                noBalanceAlert.show();
                noBalanceAlert.setCancelable(true);
                noBalanceAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        noBalanceAlert.dismiss();
                    }
                });
            }
            public void showPasswordAlert() {
                final EditText password = passwordView.findViewById(R.id.cardpin);
                passwordAlert.show();
                passwordAlert.setCancelable(true);
                Button btn = passwordView.findViewById(R.id.confirmpasswordBtn);
                final SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        passwordCheck = password.getText().toString().compareToIgnoreCase(sharedPreferences.getString(Configuration.KEY_PASSWORD,null)) == 0;
                        if (passwordCheck){
                            passwordAlert.dismiss();
                            if (allGood && passwordCheck && balanceCheck) {
                                paymentInfo.setCardId(LocalDBA.getInstance(getApplicationContext()).getAllCards().get(pos).getCardNo());
                                //NdefMessage ndefMessage = createNdefMessage();
                                nfcAdapter.setNdefPushMessageCallback(PurchaserMainActivity.this, PurchaserMainActivity.this);

                            }
                        }
                        else{
                            Toast.makeText(PurchaserMainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                passwordAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        passwordAlert.dismiss();
                    }
                });
            }

            ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Configuration.KEY_CARD_NO, LocalDBA.getInstance(getApplicationContext()).getAllCards().get(position).getCardNo());
                    editor.apply();
                    pos = position;
                    if (!balanceCheck && allGood) {
                        if (Double.valueOf(paymentInfo.getBillAmount()) < LocalDBA.getInstance(getApplicationContext()).getAllCards().get(position).getCardBalance()) {
                            balanceCheck = true;
                        } else {
                            showNoBalanceAlert();
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }

            };
}

