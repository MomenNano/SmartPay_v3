package com.android.nfc.smartpay_v3.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.android.nfc.smartpay_v3.Adapters.PaymentInfoAdapter;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.ClassesManagers.PaymentInfoManager;
import com.android.nfc.smartpay_v3.R;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    View myView;
    RecyclerView recyclerView;
    PaymentInfoAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.history_fragment, container, false);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.setVisibility(View.GONE);
        collapsingToolbarContent.removeAllViewsInLayout();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Map");
        collapsingToolbarLayout.setTitle("Map");
        recyclerView = (RecyclerView) myView.findViewById(R.id.history_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        updateUI();
        return myView;
    }
    public void updateUI(){
        ArrayList<PaymentInfo> massageArrayList = PaymentInfoManager.getMassagesManeger(getActivity().getBaseContext()).getPaymentInfoArrayList();
        if (adapter==null){
            adapter = new PaymentInfoAdapter(massageArrayList);
            recyclerView.setAdapter(adapter);
            return;
        }
        adapter.swap(massageArrayList);
        adapter.notifyDataSetChanged();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.side_nav_bar);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorTransparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
