package com.example.ran.happymoments.screens.result;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ran.happymoments.R;
import com.example.ran.happymoments.adapters.SlidingImagesAdapter;
import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.common.Utils;
import com.example.ran.happymoments.screens.main.MainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ResultsActivity extends AppCompatActivity {

    private ArrayList<String> mResultsPhotosPath;
    private ViewPager mPager;
    private FloatingActionButton mShareBtn;
    private FloatingActionButton mSaveBtn;
    private Button mBackToMenuBtn;
    private int currentPage=0;
    private Set<String> mSavedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle bundle = getIntent().getExtras();
        mResultsPhotosPath = bundle.getStringArrayList(AppConstants.OUTPUT_PHOTOS);
        mSavedPhotos = new HashSet<>();
        init();
    }

    private void init() {
        if (mResultsPhotosPath.size() == 0) {
            //show message to user - No faces found
            setContentView(R.layout.activity_no_results);
            mBackToMenuBtn = (Button) findViewById(R.id.back_to_menu);
            mBackToMenuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMainActivity();
                }
            });
        } else {
            initializeViews();
        }
    }

    public void initializeViews(){
        mPager = (ViewPager) findViewById(R.id.pager);
        mSaveBtn = (FloatingActionButton)findViewById(R.id.save_btn);
        mShareBtn = (FloatingActionButton)findViewById(R.id.share_btn);
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);

        //Set circle indicator radius
        float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        mPager.setAdapter(new SlidingImagesAdapter(ResultsActivity.this,mResultsPhotosPath));
        indicator.setViewPager(mPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        setListeners();

    }

    private void setListeners() {
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String path = mResultsPhotosPath.get(currentPage);
                final Uri uriToImage = Uri.parse(path);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = mResultsPhotosPath.get(currentPage);
                if (!mSavedPhotos.contains(path)) {
                    copyPhotoToAlbum(path);
                    mSavedPhotos.add(path);
                    Toast.makeText(ResultsActivity.this , "saved!" , Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ResultsActivity.this , "photo already saved..." , Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void copyPhotoToAlbum(String path){
        Utils.copyFile(new File(path) ,Utils.getOutputMediaFile());
    }

    private void copyPhotosToAlbum() {
        for (String path : mResultsPhotosPath) {
            copyPhotoToAlbum(path);
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(ResultsActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void exit() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        goToMainActivity();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
        builder.setTitle("Finish").setMessage("Go to Main Menu? ")
                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

}


