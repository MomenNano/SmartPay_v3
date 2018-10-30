package com.android.nfc.smartpay_v3.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import android.widget.Toast;


import com.android.nfc.smartpay_v3.Adapters.SlidePagerAdapter;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.ClassesManagers.CardPaymentInfoManager;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.DBA;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fifty on 5/6/2018.
 */

public class MyCardsFragment extends Fragment {
    View myView;
    CardPaymentHistoryAdapter adapter;
    RecyclerView recyclerView;
    SlidePagerAdapter slidePagerAdapter;
    View collapseView;
    ViewPager slidePager;
    AlertDialog alert;
    View alertView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.my_cards_fragment,container,false);
        alert = new AlertDialog.Builder(getActivity()).create();
        alertView = inflater.inflate(R.layout.progress_dialog,container,false);
        alert.setView(alertView);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.removeAllViewsInLayout();
        collapsingToolbarContent.setVisibility(View.VISIBLE);
        collapseView = inflater.inflate(R.layout.my_cards_collapse_layout,container,false);
        slidePager = (ViewPager) collapseView.findViewById(R.id.slidePager);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREFERENCE, Context.MODE_PRIVATE);
        slidePagerAdapter = new SlidePagerAdapter(getActivity().getBaseContext(),sharedPreferences,false,true);
        slidePager.setAdapter(slidePagerAdapter);
        slidePager.setOnPageChangeListener(viewPagerListener);
        recyclerView = (RecyclerView) myView.findViewById(R.id.card_payment_history_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My Cards");
        collapsingToolbarLayout.setTitle("My Cards");
        collapsingToolbarContent.addView(slidePager);
        Button deductBtn = (Button) myView.findViewById(R.id.deductBtn);
        deductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deduct();
            }
        });
        return myView;

    }

    public class CardPaymentHistoryAdapter extends Adapter<CardPaymentHistoryAdapter.PayHolder>{
        private List<PaymentInfo> paymentList;
        public class PayHolder extends RecyclerView.ViewHolder {
            TextView companeyNameTextView;
            TextView billAmountTextView;
            TextView paymentDateTextView;
            TextView paymentTimeTextView;
            ImageView companeyIcon;
            PaymentInfo paymentObj;

            public PayHolder(View itemView) {
                super(itemView);
                companeyNameTextView = (TextView) itemView.findViewById(R.id.company_name);
                billAmountTextView = (TextView) itemView.findViewById(R.id.bill_amount);
                paymentDateTextView = (TextView) itemView.findViewById(R.id.date);
                companeyIcon = (ImageView) itemView.findViewById(R.id.company_icon);
            }

        }

        public CardPaymentHistoryAdapter(List<PaymentInfo> list) {
            this.paymentList = list;
        }

        @Override
        public PayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.payment_info_card, parent, false);
            return new PayHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PayHolder holder, int position) {
            holder.paymentObj = paymentList.get(position);
            holder.companeyNameTextView.setText(holder.paymentObj.getCompanyName());
            holder.paymentDateTextView.setText(holder.paymentObj.getStringDate());
            holder.billAmountTextView.setText(holder.paymentObj.getBillAmount());
            if(holder.paymentObj.getCompanyType()==1)
                holder.companeyIcon.setImageResource(R.mipmap.desert_icon_round);

        }

        @Override
        public int getItemCount() {
            return this.paymentList.size();
        }

    }
    public void UpdateUI(int postion){
        ArrayList<Card> cardArrayList = LocalDBA.getInstance(getActivity().getApplicationContext()).getAllCards();
        ArrayList<PaymentInfo> paymentInfoArrayList = CardPaymentInfoManager.getPaymentInfoManager(getActivity().getBaseContext(),cardArrayList.get(postion).getCardId()).getPaymentInfosList();
        if (this.adapter == null){
            CardPaymentHistoryAdapter cAdapter = new CardPaymentHistoryAdapter(paymentInfoArrayList);
            adapter = cAdapter;
            this.recyclerView.setAdapter(adapter);
            return;
        }
        this.adapter.notifyDataSetChanged();
    }
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            UpdateUI(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public void deduct(){
        final EditText moneyToDeduct = (EditText) getView().findViewById(R.id.et_money_to_deduct);
        if (!moneyToDeduct.getText().toString().isEmpty()) {
            final double money = Double.valueOf(moneyToDeduct.getText().toString());
            final int position = slidePager.getCurrentItem();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.DEDUCT_MONEY_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString(Configuration.CODE).compareTo("1") == 0) {
                                    LocalDBA localDBA = new LocalDBA(getActivity().getBaseContext());
                                    ArrayList<Card> cardArrayList = localDBA.getAllCards();
                                    localDBA.updateBalance(cardArrayList.get(position).getCardBalance() + money, cardArrayList.get(position).getCardId());
                                    slidePagerAdapter.notifyDataSetChanged();
                                    dismissProgressBar(jsonObject.getString(Configuration.MESSAGE));
                                    dismiss();
                                    Toast.makeText(getActivity().getBaseContext(), jsonObject.getString(Configuration.MESSAGE), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.getString(Configuration.CODE).compareTo("0") == 0) {
                                    Toast.makeText(getActivity().getBaseContext(), jsonObject.getString(Configuration.MESSAGE), Toast.LENGTH_SHORT).show();
                                    dismissProgressBar(jsonObject.getString(Configuration.MESSAGE));
                                }
                                else {
                                    dismissProgressBar(jsonObject.getString(Configuration.MESSAGE));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity().getBaseContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show();
                    dismissProgressBar("Something went wrong try again");

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    LocalDBA localDBA = new LocalDBA(getActivity().getBaseContext());
                    ArrayList<Card> cardArrayList = localDBA.getAllCards();
                    Map<String, String> parms = new HashMap<>();
                    parms.put(Configuration.KEY_CARD_NO, cardArrayList.get(position).getCardNo());
                    parms.put(Configuration.KEY_CARD_BANK_NAME, cardArrayList.get(position).getBankName());
                    parms.put(Configuration.MONEY_TO_DEDUCT, moneyToDeduct.getText().toString());
                    parms.put(Configuration.KEY_CARD_HOLDER_NAME, cardArrayList.get(position).getCardHolderName());
                    return parms;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
            showProgressBar("Deducting Money...");
        }
        else {
            showProgressBar("Deducting Money");
            dismissProgressBar("Enter The amount of money you like to deduct");
        }
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


    public void dismiss(){
        alert.dismiss();
    }


}
