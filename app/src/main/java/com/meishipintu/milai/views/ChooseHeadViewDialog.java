package com.meishipintu.milai.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.meishipintu.milai.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/9/28.
 */

public class ChooseHeadViewDialog extends Dialog {

    OnItemClickListener listener;

    public ChooseHeadViewDialog(Context context, int themeResId, OnItemClickListener listener) {
        super(context, themeResId);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view_select);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_round_cornerl);
    }

    @OnClick({R.id.tv_from_camera, R.id.tv_from_album})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_from_camera:
                listener.onClickCamera(view, this);
                break;
            case R.id.tv_from_album:
                listener.onClickAlbum(view, this);
                break;
        }
    }

    public interface OnItemClickListener {
        void onClickCamera(View view, Dialog dialog);

        void onClickAlbum(View view, Dialog dialog);
    }

}
