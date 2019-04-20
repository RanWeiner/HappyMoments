package com.example.ran.happymoments.screens.main.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ran.happymoments.R;


public class MainActivityViewImpl implements MainActivityView {

    private View mRootView;
    private Button mImportBtn , mCameraBtn;
    private Listener mListener;

    public MainActivityViewImpl(Context context, ViewGroup container) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.activity_main, container);
        mImportBtn = mRootView.findViewById(R.id.import_btn);
        mCameraBtn = mRootView.findViewById(R.id.camera_btn);
        setViewsListeners();
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


    public void  setViewsListeners() {
      mImportBtn.setOnClickListener(v -> mListener.onImportClicked());
      mCameraBtn.setOnClickListener(v -> mListener.onCameraClicked());
}


    @Override
    public void registerLister(Listener listener) {
        mListener = listener;
    }

    @Override
    public void unregisterListener() {
        mListener = null;
    }

}
