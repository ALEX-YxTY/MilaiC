package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.utils.Immersive;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

public class GuideActivity extends BaseActivity {

    private List<View> viewlist;

    @BindView(R.id.vp)
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initViewPager();

    }

    private void initViewPager() {
        viewlist = new ArrayList<>();
        View view1 = View.inflate(this, R.layout.guide_1_layout, null);
        View view2 = View.inflate(this, R.layout.guide_2_layout, null);
        View view3 = View.inflate(this, R.layout.guide_3_layout, null);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 测试用
//                if (Cookies.getCityId() == 0) {
//                    startActivity(new Intent(GuideActivity.this, CitySelectActivity.class));
//                }else{
                    startActivity(new Intent(GuideActivity.this, MainActivity.class));
//                }
                GuideActivity.this.finish();
                Cookies.setShowGuide(false);
            }
        });
        viewlist.add(view1);
        viewlist.add(view2);
        viewlist.add(view3);
        vp.setAdapter(new MyPageAdapter(viewlist));
        vp.setCurrentItem(0);
    }

    private class MyPageAdapter extends PagerAdapter {
        private List<View> list;

        public MyPageAdapter(List<View> viewlist) {
            this.list = viewlist;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
