package com.example.ran.happymoments.screens.result;

import com.example.ran.happymoments.common.BaseView;

public interface ResultsView extends BaseView {

    void registerLister(Listener listener);
    void showExitDialog();
    void hideExitDialog();
    void savedClicked(boolean success);

    interface Listener {
        void onShareClicked();
        void onSaveClicked();
        void onPageSelected(int position);
        void onExitClicked();

        void onPositiveClicked();
        void onNegativeClicked();
    }
}
