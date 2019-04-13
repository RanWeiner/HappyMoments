package com.example.ran.happymoments.screens.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.adapters.FullScreenImageAdapter;

import java.util.ArrayList;

public class FullScreenViewImpl implements FullScreenView {
    private View mRootView;
    private FullScreenImageAdapter mAdapter;
    private ViewPager mViewPager;

    public FullScreenViewImpl(LayoutInflater inflater, ViewGroup container,ArrayList<String> resultsPhotosPath){

        mRootView = inflater.inflate(R.layout.activity_full_screen_view, container);
        mAdapter = new FullScreenImageAdapter(getContext(), resultsPhotosPath) ;
        mViewPager = mRootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }

    @Override
    public Context getContext() {
        return mRootView.getContext();
    }

    @Override
    public void setPosition(int position) {
        mViewPager.setCurrentItem(position);
    }
}
