package com.meishipintu.milai.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyCouponAdapter;
import com.meishipintu.milai.beans.Coupon;

import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */

public class MyIncomeFragment extends Fragment {

    private RecyclerView rv;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  LayoutInflater.from(getActivity()).inflate(R.layout.activity_coupon, null);
        rv= (RecyclerView) view.findViewById(R.id.rv);
        if (getArguments().get("adapter") != null) {
            adapter = (RecyclerView.Adapter) getArguments().get("adapter");
        }
        initRv();
        return view;
    }

    private void initRv() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        if (adapter != null) {
            rv.setAdapter(adapter);
        }
    }

}
