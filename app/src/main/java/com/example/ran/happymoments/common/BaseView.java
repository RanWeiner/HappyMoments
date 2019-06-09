package com.example.ran.happymoments.common;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface BaseView {

     View getRootView();
     Bundle getViewState();
     Context getContext();
}
