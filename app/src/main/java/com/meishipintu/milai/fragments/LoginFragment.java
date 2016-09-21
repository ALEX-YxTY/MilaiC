package com.meishipintu.milai.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.LoginActivity;
import com.meishipintu.milai.utils.ConstansUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/10.
 */
public class LoginFragment extends Fragment {

    private static LoginFragment instance;

    @OnClick(R.id.ll_login)
    void login() {
        //进入登陆界面
        Intent intent = new Intent(getContext(), LoginActivity.class);
        getActivity().startActivityForResult(intent, ConstansUtils.LOGGING_SITUATION);
    }

    public static LoginFragment getInstance() {
        if (instance == null) {
            instance = new LoginFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
