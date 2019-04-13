package com.example.ran.happymoments.screens.result;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.adapters.SlidingImagesAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class ResultsViewImpl implements ResultsView {

    private View mRootView;
    private ViewPager mPager;
    private FloatingActionButton mShareBtn;
    private FloatingActionButton mSaveBtn;
    private CirclePageIndicator mIndicator;
    private Listener mListener;
    private SlidingImagesAdapter mAdapter;


    public ResultsViewImpl(LayoutInflater inflater, ViewGroup container, ArrayList<String> mResultsPhotosPath) {
        mRootView = inflater.inflate(R.layout.activity_results, container);
        mPager = mRootView.findViewById(R.id.pager);
        mSaveBtn = mRootView.findViewById(R.id.save_btn);
        mShareBtn = mRootView.findViewById(R.id.share_btn);
        mIndicator = mRootView.findViewById(R.id.indicator);
        mAdapter = new SlidingImagesAdapter(getContext(),mResultsPhotosPath);
        mPager.setAdapter(mAdapter);

        setViews();
    }


    private void setViews() {

        float density = getContext().getResources().getDisplayMetrics().density;
        mIndicator.setRadius(5 * density);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mListener.onPageSelected(position);
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });


        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onShareClicked();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSaveClicked();
            }
        });

    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }

    @Override
    public Context getContext() {
        return mRootView.getContext();
    }

    @Override
    public void registerLister(Listener listener) {
        mListener = listener;
    }

    @Override
    public void showExitDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mListener.onExitClicked();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Finish").setMessage("Go to Main Menu? ")
                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

}
