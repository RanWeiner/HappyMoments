package com.example.ran.happymoments.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ran.happymoments.R;

import java.io.File;
import java.util.ArrayList;



    public class SlidingImagesAdapter extends PagerAdapter {

        private ArrayList<String> mImages;
        private LayoutInflater mInflater;
        private Context mContext;



        public SlidingImagesAdapter(Context mContext, ArrayList<String> mImages) {
            this.mContext = mContext;
            this.mImages = mImages;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = mInflater.inflate(R.layout.sliding_result_images_layout, view, false);

            ImageView imageView = (ImageView)imageLayout.findViewById(R.id.image);

            Uri uri = Uri.fromFile(new File(mImages.get(position)));
            Glide.clear(imageView);
            Glide.with(mContext).load(uri).crossFade().centerCrop().into(imageView);

            view.addView(imageLayout);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }


