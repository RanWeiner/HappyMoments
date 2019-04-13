package com.example.ran.happymoments.screens.detection.views;

import android.view.View;

import com.example.ran.happymoments.common.BaseView;

public interface PhotoItemView extends BaseView {

    public interface Listener {
        void onItemDelete(int position);
        void onItemClick(int position);
    }

    View getRootView();
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);
    void bindPhoto(String photoPath , int position);

}
