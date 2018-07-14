package com.android.nfc.smartpay_v3.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.Classes.BillInfo;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PayActivity extends Activity {
    private NfcAdapter nfcAdapter;
    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<byte[]> messagesReceivedArray = new ArrayList<>();
    /*BillInfo billInfo = new BillInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        PackageManager pm = getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(getBaseContext(), "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(getBaseContext(), "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(getBaseContext(), "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(getBaseContext());

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(getBaseContext(), "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(getBaseContext(), "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        if(nfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            nfcAdapter.setNdefPushMessageCallback(this, this);
            //This will be called if the message is sent successfully
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }


    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord recordsToAttatch = createRecords();
        return new NdefMessage(recordsToAttatch);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Toast.makeText(this, "Message has been send Successfully", Toast.LENGTH_LONG).show();
        messageSendSuccessfully();
    }


    public NdefRecord createRecords(){
        NdefRecord record ;
        PaymentInfo paymentInfo = new PaymentInfo();
        Date today = new Date();
        paymentInfo.setCompanyName(billInfo.getCompaney().getName());
        paymentInfo.setBillAmount(billInfo.getBillAmount());
        paymentInfo.setCompanyType(billInfo.getCompaney().getType());
        paymentInfo.setPaymentDate(today);
        byte[] payload ;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            try {
                payload = serialize(paymentInfo);
                record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,"PaymentInfo".getBytes(), payload);
                return record;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                payload = serialize(paymentInfo);
                record = NdefRecord.createMime("text/plain",payload);
                return record;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;

    }
    private void handleNfcIntent(Intent NfcIntent){

        if(nfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())){
            Parcelable [] reciveArray = NfcIntent.getParcelableArrayExtra(nfcAdapter.EXTRA_NDEF_MESSAGES);
            if (reciveArray != null){
                messagesReceivedArray.clear();
                NdefMessage recivedMessage = (NdefMessage) reciveArray[0];
                NdefRecord [] attachedRecords = recivedMessage.getRecords();
                for( NdefRecord record:attachedRecords){
                    byte [] payloaad = record.getPayload();
                    if (payloaad.equals(getPackageName())){
                        continue;
                    }
                    String recordID = Arrays.toString(record.getId());
                    if (recordID.compareToIgnoreCase("PaymentInfo")==0){
                        receivedBillInfo(record.getPayload());
                    }
                    else {

                        messagesReceivedArray.add(payloaad);
                        Toast.makeText(this, "Received " + messagesReceivedArray.size() + " Messages", Toast.LENGTH_LONG).show();
                        //the Message Received Successfully
                        messageReceived();
                    }
                }
            }
            else {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void messageReceived() {
        PaymentInfo paymentInfo;
        DBA dba = new DBA();
        try {
            paymentInfo = (PaymentInfo) deserialize(messagesReceivedArray.get(0));
            dba.insertPaymentInfoToDB(paymentInfo);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View alertView = inflater.inflate(R.layout.payment_successfully_done_dialog,null);
            alert.setView(alertView);



            alert.create().show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void receivedBillInfo(byte [] payload){
        try {
            billInfo = (BillInfo) deserialize(payload);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void messageSendSuccessfully() {

    }*/
}
