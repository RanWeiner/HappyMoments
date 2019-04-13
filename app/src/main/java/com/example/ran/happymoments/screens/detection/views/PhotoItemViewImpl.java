package com.example.ran.happymoments.screens.detection.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ran.happymoments.R;

import java.io.File;


public class PhotoItemViewImpl implements PhotoItemView {

    private View mRootView;
    private ImageButton mRemoveButton;
    private ImageView mImageView;
    private int mPosition;
    private Listener mListener;
    private String mPath;

    public PhotoItemViewImpl(LayoutInflater inflater, @Nullable ViewGroup parent) {
        mRootView = inflater.inflate(R.layout.custom_layout, parent, false);
        mRemoveButton = mRootView.findViewById(R.id.ib_remove);
        mImageView = mRootView.findViewById(R.id.iv);

        setViewsListeners();
    }

    private void setViewsListeners() {
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemDelete(mPosition);
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mPosition);
            }
        });
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
    public void registerListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void unregisterListener(Listener listener) {
        mListener = null;
    }

    @Override
    public void bindPhoto(String photoPath, int position) {
        mPath = photoPath;
        mPosition = position;
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Uri uri = Uri.fromFile(new File(mPath));
        Glide.clear(mImageView);
        Glide.with(mImageView.getContext()).load(uri).into(mImageView);
    }


}
