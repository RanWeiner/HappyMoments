package com.example.ran.happymoments.screens.main.views;

import com.example.ran.happymoments.BaseView;

public interface MainActivityView extends BaseView {

    interface Listener {
        void onImportClicked();
        void onCameraClicked();
    }

    void registerLister(Listener listener);

    void unregisterListener();


}
