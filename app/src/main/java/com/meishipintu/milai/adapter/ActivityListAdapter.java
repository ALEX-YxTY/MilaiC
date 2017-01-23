package com.meishipintu.milai.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.TaskDetailActivity;
import com.meishipintu.milai.activitys.WelfareDetailNewActivity;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/19.
 */

public class ActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 1;
    private static final int TYPE_NORMAL = 2;

    private List<Task> dataList;
    private Context context;
    private Picasso picasso;
    private boolean isEmpty;
    private int[] colorForegroundArray, colorButtonArray;

    public ActivityListAdapter(List<Task> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        picasso = Picasso.with(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_activitylist, parent, false);
            return new ListItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list_empty, parent, false);
            return new MyEmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            final Task task = dataList.get(position);
            ListItemViewHolder holder = (ListItemViewHolder) holder1;
            picasso.load("http://" + task.getLogo()).placeholder(R.drawable.bg_erweima).into(holder.ivActivity);
            holder.tvName.setText(task.getTitle());
            holder.tvTime.setText(DateUtils.getAvalibleTime(task.getStart_time(), task.getEnd_time()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    String url;
                    switch (Integer.valueOf(task.getType())) {
                        case 3:         //空，弹出说明文字
                            ToastUtils.show(context, task.getType_detail());
                            break;
                        case 1:         //正常活动
                            intent = new Intent(context, WelfareDetailNewActivity.class);
                            if (colorButtonArray == null) {
                                colorForegroundArray = context.getResources().getIntArray(R.array.colorForeground);
                                colorButtonArray = context.getResources().getIntArray(R.array.colorBottom);
                            }
                            intent.putExtra("data", task);
                            intent.putExtra("foregroundColor", colorForegroundArray[position % colorForegroundArray.length]);
                            intent.putExtra("buttonColor", colorButtonArray[position % colorButtonArray.length]);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.anim_in_scale, R.anim.anim_out_alpha);
                            break;
                        case 2:         //外链
                            url = task.getType_detail();
                            intent = new Intent(context, TaskDetailActivity.class);
                            intent.putExtra("detail", url);
                            intent.putExtra("type", 2);
                            context.startActivity(intent);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        isEmpty = false;
        if (dataList.size() == 0) {
            isEmpty = true;
            return 1;
        }
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty && position == 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }


    class ListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_activity)
        ImageView ivActivity;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    class MyEmptyViewHolder extends RecyclerView.ViewHolder {

        public MyEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
