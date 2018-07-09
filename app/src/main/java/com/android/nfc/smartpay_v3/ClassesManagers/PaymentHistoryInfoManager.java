package com.android.nfc.smartpay_v3.ClassesManagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.nfc.smartpay_v3.Classes.Companey;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 5/8/2018.
 */

public class PaymentHistoryInfoManager {
    Context context;
    SharedPreferences sharedPreferences;
    private static PaymentHistoryInfoManager paymentInfoManager;
    private ArrayList<PaymentInfo> paymentInfosList = new ArrayList<PaymentInfo>();
    public PaymentHistoryInfoManager(Context context,SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        getPaymentHistoryFromServer();
        paymentInfoManager = this;
    }
    public ArrayList<PaymentInfo> getPaymentInfosList(){
        return paymentInfosList;
    }

    public static PaymentHistoryInfoManager getPaymentInfoManager(Context context,SharedPreferences sharedPreferences){
        if(paymentInfoManager==null){
            paymentInfoManager = new PaymentHistoryInfoManager(context,sharedPreferences);
        }

        return paymentInfoManager;
    }
    public void getPaymentHistoryFromServer(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Configuration.GET_STORES_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        while (count<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(count);
                                PaymentInfo paymentInfo = new PaymentInfo();
                                paymentInfo.setBillAmount(jsonObject.getDouble(Configuration.KEY_PAYMENT_BILL_AMOUNT));
                                paymentInfo.setCompaneyName(jsonObject.getString(Configuration.KEY_PAYMENT_COMPANY_NAME));
                                paymentInfo.setCompaneyType(jsonObject.getInt(Configuration.KEY_PAYMENT_COMPANY_TYPE));
                                paymentInfo.setStringDate(jsonObject.getString(Configuration.KEY_PAYMENT_DATE));
                                paymentInfo.setStringTime(jsonObject.getString(Configuration.KEY_PAYMENT_TIME));
                                paymentInfosList.add(paymentInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            count++;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Check Your Internet Connection And Open The Activity Again", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parms = new HashMap<>();
                parms.put(Configuration.KEY_USER_ID,sharedPreferences.getString(Configuration.KEY_PREFERENCE_USER_ID,null));
                return parms;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

}
