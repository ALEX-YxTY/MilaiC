package com.meishipintu.milai.fragments;


import android.os.Bundle;
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
 * Created by Administrator on 2016/9/29 0029.
 */

public class CouponFragment extends Fragment {
    private RecyclerView rv;
    private MyCouponAdapter adapter;
    private List<Coupon> coupon;

    public void setArguments(List<Coupon> coupon) {
        this.coupon=coupon;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  LayoutInflater.from(getActivity()).inflate(R.layout.activity_coupon, null);

        rv= (RecyclerView) view.findViewById(R.id.rv);
       initData();
        return view;
    }
    private void initData() {

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyCouponAdapter(getActivity(),coupon);
        rv.setAdapter(adapter);

    }

    public void refreshUI() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
