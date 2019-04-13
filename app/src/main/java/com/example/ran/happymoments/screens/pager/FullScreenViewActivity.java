package com.example.ran.happymoments.screens.pager;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.adapters.FullScreenImageAdapter;

import java.util.ArrayList;


public class FullScreenViewActivity extends Activity {
    private FullScreenView mView;
    private ArrayList<String> mPhotosPath;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        mPhotosPath = bundle.getStringArrayList(AppConstants.PHOTOS_PATH);
        mPosition = bundle.getInt(AppConstants.POSITION);
        mView = new FullScreenViewImpl(LayoutInflater.from(this), null , mPhotosPath);
        mView.setPosition(mPosition);

        setContentView(mView.getRootView());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
