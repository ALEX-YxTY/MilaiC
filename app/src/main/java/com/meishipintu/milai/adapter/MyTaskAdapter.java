package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/3.
 */

public class MyTaskAdapter extends RecyclerView.Adapter<TaskViewHolder>{

    private List<Task> list;
    private Context context;
    private TaskOnItemClickListener listener;
    private Picasso picasso;

    public MyTaskAdapter(List<Task> list, Context context, TaskOnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        picasso = Picasso.with(context);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_getmi_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {
        final Task task = list.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvContent.setText(DateUtils.getTimePeriodWithSlash(task.getStart_time()
                ,task.getEnd_time())+" • "+task.getSub_name());
        picasso.load("http://" + task.getLogo()).placeholder(R.drawable.bg_erweima).into(holder.ivTask);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, task.getType_detail(), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface TaskOnItemClickListener{
        void onItemClick(View view, String detail, String shareTitle);
    }
}

class TaskViewHolder  extends RecyclerView.ViewHolder{

    @BindView(R.id.iv_task)
    ImageView ivTask;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;

    public TaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}

