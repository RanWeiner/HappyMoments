package com.example.ran.happymoments.screens.detection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.common.AppConstants;


import com.example.ran.happymoments.common.Utils;
import com.example.ran.happymoments.screens.pager.FullScreenViewActivity;
import com.example.ran.happymoments.screens.result.ResultsActivity;
import com.example.ran.happymoments.screens.main.MainActivity;
import com.example.ran.happymoments.screens.detection.views.DetectionViewImpl;

import com.example.ran.happymoments.service.SeriesGenerator;
import com.example.ran.happymoments.service.SeriesGeneratorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import static android.provider.Contacts.SettingsColumns.KEY;

public class DetectionActivity extends AppCompatActivity implements DetectionViewImpl.Listener{

    DetectionViewImpl mView;
    private List<String> mInputPhotosPath , mOutputPhotosPath;
    private SeriesGenerator mSeriesGenerator;

    private SharedPreferences mPrefs;
    private boolean firstStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = getIntent().getExtras();
        mInputPhotosPath = bundle.getStringArrayList(AppConstants.IMPORTED_IMAGES);
        mView = new DetectionViewImpl(LayoutInflater.from(this), null);
        mView.registerLister(this);
        mSeriesGenerator = new SeriesGeneratorImpl(getApplicationContext());
        mOutputPhotosPath = new ArrayList<>();
        setContentView(mView.getRootView());
        checkAndShowInfoDialog();


    }

    private void checkAndShowInfoDialog() {
        mPrefs  = this.getSharedPreferences(AppConstants.SHARED_PREFS_NAME, MODE_PRIVATE);
        firstStart = mPrefs.getBoolean(AppConstants.KEY,true);
        SharedPreferences.Editor editor = mPrefs.edit();

        if(firstStart) {
            mView.showInfoDialog();
            editor.putBoolean(AppConstants.KEY, false);
            editor.apply();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mView.bindPhotos(mInputPhotosPath);
    }


    private void chooseImagesFromDeviceGallery(int imagesChosenLimit) {
        Intent intent = new Intent(DetectionActivity.this, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, imagesChosenLimit);
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK ) {
            return;
        }
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
            ArrayList<Image> chosenImages = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            Set set = chosenImages.stream().map(i->i.path).collect(Collectors.toSet());
            set.addAll(mInputPhotosPath);
            mInputPhotosPath = new ArrayList<>(set);
            mView.updateViews();
        }
    }


    private void showNetworkError() {

        mView.showNetworkDialog();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void goToFullScreenActivity(int position) {
        Intent intent = new Intent(DetectionActivity.this, FullScreenViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(AppConstants.PHOTOS_PATH , (ArrayList<String>) mInputPhotosPath);
        bundle.putInt(AppConstants.POSITION , position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startDetection() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mView.detectionStarted();
        long startTime = System.nanoTime();

        Thread t = new Thread(() -> {
                mOutputPhotosPath = mSeriesGenerator.detect(mInputPhotosPath);

            runOnUiThread(() -> {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mView.detectionFinished();

                long endTime   = System.nanoTime();
                long totalTime = endTime - startTime;
                Log.i("DETECTION TIME", "DETECTION TIME= " + totalTime);

                if(mOutputPhotosPath.size() > 0)
                    goToResultsActivity();
                else mView.showNotFoundDialog();
            });
        });
        t.start();


    }



    private void goToResultsActivity() {
        Intent intent = new Intent(DetectionActivity.this , ResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(AppConstants.OUTPUT_PHOTOS , (ArrayList<String>) mOutputPhotosPath);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetectionActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSeriesGenerator.releaseResources();
    }

    @Override
    public void onAddPhotosClicked() {
        //user reached the limit and can't add more photos
        if(mInputPhotosPath.size() == AppConstants.NUM_IMAGE_CHOSEN_LIMIT) {
            mView.showReachedLimitMessage();
        }
        else {
            chooseImagesFromDeviceGallery(AppConstants.NUM_IMAGE_CHOSEN_LIMIT - mInputPhotosPath.size());
        }
    }

    @Override
    public void onDetectClicked() {
        if (isNetworkAvailable()) {
            startDetection();
        } else {
            showNetworkError();
        }
    }

    @Override
    public void onItemClicked(int position) {
        goToFullScreenActivity(position);
    }

    @Override
    public void onItemDelete(int position) {
        mInputPhotosPath.remove(position);
        mView.updateViews();
    }

    @Override
    public void onNetworkAccessClicked() {
        mView.hideNetworkDialog();
        Intent intent=new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onConfirmDialogClicked() {
        mView.hideNotFoundDialog();
    }

    @Override
    public void onClearAllClicked() {
        mInputPhotosPath.clear();
        mView.updateViews();
    }

    @Override
    public void onCancelClicked() {
        mView.hideNetworkDialog();
    }

    @Override
    public void onCloseInfoDialogClicked() {
        mView.hideInfoDialog();
    }
}
