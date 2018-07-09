package com.android.nfc.smartpay_v3.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Fifty on 5/6/2018.
 */

public class PayFragment extends Fragment {
    View myView;
    private NfcAdapter nfcAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.pay_fragment,container,false);
        PackageManager pm = getActivity().getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(getActivity().getBaseContext(), "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(getActivity().getBaseContext(), "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(getActivity().getBaseContext(), "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
            ImageView imageView = (ImageView) myView.findViewById(R.id.paymentCardimageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageSendSuccessfully();
                }
            });
        }
        return myView;

    }

    public void sendFile() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity().getBaseContext());

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(getActivity().getBaseContext(), "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(getActivity().getBaseContext(), "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // NFC and Android Beam both are enabled

            // File to be transferred
            // For the sake of this tutorial I've placed an image
            // named 'wallpaper.png' in the 'Pictures' directory
            String fileName = "wallpaper.png";

            // Retrieve the path to the user's public pictures directory
            File fileDirectory = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);

            // Create a new file using the specified directory and name
            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, getActivity());
        }
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
    public void messageSendSuccessfully(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertView = inflater.inflate(R.layout.payment_successfully_done_dialog,null);
        alert.setView(alertView);
        alert.create().show();

    }
}
