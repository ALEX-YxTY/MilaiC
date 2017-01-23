package com.meishipintu.milai.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.TaskDetailActivity;
import com.meishipintu.milai.activitys.WelfareDetailNewActivity;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class BannerAdapter extends PagerAdapter {

    private List<ImageView> convertViewList;
    private Picasso picasso;
    private Context context;
    private List<Task> dataList;
    private int[] colorForegroundArray,colorButtonArray;

    public BannerAdapter (Context context, List<Task> dataList) {
        this.context = context;
        this.dataList = dataList;
        picasso = Picasso.with(context);
        convertViewList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (dataList.size() > 0) {
            final Task task = dataList.get(position % dataList.size());
            colorForegroundArray = context.getResources().getIntArray(R.array.colorForeground);
            colorButtonArray = context.getResources().getIntArray(R.array.colorBottom);
            ImageView view;
            if (convertViewList.size() > 0) {
                view = convertViewList.remove(0);
            } else {
                view = new ImageView(context);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            picasso.load("http://" + task.getLogo()).placeholder(R.drawable.error_holder).into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    String url;
                    switch (Integer.valueOf(task.getType())) {
                        case 3:         //空，弹出说明文字
                            ToastUtils.show(context , task.getType_detail());
                            break;
                        case 1:         //正常活动
                            intent = new Intent(context, WelfareDetailNewActivity.class);
                            intent.putExtra("data", task);
                            intent.putExtra("foregroundColor", colorForegroundArray[position % colorForegroundArray.length]);
                            intent.putExtra("buttonColor", colorButtonArray[position % colorButtonArray.length]);
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.anim_in_scale, R.anim.anim_out_alpha);
                            break;
                        case 2:         //外链
                            url = task.getType_detail();
                            intent = new Intent(context, TaskDetailActivity.class);
                            intent.putExtra("detail", url);
                            intent.putExtra("type", 2);     //外链
                            context.startActivity(intent);
                            break;
                    }
                }
            });
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。  
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        convertViewList.add((ImageView) object);
    }

    @Override
    public int getCount() {
        if (dataList.size() == 1 || dataList.size() == 0) {
            return 1;
        }
        return 1000 ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
