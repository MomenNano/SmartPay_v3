package com.android.nfc.smartpay_v3.Fragments;

import android.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.ClassesManagers.CardPaymentInfoManager;
import com.android.nfc.smartpay_v3.R;
import com.android.nfc.smartpay_v3.Adapters.SlidePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.my_cards_fragment,container,false);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.removeAllViewsInLayout();
        collapseView = inflater.inflate(R.layout.my_cards_collapse_layout,container,false);
        slidePager = (ViewPager) collapseView.findViewById(R.id.slidePager);
        slidePagerAdapter = new SlidePagerAdapter(getActivity().getBaseContext());
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
        UpdateUI();
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
                paymentTimeTextView = (TextView) itemView.findViewById(R.id.payment_time);
                paymentDateTextView = (TextView) itemView.findViewById(R.id.payment_date);
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
            holder.paymentObj = paymentList.get(position);
            holder.companeyNameTextView.setText(holder.paymentObj.getCompaneyName());
            holder.paymentDateTextView.setText(dateFormat.format(holder.paymentObj.getPaymentDate()));
            holder.paymentTimeTextView.setText(timeFormat.format(holder.paymentObj.getPaymentDate()));
            holder.billAmountTextView.setText(String.valueOf(holder.paymentObj.getBillAmount()));
            if(holder.paymentObj.getCompaneyType()==1)
                holder.companeyIcon.setImageResource(R.mipmap.coffee_icon);

        }

        @Override
        public int getItemCount() {
            return this.paymentList.size();
        }

    }
    public void UpdateUI(){
        ArrayList<PaymentInfo> paymentInfoArrayList = CardPaymentInfoManager.getPaymentInfoManager(getActivity().getBaseContext()).getPaymentInfosList();
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

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public void deduct(){
        EditText moneyToDeduct = (EditText) getView().findViewById(R.id.et_money_to_deduct);
        double money = Double.valueOf(moneyToDeduct.getText().toString());
        DBA dba = new DBA();
        int result = dba.deductMoneyFromBank(money);
        if (result == 1){
            int position = slidePager.getCurrentItem();
            LocalDBA localDBA = new LocalDBA(getActivity().getBaseContext());
            ArrayList<Card> cardArrayList = localDBA.getAllCards();
            localDBA.updateBalance(cardArrayList.get(position).getCardBalance()+money,cardArrayList.get(position).getCardNo());
            slidePagerAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity().getBaseContext(), "Yor money has been deducted successfully", Toast.LENGTH_SHORT).show();
        }
        else if(result == 2){
            Toast.makeText(getActivity().getBaseContext(), "Sorry you bank Account doesn't have this amount of money", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity().getBaseContext(), "Sorry something went wrong check your internet connection and try again", Toast.LENGTH_SHORT).show();
        }
    }


}
