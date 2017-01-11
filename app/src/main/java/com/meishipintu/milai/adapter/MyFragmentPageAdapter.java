package com.meishipintu.milai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MyFragmentPageAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public MyFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     *  根据此方法确定是否在dataSetChange方法中重绘视图，如果返回{@link #POSITION_UNCHANGED}则默认无变化，不绘制
     *  如返回{@link #POSITION_NONE}则认为变化，重新绘制
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
