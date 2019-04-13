package com.example.ran.happymoments.adapters;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.ran.happymoments.R;

public class FullScreenImageAdapter extends PagerAdapter {

    public interface OnClickListener {
        void onCloseBtnClick();
    }


    private Context mContext;
    private ArrayList<String> mImagesPath;
    private LayoutInflater inflater;

    private final OnClickListener listener;

    public FullScreenImageAdapter(Context context, ArrayList<String> imagePaths , OnClickListener listener) {
        this.mContext = context;
        this.mImagesPath = imagePaths;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return this.mImagesPath.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgDisplay;
        Button btnClose;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);


        Uri uri = Uri.fromFile(new File(mImagesPath.get(position)));
        Glide.clear(imgDisplay);
        Glide.with(mContext).load(uri).crossFade().centerCrop().into(imgDisplay);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCloseBtnClick();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
