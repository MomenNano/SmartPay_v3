package com.android.nfc.smartpay_v3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.ClassesManagers.PaymentHistoryInfoManager;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fifty on 7/3/2018.
 */

public class PaymentInfoAdapter extends RecyclerView.Adapter<PaymentInfoAdapter.PayHolder> {
    private List<PaymentInfo> paymentList;
    public class PayHolder extends RecyclerView.ViewHolder {
        TextView companeyNameTextView;
        TextView billAmountTextView;
        TextView paymentDateTextView;
        TextView paymentTimeTextView;
        Button stateBtn;
        ImageView companeyIcon;
        PaymentInfo paymentObj;

        public PayHolder(View itemView) {
            super(itemView);
            companeyNameTextView = (TextView) itemView.findViewById(R.id.company_name);
            billAmountTextView = (TextView) itemView.findViewById(R.id.bill_amount);
            paymentDateTextView = (TextView) itemView.findViewById(R.id.date);
            companeyIcon = (ImageView) itemView.findViewById(R.id.company_icon);
            stateBtn = (Button) itemView.findViewById(R.id.state);
        }

    }

    public PaymentInfoAdapter(List<PaymentInfo> list) {
        this.paymentList = list;
    }

    @Override
    public PayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_info_card, parent, false);
        return new PayHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayHolder holder, int position) {

        holder.paymentObj = paymentList.get(position);
        holder.companeyNameTextView.setText(holder.paymentObj.getCompanyName());
        holder.paymentDateTextView.setText(holder.paymentObj.getStringDate());
        holder.billAmountTextView.setText(String.valueOf(holder.paymentObj.getBillAmount())+" SDG");
        if (holder.paymentObj.getFlag()==1){
            holder.stateBtn.setBackgroundResource(R.drawable.state_true);
            holder.stateBtn.setText("Completed");
        }
        else {
            holder.stateBtn.setBackgroundResource(R.drawable.state_false);
            holder.stateBtn.setText("Not Completed");
        }
            if(holder.paymentObj.getCompanyType()==0)
                holder.companeyIcon.setImageResource(R.mipmap.cafe_icon);
            else if (holder.paymentObj.getCompanyType()==1)
                holder.companeyIcon.setImageResource(R.mipmap.resturant_icon);
            else if (holder.paymentObj.getCompanyType()==2)
                holder.companeyIcon.setImageResource(R.mipmap.game_icon);
            else if (holder.paymentObj.getCompanyType()==3)
                holder.companeyIcon.setImageResource(R.mipmap.supermarket);

    }

    @Override
    public int getItemCount() {
        return this.paymentList.size();
    }
    public void swap(ArrayList<PaymentInfo> newList) {
        if(newList == null || newList.size()==0)
            return;
        if (paymentList != null && paymentList.size()>0)
            paymentList.clear();
        paymentList.addAll(newList);
        notifyDataSetChanged();
    }

}
