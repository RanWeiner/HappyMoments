package com.example.ran.happymoments.screens.detection.views;


import com.example.ran.happymoments.BaseView;

public interface DetectionView extends BaseView {

    interface Listener {
        void onAddPhotosClicked();
        void onDetectClicked();
        void onItemClicked(int position);
        void onItemDelete(int position);
    }

    void registerLister(Listener listener);

    void unregisterListener();


}