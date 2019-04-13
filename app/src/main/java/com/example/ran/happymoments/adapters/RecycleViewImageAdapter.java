package com.example.ran.happymoments.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ran.happymoments.screens.detection.views.PhotoItemView;
import com.example.ran.happymoments.screens.detection.views.PhotoItemViewImpl;

import java.util.List;

public class RecycleViewImageAdapter extends RecyclerView.Adapter<RecycleViewImageAdapter.ViewHolder> implements PhotoItemView.Listener {

    private final LayoutInflater mInflater;
    private final Listener mListener;
    private List<String> mPhotos;

    public RecycleViewImageAdapter(LayoutInflater inflater, Listener listener) {
        mInflater = inflater;
        mListener = listener;
    }


    public void bindPhotos(List<String> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }


    @Override
    public void onItemDelete(int position) {
        mListener.onItemDelete(position);

    }

    @Override
    public void onItemClick(int position) {
        mListener.onItemClick(position);
    }

    public interface Listener {
        void onItemClick(int position);
        void onItemDelete(int position);
    }


    @NonNull
    @Override
    public RecycleViewImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoItemView view = new PhotoItemViewImpl(mInflater, parent);
        view.registerListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewImageAdapter.ViewHolder holder, final int position) {
        holder.mView.bindPhoto(mPhotos.get(position) , position);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        private final PhotoItemView mView;

        public ViewHolder(PhotoItemView view) {
            super(view.getRootView());
            mView = view;
        }
    }

}
