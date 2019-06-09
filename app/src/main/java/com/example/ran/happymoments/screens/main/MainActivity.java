package com.example.ran.happymoments.screens.main;


import android.content.Intent;

import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;


import com.example.ran.happymoments.screens.detection.DetectionActivity;
import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.common.Utils;
import com.example.ran.happymoments.screens.main.views.MainActivityView;
import com.example.ran.happymoments.screens.main.views.MainActivityViewImpl;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import static android.support.constraint.Constraints.TAG;


public class MainActivity extends AppCompatActivity implements MainActivityView.Listener {

    MainActivityViewImpl mView;
    List<String> chosenImagesPath = new ArrayList<>();

    private String pathToFile;
    public int numPicturesTaken = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new MainActivityViewImpl(this , null);
        mView.registerLister(this);
        setContentView(mView.getRootView());

        //check if in on Start
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            checkAndRequestCameraPermission();
            checkAndRequestStoragePermission();
        }
    }


    private void goToCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = Utils.createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this , "com.example.ran.happymoments.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent , AppConstants.CAMERA_REQUEST_CODE);
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK ) {
            if (numPicturesTaken > 0) {
                goToDetectionActivity(chosenImagesPath);
            } else {
                return;
            }
        }

        else if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                    List<Image> chosenImages = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
                    chosenImagesPath = getImagesPath(chosenImages);
                    goToDetectionActivity(chosenImagesPath);

                } else if (requestCode == AppConstants.CAMERA_REQUEST_CODE ) {
                        numPicturesTaken++;
                        chosenImagesPath.add(pathToFile);
                        goToCamera();
                }
    }

    private List<String> getImagesPath(List<Image> chosenImages) {

        return chosenImages.stream().map(p->p.path).collect(Collectors.toList());
    }


    private void goToDetectionActivity(List<String> photos) {
        Intent intent = new Intent(MainActivity.this, DetectionActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<String> selectedPhotos = new ArrayList<String>(photos);
        bundle.putStringArrayList(AppConstants.IMPORTED_IMAGES , selectedPhotos);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    //TODO wrap with try-catch for any case
    private void chooseImagesFromDeviceGallery() {
        Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, AppConstants.NUM_IMAGE_CHOSEN_LIMIT);
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }


    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG , "OpenCv Loaded Successfully!");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.i(TAG , "OpenCv lib Not found, using manager!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0 , this , baseLoaderCallback);
        }else {
            Log.i(TAG , "OpenCv Loaded Successfully!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    //permissions

    public boolean checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                this.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.READ_STORAGE_PERMISSION);
        }
        return false;
    }

    public boolean checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                this.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, AppConstants.CAMERA_PERMISSION);
        }
        return false;
    }

    @Override
    public void onImportClicked() {
        chooseImagesFromDeviceGallery();
    }

    @Override
    public void onCameraClicked() {
        goToCamera();
    }
}



