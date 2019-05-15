package com.example.ran.happymoments.screens.detection.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.ran.happymoments.R;
import com.example.ran.happymoments.adapters.RecycleViewImageAdapter;
import com.example.ran.happymoments.common.AppConstants;

import java.util.List;

public class DetectionViewImpl implements DetectionView , RecycleViewImageAdapter.Listener {

    private View mRootView;
    private ImageButton mDetectBtn,mAddBtn,mClearAllBtn ;
    private Button mConfirmDialogBtn, mConnectBtn, mCancelBtn, mCloseInfoDialog;
    private Dialog mLoaderDialog,mNotFoundDialog, mNetworkDialog, mInfoDialog;
    private RecyclerView mRecyclerPhotos;
    private RecycleViewImageAdapter mAdapter;

    private Listener mListener;

    public DetectionViewImpl(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.activity_detection, container);
        mDetectBtn = mRootView.findViewById(R.id.detect_btn_id);
        mAddBtn = mRootView.findViewById(R.id.add_more_btn);
        mClearAllBtn = mRootView.findViewById(R.id.clear_all_btn);
        mLoaderDialog  = new Dialog(getContext());

        mRecyclerPhotos = mRootView.findViewById(R.id.gallery);
        mRecyclerPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerPhotos.setLayoutManager(layoutManager);


        mAdapter = new RecycleViewImageAdapter(inflater, this);
        mRecyclerPhotos.setAdapter(mAdapter);

        setupNotFoundDialog();
        setupNetworkConnectionDialog();
        setupInfoDialog();

        setViewsListeners();
    }

    private void setupInfoDialog() {
        mInfoDialog = new Dialog(getContext());
        mInfoDialog.setContentView(R.layout.layout_guide);
        mCloseInfoDialog = mInfoDialog.findViewById(R.id.confirm_btn);
    }

    public void setupNotFoundDialog() {
        mNotFoundDialog  = new Dialog(getContext());
        mNotFoundDialog.setContentView(R.layout.layout_no_results);
        mConfirmDialogBtn = mNotFoundDialog.findViewById(R.id.confirm_btn);
    }

    public void setupNetworkConnectionDialog() {
        mNetworkDialog = new Dialog(getContext());
        mNetworkDialog.setContentView(R.layout.layout_network_connection);
        mConnectBtn = mNetworkDialog.findViewById(R.id.connect_btn);
        mCancelBtn = mNetworkDialog.findViewById(R.id.cancel_btn);

    }

    private void setViewsListeners() {

        mAddBtn.setOnClickListener(v -> mListener.onAddPhotosClicked());

        mClearAllBtn.setOnClickListener(v -> mListener.onClearAllClicked());

        mDetectBtn.setOnClickListener(v -> mListener.onDetectClicked());

        mConfirmDialogBtn.setOnClickListener(v -> mListener.onConfirmDialogClicked());

        mConnectBtn.setOnClickListener(v -> mListener.onNetworkAccessClicked());

        mCancelBtn.setOnClickListener(v -> mListener.onCancelClicked());

        mCloseInfoDialog.setOnClickListener(v -> mListener.onCloseInfoDialogClicked());
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
        showDialog();
    }

    public void detectionFinished() {
        hideDialog();
    }

    public void bindPhotos(List<String> photosPath) {
        mAdapter.bindPhotos(photosPath);
    }

    public void updateViews() {
        mAdapter.notifyDataSetChanged();
    }

    public void hideDialog(){
        mLoaderDialog.dismiss();
    }

    public void hideNotFoundDialog() { mNotFoundDialog.dismiss();}

    public void hideNetworkDialog() { mNetworkDialog.dismiss();}


    @Override
    public void onItemClick(int position) {
        mListener.onItemClicked(position);
    }

    @Override
    public void onItemDelete(int position) {
        mListener.onItemDelete(position);
    }

    public void showNotFoundDialog() {
        mNotFoundDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mNotFoundDialog.show();
    }


    public void showDialog() {
        mLoaderDialog.setCancelable(false);
        mLoaderDialog.setContentView(R.layout.loading_layout);

        ImageView gifImageView = mLoaderDialog.findViewById(R.id.custom_loading_imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        Glide.with(getContext())
                .load(R.drawable.camera_gif)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(gifImageView);

        mLoaderDialog.show();
    }

    public void showNetworkDialog() {
        mNetworkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mNetworkDialog.show();
    }


    public void showReachedLimitMessage() {
        Toast.makeText(getContext(), "Sorry, The limit is " + AppConstants.NUM_IMAGE_CHOSEN_LIMIT + " images", Toast.LENGTH_SHORT).show();
    }

    public void hideInfoDialog() {
        mInfoDialog.dismiss();
    }

    public void showInfoDialog() {
        mInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mInfoDialog.show();
    }
}
