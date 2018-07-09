package com.android.nfc.smartpay_v3.Activities;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.R;
//import java.nio.charset.Charset;

/*
* check if the NdefMessage callback excute on onCreate() or on onResume
* */

public class PurchaserMainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private boolean isArriveSuccessfully = false;
    private TextView PurchaserMessage;
    private NdefMessage ndefMessage;
    boolean allGood = false;
    protected String externalType = "nfclab.com:SmartPay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaser_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null){
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Plese Enable your NFC.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }

        if(!nfcAdapter.isNdefPushEnabled()){
            Toast.makeText(this, "Plese Enable Android Beam.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        PurchaserMessage = (TextView) findViewById(R.id.PurchaserMessage);
        if(Build.VERSION.SDK_INT >= 21){
            //this.invokeBeam();
        }
        /*if(allGood){

            }*/
        ndefMessage = createNdefMessage();
        nfcAdapter.setNdefPushMessage(ndefMessage,this);

        //nfcAdapter.setOnNdefPushCompleteCallback(null,this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void invokeBeam(){
        nfcAdapter.invokeBeam(this);
    }
    /*@TargetApi(Build.VERSION_CODES.M)
    protected boolean checkFingerprint(){
        if((int) Build.VERSION.SDK_INT >= 23){
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if(fingerprintManager.isHardwareDetected()) {

            }
        }
        return false;
    }*/

    private NdefMessage createNdefMessage() {
        String done = "Done";
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,externalType.getBytes(), new byte[0], done.getBytes());
        NdefMessage message= new NdefMessage(new NdefRecord[] {textRecord,NdefRecord.createApplicationRecord("com.android.nfc.purchasersmartpay")});
        return message;
    }

    @Override
    protected void onResume() {
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Plese Enable your NFC.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }

        if(!nfcAdapter.isNdefPushEnabled()){
            Toast.makeText(this, "Plese Enable Android Beam.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            receiveIntent(getIntent());      }

        if(allGood){
            ndefMessage = createNdefMessage();
            nfcAdapter.setNdefPushMessage(ndefMessage,this);
        }
    }

    private void receiveIntent(Intent intent) {
        NdefMessage messages = getNdefMessages(getIntent());

        NdefRecord record = messages.getRecords()[0];
        //String payload = new String (record.getPayload(),1,record.getPayload().length-1, Charset.forName("UTF-8"));
        //String payload = new String (record.getPayload(),0,record.getPayload().length, Charset.forName("UTF-8"));
        String payload = new String (record.getPayload());
        allGood = true;
        PurchaserMessage.setText(payload);

    }

    protected NdefMessage getNdefMessages(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        //if (rawMsgs != null){
        //NdefMessage msg = new NdefMessage[rawMsgs.length];
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        //}
            /*else {
                byte [] empty = new byte [] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,empty,empty,empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage [] {
                        msg
                };

            }*/

        /*else{
            Log.d("Error" , "UNknown intent");
            finish();
        }*/
       /* String lol = new String (msg.getRecords()[0].getPayload());
        PurchaserMessage.setText(lol);*/

        return msg;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    /*@Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        return null;
    }*/
}
