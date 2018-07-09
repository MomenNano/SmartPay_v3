package com.android.nfc.smartpay_v3.ClassesManagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.DBA.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 5/8/2018.
 */

public class CardInfoManager {
    private static CardInfoManager cardInfoManager;
    private ArrayList<Card> cardInfoList ;
    SharedPreferences sharedPreferences;
    Context context;
    public CardInfoManager(Context context, SharedPreferences sharedPreferences){
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        LocalDBA localDBA = new LocalDBA(context);
        ArrayList cardsList = localDBA.getAllCards();
        if (cardsList.isEmpty()){

        }
        cardInfoManager = this;
        this.cardInfoList = cardsList ;

    }
    public ArrayList<Card> getCardsInfoList(){
        return cardInfoList;
    }

    public static CardInfoManager getCardsInfoManager(Context context,SharedPreferences sharedPreferences){
        if(cardInfoManager==null){
            cardInfoManager = new CardInfoManager(context,sharedPreferences);
        }

        return cardInfoManager;
    }
    public void getCardsFromServer(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Configuration.GET_STORES_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        while (count<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(count);
                                Card card = new Card();
                                card.setBankName(jsonObject.getString(Configuration.KEY_CARD_BANK_NAME));
                                card.setCardHolderName(jsonObject.getString(Configuration.KEY_CARD_HOLDER_NAME));
                                card.setCardBalance(jsonObject.getDouble(Configuration.KEY_CARD_BALANCE));
                                card.setCardNo(jsonObject.getString(Configuration.KEY_CARD_NO));
                                card.setCardIcon(jsonObject.getInt(Configuration.KEY_CARD_ICON));
                                cardInfoList.add(card);
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
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put(Configuration.KEY_USER_ID, sharedPreferences.getString(Configuration.KEY_PREFERENCE_USER_ID, null));
                return parms;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

}
