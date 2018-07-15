package com.android.nfc.smartpay_v3.Activities;

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

import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.R;
import com.google.gson.Gson;

import java.util.Date;

public class PurchaserMainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private TextView PurchaserMessage;
    private NdefMessage ndefMessage;
    boolean allGood = false;
    protected String externalType = "nfclab.com:SmartPay";
    PaymentInfo paymentInfo = new PaymentInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaser_main);

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

    private NdefMessage createNdefMessage() {
        Gson gson = new Gson();

        String msg = gson.toJson(paymentInfo);
        //Setup NdefRecord
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], msg.getBytes());
        NdefMessage message = new NdefMessage(textRecord);
        return message;
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
        paymentInfo = gson.fromJson(payload, PaymentInfo.class);

        paymentInfo.setBillAmount(paymentInfo.getBillAmount());
        paymentInfo.setCompanyName("lol");
        paymentInfo.setCompanyType(123);
        paymentInfo.setPaymentDate(new Date());

        if (allGood) {
            ndefMessage = createNdefMessage();
            nfcAdapter.setNdefPushMessage(ndefMessage, this);
        }
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
}
