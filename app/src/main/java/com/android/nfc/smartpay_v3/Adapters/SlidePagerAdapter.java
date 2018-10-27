package com.android.nfc.smartpay_v3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import com.android.nfc.smartpay_v3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 4/15/2018.
 */

public class SlidePagerAdapter extends PagerAdapter {
    LayoutInflater layoutInflater;
    LocalDBA localDBA;
    ArrayList<Card> cardArrayList;
    Context context;
    SharedPreferences sharedPreferences;
    public SlidePagerAdapter(Context context, SharedPreferences sharedPreferences){
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        localDBA = new LocalDBA(context);
        cardArrayList = localDBA.getAllCards();
        if (cardArrayList.isEmpty()){
            getCardsFromServer();
            for (int i = 0 ; i<cardArrayList.size(); i++){
                localDBA.insertCard(cardArrayList.get(i));
            }
        }
    }

    public int [] slideImage= {
            R.drawable.fispngsmall,
            R.drawable.omb,
            R.drawable.bok
    };

    @Override
    public int getCount() {
        return cardArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideIcon = (ImageView) view.findViewById(R.id.slider_latout_card_image);
        TextView cardBalance = (TextView) view .findViewById(R.id.card_balance);
        slideIcon.setImageResource(slideImage[cardArrayList.get(position).getCardIcon()]);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Configuration.MY_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Configuration.KEY_CARD_NO,cardArrayList.get(position).getCardNo());
        editor.apply();
        cardBalance.setText(String.valueOf(cardArrayList.get(position).getCardBalance())+" SDG");
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
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
                                cardArrayList.add(card);
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
