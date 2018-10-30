package com.android.nfc.smartpay_v3.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.ClassesManagers.CardInfoManager;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fifty on 5/6/2018.
 */

public class WalletFragment extends Fragment {
    View myView;
    CardsAdapter adapter;
    RecyclerView recyclerView;

    public int [] slideImage= {
            R.mipmap.fisal_bank,
            R.mipmap.omb_card,
            R.mipmap.bok_card
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.wallet_fragment_layout,container,false);
        View collapseView = inflater.inflate(R.layout.wallet_collapse_layout,container,false);
        TextView textView = collapseView.findViewById(R.id.wallet_balance);
        textView.setText(String.valueOf(LocalDBA.getInstance(getActivity().getApplicationContext()).getWalletBalance())+" SDG");
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.removeAllViewsInLayout();
        collapsingToolbarContent.setVisibility(View.VISIBLE);
        collapsingToolbarContent.addView(collapseView,params);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My Wallet");
        collapsingToolbarLayout.setTitle("My Wallet");
        recyclerView = (RecyclerView) myView.findViewById(R.id.wallet_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        UpdateUI();
        return myView;

    }



    public class CardsAdapter extends Adapter<CardsAdapter.CardHolder>{
        private List<Card> cardsList;
        public class CardHolder extends RecyclerView.ViewHolder {
            TextView cardBankName;
            TextView cardNo;
            ImageView cardIcon;
            Card card = new Card();

            public CardHolder(View itemView) {
                super(itemView);
                cardBankName = (TextView) itemView.findViewById(R.id.card_bank);
                cardNo = (TextView) itemView.findViewById(R.id.card_no);
                cardIcon = (ImageView) itemView.findViewById(R.id.card_icon);
            }

        }

        public CardsAdapter(List<Card> list) {
            this.cardsList = list;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.cards_cardview, parent, false);
            return new CardHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.card = cardsList.get(position);
            holder.cardBankName.setText(holder.card.getBankName());
            holder.cardNo.setText("xxxx xxxx xxxx - "+holder.card.getCardId());
            holder.cardIcon.setImageResource(slideImage[holder.card.getCardIcon()]);

        }

        @Override
        public int getItemCount() {
            return this.cardsList.size();
        }
        public void swap(ArrayList<Card> newList) {
            if(newList == null || newList.size()==0)
                return;
            if (cardsList != null && cardsList.size()>0)
                cardsList.clear();
            cardsList.addAll(newList);
            notifyDataSetChanged();
        }
    }
    public void UpdateUI(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREFERENCE, Context.MODE_PRIVATE);
        ArrayList<Card> cardArrayList = CardInfoManager.getCardsInfoManager(getActivity().getBaseContext(),sharedPreferences).getCardsInfoList();
        if (cardArrayList.isEmpty()){
            return;
        }
        if (this.adapter == null && !cardArrayList.isEmpty()){
            CardsAdapter cAdapter = new CardsAdapter(cardArrayList);
            adapter = cAdapter;
            this.recyclerView.setAdapter(adapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        this.adapter.swap(cardArrayList);
        this.adapter.notifyDataSetChanged();
    }

}
