package com.example.ran.happymoments.screens.detection.views;


import com.example.ran.happymoments.common.BaseView;

public interface DetectionView extends BaseView {

    interface Listener {
        void onAddPhotosClicked();
        void onDetectClicked();
        void onItemClicked(int position);
        void onItemDelete(int position);
        void onNetworkAccessClicked();
        void onConfirmDialogClicked();
        void onClearAllClicked();
        void onCancelClicked();
        void onCloseInfoDialogClicked();
    }

    void registerLister(Listener listener);

    void unregisterListener();


}
