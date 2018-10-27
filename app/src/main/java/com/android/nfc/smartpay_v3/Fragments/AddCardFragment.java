package com.android.nfc.smartpay_v3.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.Adapters.BankAdapter;
import com.android.nfc.smartpay_v3.Classes.BankItem;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import com.android.nfc.smartpay_v3.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 5/6/2018.
 */

public class AddCardFragment extends Fragment {
    View myView;
    BankAdapter bankAdapter;
    ArrayList<BankItem> bankItemArrayList;
    TextView tv_cardHolderName;
    TextView tv_cardNo;
    TextView tv_cardBankName;
    TextView tv_exDate;
    Spinner spinner;
    EditText et_cardPassword;
    AlertDialog alert;
    View alertView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.add_card_fragment,container,false);
        alert = new AlertDialog.Builder(getActivity().getBaseContext()).create();
        alertView = View.inflate(getActivity().getBaseContext(),R.layout.progress_dialog,null);
        alert.setView(alertView);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.setVisibility(View.GONE);
        collapsingToolbarContent.removeAllViewsInLayout();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Map");
        collapsingToolbarLayout.setTitle("Map");
        tv_cardHolderName = (TextView) myView.findViewById(R.id.card_holder_name_tv);
        tv_cardNo = (TextView) myView.findViewById(R.id.card_number_tv);
        tv_exDate = (TextView) myView.findViewById(R.id.expire_date_tv);
        tv_cardBankName = (TextView) myView.findViewById(R.id.bank_name_tv);
        final EditText et_cardHolderName = (EditText) myView.findViewById(R.id.card_holder_name_et);
        EditText et_cardNo = (EditText) myView.findViewById(R.id.card_number_et);
        EditText et_exDate = (EditText) myView.findViewById(R.id.expire_date_et);
        et_cardPassword = (EditText) myView.findViewById(R.id.card_password_et);

        et_cardHolderName.addTextChangedListener(new TextWatcher() {
            boolean card_holder_name_boolean = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(et_cardHolderName.getText().toString().isEmpty()){
                    tv_cardHolderName.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(card_holder_name_boolean==true){
                    return;
                }
                card_holder_name_boolean = true;
                tv_cardHolderName.setText(s);
                card_holder_name_boolean = false;
            }
        });
        et_cardNo.addTextChangedListener(new TextWatcher() {
            boolean card_No_boolean = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(card_No_boolean==true){
                    return;
                }
                card_No_boolean = true;
                tv_cardNo.setText(s);
                card_No_boolean = false;
            }
        });
        et_exDate.addTextChangedListener(new TextWatcher() {
            boolean card_exDate_boolean = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(et_cardHolderName.getText().toString().isEmpty()){
                    tv_cardHolderName.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(card_exDate_boolean==true){
                    return;
                }
                card_exDate_boolean = true;
                if((s.length()+1)%4==0) {
                    tv_exDate.setText(s + " "+"4");
                }
                else {
                    tv_exDate.setText(s);
                }
                card_exDate_boolean = false;
            }
        });

        initBanksList();
        spinner = (Spinner) myView.findViewById(R.id.banksspinner);
        bankAdapter = new BankAdapter(getActivity().getBaseContext(),bankItemArrayList);
        spinner.setAdapter(bankAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BankItem selectedBank = (BankItem) parent.getItemAtPosition(position);
                String selectedBankName = selectedBank.getBankName();
                Toast.makeText(getActivity().getBaseContext(),"Selected Bank is "+selectedBankName,Toast.LENGTH_SHORT).show();
                tv_cardBankName.setText(selectedBankName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button addCard = (Button) myView.findViewById(R.id.addNewCardBtn);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard();
            }
        });
        return myView;

    }

    private void initBanksList() {
        bankItemArrayList = new ArrayList<BankItem>();
        bankItemArrayList.add(new BankItem("Fisal Islamic Bank",R.mipmap.fisal_bank_icon));
        bankItemArrayList.add(new BankItem("Omdurman National Bank",R.mipmap.omdurman_bank_icon));
        bankItemArrayList.add(new BankItem("Khartoum National Bank",R.mipmap.khartoum_bank_icon));
    }
    public void addNewCard(){
        String cardHolderName = tv_cardHolderName.getText().toString();
        String cardBankName= tv_cardBankName.getText().toString();
        String dbName="";
        if (tv_cardBankName.getText().toString().compareToIgnoreCase("Fisal Islamic Bank")==0){
            dbName = "faisal_bankdb";
        } else if (tv_cardBankName.getText().toString().compareToIgnoreCase("Omdurman National Bank")==0){
            dbName = "omdurman_bankdb";
        } else if (tv_cardBankName.getText().toString().compareToIgnoreCase("Khartoum National Bank")==0){
            dbName = "khartoum_bankdb";
        }
        String cardExDate = tv_exDate.getText().toString();
        String cardNo = tv_cardNo.getText().toString();
        String cardPassword = et_cardPassword.getText().toString();
        if(cardHolderName.isEmpty() || cardBankName.isEmpty() || cardExDate.isEmpty() || cardNo.isEmpty() == false) {
            Log.d("Log:",cardHolderName+"||"+cardBankName+"||"+cardExDate+"||"+cardNo);
            Card card = new Card();
            card.setCardHolderName(cardHolderName);
            card.setCardNo(cardNo);
            card.setBankName(cardBankName);
            card.setStringBankName(dbName);
            card.setExDate(cardExDate);
            card.setCardIcon(spinner.getSelectedItemPosition());
            card.setPassword(cardPassword);
            addCardToServer(card);
            //showProgressBar("Validating The Card...");
        }
        else{
            Toast.makeText(getActivity().getBaseContext(),"Sorry you need to fill all the field first",Toast.LENGTH_SHORT).show();
        }
    }
    public void addCardToServer(final Card card){ //send new Card information to the server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.ADD_NEW_CARD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(Configuration.CODE).compareTo("1") == 0){
                                LocalDBA localDBA = new LocalDBA(getActivity().getBaseContext());
                                localDBA.insertCard(card);
                                Toast.makeText(getActivity().getBaseContext(), jsonObject.getString(Configuration.KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                //dismissProgressBar(jsonObject.getString(Configuration.KEY_MESSAGE));
                            }
                            else if (jsonObject.getString(Configuration.CODE).compareTo("0") == 0){
                                Toast.makeText(getActivity().getBaseContext(),jsonObject.getString(Configuration.KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                //dismissProgressBar(jsonObject.getString(Configuration.KEY_MESSAGE));
                            }
                            else if (jsonObject.getString(Configuration.KEY_RESULT).compareTo("2") == 0){
                                Toast.makeText(getActivity().getBaseContext(),jsonObject.getString(Configuration.KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                //dismissProgressBar(jsonObject.getString(Configuration.KEY_MESSAGE));
                            }
                            else {
                                Toast.makeText(getActivity().getBaseContext(), "Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show();
                                //dismissProgressBar("Check Your Internet Connection And Try Again");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getBaseContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show();
                //dismissProgressBar("Check Your Internet Connection And Try Again");
            }

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREFERENCE,Context.MODE_PRIVATE);
                Map<String,String> parms = new HashMap<>();
                parms.put(Configuration.KEY_CARD_NO,card.getCardNo());
                parms.put(Configuration.KEY_CARD_BANK_NAME,card.getStringBankName());
                parms.put(Configuration.KEY_CARD_HOLDER_NAME,card.getCardHolderName());
                parms.put(Configuration.KEY_CARD_EX_DATE,card.getExDate());
                parms.put(Configuration.KEY_PHONE_SERIAL_NO,sharedPreferences.getString(Configuration.KEY_PHONE_SERIAL_NO,""));
                parms.put(Configuration.KEY_PURCHASER_ID,sharedPreferences.getString(Configuration.KEY_PREFERENCE_USER_ID,""));
                parms.put(Configuration.KEY_PASSWORD,card.getPassword());
                return parms;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

    }
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
}
