package com.example.ran.happymoments.screens.detection.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.screens.detection.DetectionActivity;

import java.util.List;

public class DetectionViewImpl implements DetectionView , RecycleViewImageAdapter.Listener {

    private View mRootView;
    private Button mDetectBtn , mAddBtn;
    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerPhotos;
    private RecycleViewImageAdapter mAdapter;

    private Listener mListener;

    public DetectionViewImpl(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.activity_detection, container);
        mDetectBtn = mRootView.findViewById(R.id.detect_btn_id);
        mAddBtn = mRootView.findViewById(R.id.add_more_btn);
        mProgressBar = mRootView.findViewById(R.id.progress_bar);

        mRecyclerPhotos = mRootView.findViewById(R.id.gallery);
        mRecyclerPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerPhotos.setLayoutManager(layoutManager);

        mAdapter = new RecycleViewImageAdapter(inflater, this);
        mRecyclerPhotos.setAdapter(mAdapter);

        setViewsListeners();
    }

    private void setViewsListeners() {

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddPhotosClicked();
            }
        });

        mDetectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDetectClicked();
            }
        });
    }

    @Override
    public void registerLister(Listener listener) {
        mListener = listener;
    }

    @Override
    public void unregisterListener() {
        mListener = null;
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

    public void detectionStarted() {
        Toast.makeText(getContext() , "Start Detection...",Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.VISIBLE);
        mDetectBtn.setEnabled(false);
    }

    public void detectionFinished() {
        Toast.makeText(getContext(), "Finished!",Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.INVISIBLE);
        mDetectBtn.setEnabled(true);
    }

    public void bindPhotos(List<String> photosPath) {
        mAdapter.bindPhotos(photosPath);
    }

    public void updateViews() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(int position) {
        mListener.onItemClicked(position);
    }

    @Override
    public void onItemDelete(int position) {
        mListener.onItemDelete(position);
    }


}