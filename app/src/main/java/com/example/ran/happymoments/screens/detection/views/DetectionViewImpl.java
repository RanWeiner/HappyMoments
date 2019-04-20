package com.example.ran.happymoments.screens.detection.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.ran.happymoments.R;
import com.example.ran.happymoments.adapters.RecycleViewImageAdapter;

import java.util.List;

public class DetectionViewImpl implements DetectionView , RecycleViewImageAdapter.Listener {

    private View mRootView;
    private ImageButton mDetectBtn ;
    private Button mAddBtn;
    private Dialog mLoaderDialog;
    private RecyclerView mRecyclerPhotos;
    private RecycleViewImageAdapter mAdapter;

    private Listener mListener;

    public DetectionViewImpl(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.activity_detection, container);
        mDetectBtn = mRootView.findViewById(R.id.detect_btn_id);
        mAddBtn = mRootView.findViewById(R.id.add_more_btn);
        mLoaderDialog  = new Dialog(getContext());
        mRecyclerPhotos = mRootView.findViewById(R.id.gallery);
        mRecyclerPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerPhotos.setLayoutManager(layoutManager);

        mAdapter = new RecycleViewImageAdapter(inflater, this);
        mRecyclerPhotos.setAdapter(mAdapter);

        setViewsListeners();
    }

    private void setViewsListeners() {

        mAddBtn.setOnClickListener(v -> mListener.onAddPhotosClicked());

        mDetectBtn.setOnClickListener(v -> mListener.onDetectClicked());
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
//        Toast.makeText(getContext() , "Start Detection...",Toast.LENGTH_LONG).show();
    }

    public void detectionFinished() {
        hideDialog();
//        Toast.makeText(getContext(), "Finished!",Toast.LENGTH_LONG).show();
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


    @Override
    public void onItemClick(int position) {
        mListener.onItemClicked(position);
    }

    @Override
    public void onItemDelete(int position) {
        mListener.onItemDelete(position);
    }


    public void showDialog() {
        mLoaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoaderDialog.setCancelable(false);
        mLoaderDialog.setContentView(R.layout.loading_layout);

        ImageView gifImageView = mLoaderDialog.findViewById(R.id.custom_loading_imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        Glide.with(getContext())
                .load(R.drawable.load_gif)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(gifImageView);

        mLoaderDialog.show();
    }

    public void showNetworkDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mListener.onNetworkAccessClicked();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Internet").setMessage("This App requires Internet connections ")
                .setPositiveButton("Connect", dialogClickListener).setNegativeButton("Exit", dialogClickListener).show();
    }
}
