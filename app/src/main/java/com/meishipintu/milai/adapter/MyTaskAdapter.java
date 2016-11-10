package com.meishipintu.milai.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/8/3.
 */

public class MyTaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private List<Task> list;
    private Context context;
    private TaskOnItemClickListener listener;
    private Picasso picasso;
    private NetApi netApi;

    public MyTaskAdapter(List<Task> list, Context context, TaskOnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        picasso = Picasso.with(context);
        netApi = NetApi.getInstance();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_getmi_task_new, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {
        final Task task = list.get(position);
        Log.i("tasktoString", task.toString());
        holder.tvTitle.setText(task.getTitle());
        holder.tvTime.setText(DateUtils.getTimePeriodWithSlash(task.getStart_time()
                , task.getEnd_time()));
        holder.tvLikes.setText(task.getLisks());
        holder.tvForward.setText(task.getForward());
        holder.tvContent.setText(task.getSub_name());
        if ("1".equals(task.getIslikes())) {
            holder.iblikes.setImageResource(R.drawable.isdolikes);
//            holder.btDianzan.setClickable(false);
        } else {
            holder.iblikes.setImageResource(R.drawable.dolikes);
//            holder.btDianzan.setClickable(true);
        }
        picasso.load("http://" + task.getLogo()).placeholder(R.drawable.bg_erweima).into(holder.ivTask);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, task);
            }
        });

        holder.iblikes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ("1".equals(task.getIslikes())) {
                    ToastUtils.show(context, "已点赞~");
                } else {
                    holder.iblikes.setImageResource(R.drawable.isdolikes);
                    holder.iblikes.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale));
                    doLikes(holder, task);
                    Log.i("task01", task + "");
//                holder.iblikes.setClickable(false);
                }
            }
        });

        final UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                String type = "1";  //微信
                Log.i("onResult", "分享成功");
                if (share_media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {
                    type = "2";//朋友圈
                }
                doForward(holder, task, type);
                ToastUtils.show(context,"分享成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                ToastUtils.show(context,"分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtils.show(context,"取消分享");
            }
        };

        holder.ibForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ("3".equals(task.getType())) {
                    ToastUtils.show(context, task.getType_detail());
                } else {
                    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            { SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,};
                    new ShareAction((Activity) context).setDisplayList(displaylist)
                            .withTitle("下载关注米来")
                            .withText("支付级数字营销传播者")
                            .withTargetUrl(task.getType_detail())
                            .withMedia(new UMImage(context
                                    , BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_small)))
                            .setListenerList(umShareListener)
                            .open();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface TaskOnItemClickListener {
        void onItemClick(View view, Task task);
    }

    //点赞调用
    private void doLikes(final TaskViewHolder holder, final Task task) {
        netApi.likes(Cookies.getUserId(), task.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.show(context,e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("task1", Cookies.getUserId());
                        task.setIslikes("1");
                        holder.tvLikes.setText(s);
                    }
                });
    }

    //转发调用
    private void doForward(final TaskViewHolder holder, final Task task, String type) {
        netApi.doForward(Cookies.getUserId(), task.getId(), type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.show(context, e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(" onNext", s + "回调成功");
                        holder.tvForward.setText(s);
                    }
                });

    }
}

class TaskViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_task)
    ImageView ivTask;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.ib_likes)
    ImageView iblikes;
    @BindView(R.id.ib_forward)
    ImageView ibForward;
    @BindView(R.id.tv_forward)
    TextView tvForward;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.tv_time)
    TextView tvTime;

    public TaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}

