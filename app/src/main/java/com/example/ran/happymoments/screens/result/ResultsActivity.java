package com.example.ran.happymoments.screens.result;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.common.Utils;
import com.example.ran.happymoments.screens.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ResultsActivity extends AppCompatActivity implements ResultsViewImpl.Listener{

    private ArrayList<String> mResultsPhotosPath;
    private int mCurrentPage =0;
    private Set<String> mSavedPhotos;
    private ResultsView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mResultsPhotosPath = bundle.getStringArrayList(AppConstants.OUTPUT_PHOTOS);
        mSavedPhotos = new HashSet<>();

        mView = new ResultsViewImpl(LayoutInflater.from(this), null , mResultsPhotosPath);
        mView.registerLister(this);
        setContentView(mView.getRootView());

        }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void copyPhotoToAlbum(String path){
        Utils.copyFile(getApplicationContext(),new File(path) ,Utils.getOutputMediaFile());
    }

    private void copyPhotosToAlbum() {
        for (String path : mResultsPhotosPath) {
            copyPhotoToAlbum(path);
        }
    }

    @Override
    public void onBackPressed() {
        mView.showExitDialog();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ResultsActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onShareClicked() {
        final String path = mResultsPhotosPath.get(mCurrentPage);
        final Uri uriToImage = Uri.parse(path);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

    @Override
    public void onSaveClicked() {
        String path = mResultsPhotosPath.get(mCurrentPage);
        if (!mSavedPhotos.contains(path)) {
            copyPhotoToAlbum(path);
            mSavedPhotos.add(path);
            mView.savedClicked(true);
        }
        else {
            mView.savedClicked(false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.mCurrentPage = position;
    }


    @Override
    public void onExitClicked() {
        goToMainActivity();
    }
}


