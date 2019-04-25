package com.example.ran.happymoments.screens.result;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    private Dialog mExitDialog;
    private Button mPositiveBtn, mNegativeBtn;


    public ResultsViewImpl(LayoutInflater inflater, ViewGroup container, ArrayList<String> mResultsPhotosPath) {
        mRootView = inflater.inflate(R.layout.activity_results, container);
        mPager = mRootView.findViewById(R.id.pager);
        mSaveBtn = mRootView.findViewById(R.id.save_btn);
        mShareBtn = mRootView.findViewById(R.id.share_btn);
        mIndicator = mRootView.findViewById(R.id.indicator);
        mAdapter = new SlidingImagesAdapter(getContext(),mResultsPhotosPath);
        mPager.setAdapter(mAdapter);

        setupExitDialog();

        setViews();
    }

    public void setupExitDialog() {
        mExitDialog  = new Dialog(getContext());
        mExitDialog.setContentView(R.layout.layout_exit); //???
        mPositiveBtn = mExitDialog.findViewById(R.id.positive_btn);
        mNegativeBtn = mExitDialog.findViewById(R.id.negative_btn);
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


        mShareBtn.setOnClickListener(view -> mListener.onShareClicked());

        mSaveBtn.setOnClickListener(view -> mListener.onSaveClicked());

        mPositiveBtn.setOnClickListener(view -> mListener.onPositiveClicked());

        mNegativeBtn.setOnClickListener(view -> mListener.onNegativeClicked());

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
//        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
//            switch (which){
//                case DialogInterface.BUTTON_POSITIVE:
//                    mListener.onExitClicked();
//                    break;
//
//                case DialogInterface.BUTTON_NEGATIVE:
//                    dialog.dismiss();
//                    break;
//            }
//        };
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Are you sure?").setMessage("Any unsaved photo will be deleted")
//                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        mExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mExitDialog.show();
    }

    @Override
    public void hideExitDialog() {
        mExitDialog.dismiss();
    }

    @Override
    public void savedClicked(boolean success) {
        if (success) {
            Toast.makeText(getContext(), "Saved!" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Photo already saved..." , Toast.LENGTH_SHORT).show();
        }
    }

}
