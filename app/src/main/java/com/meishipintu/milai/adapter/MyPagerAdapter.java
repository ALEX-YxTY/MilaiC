package com.meishipintu.milai.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.TaskDetailActivity;
import com.meishipintu.milai.activitys.WelfareDetailNewActivity;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */

public class MyPagerAdapter extends PagerAdapter {

    private List<View> convertViewList;
    private Picasso picasso;
    private Context context;
    private List<Task> dataList;
    private int[] colorForegroundArray,colorButtonArray;

    public MyPagerAdapter(Context context, List<Task> dataList) {
        this.context = context;
        this.dataList = dataList;
        picasso = Picasso.with(context);
        convertViewList = new ArrayList<>();
        colorForegroundArray = context.getResources().getIntArray(R.array.colorForeground);
        colorButtonArray = context.getResources().getIntArray(R.array.colorBottom);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (dataList.size() > 0) {
            final Task task = dataList.get(position % dataList.size());
            View view;
            MyViewHolder holder;
            if (convertViewList.size() > 0) {
                view = convertViewList.remove(0);
            } else {
                view = View.inflate(context, R.layout.item_getmi_task, null);
                holder = new MyViewHolder(view);
                view.setTag(holder);
            }
            holder = (MyViewHolder) view.getTag();
            Log.i("data", task.getTitle() + "");
            holder.tv.setText(task.getTitle());
            holder.cv.setCardBackgroundColor(colorForegroundArray[position % colorForegroundArray.length]);
            holder.tvTime.setText(DateUtils.getAvalibleTime(task.getStart_time(), task.getEnd_time()));
            holder.tvCityTime.setText("【 南京 】 " + DateUtils.getTimeStringChinese(task.getStart_time()));
//        holder.tvName.setText(task.getBusinessNname());
            picasso.load("http://" + task.getLogo()).placeholder(R.drawable.bg_erweima).into(holder.iv);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
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
                            intent.putExtra("type", 2);         //外部链接
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
        convertViewList.add((View) object);
    }

    @Override
    public int getCount() {
        if (dataList.size() == 1 || dataList.size() == 0) {
            return 1;
        }
        return 200 ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
