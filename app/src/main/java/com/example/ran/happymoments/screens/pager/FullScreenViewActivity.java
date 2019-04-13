package com.example.ran.happymoments.screens.pager;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.adapters.FullScreenImageAdapter;

import java.util.ArrayList;


public class FullScreenViewActivity extends Activity implements FullScreenImageAdapter.OnClickListener {

    private ArrayList<String> mPhotosPath;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);
        viewPager = (ViewPager) findViewById(R.id.pager);

        Bundle bundle = getIntent().getExtras();
        mPhotosPath = bundle.getStringArrayList(AppConstants.PHOTOS_PATH);
        mPosition = bundle.getInt(AppConstants.POSITION);
        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, mPhotosPath , this) ;


        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(mPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onCloseBtnClick() {
        finish();
    }


}
