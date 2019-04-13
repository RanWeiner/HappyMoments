package com.example.ran.happymoments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface BaseView {

    public View getRootView();
    public Bundle getViewState();
    public Context getContext();
}
