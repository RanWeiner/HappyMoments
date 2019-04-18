package com.example.ran.happymoments.screens.result;

import com.example.ran.happymoments.common.BaseView;

public interface ResultsView extends BaseView {

    void registerLister(Listener listener);
    void showExitDialog();

    interface Listener {
        void onShareClicked();
        void onSaveClicked();
        void onPageSelected(int position);
        void onExitClicked();
    }
}