package com.seupacote.wallpaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    private final List<Integer> wallpapers;
    private final OnWallpaperClick listener;

    interface OnWallpaperClick {
        void onClick(int resId);
    }

    public WallpaperAdapter(List<Integer> wallpapers, OnWallpaperClick listener) {
        this.wallpapers = wallpapers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int resId = wallpapers.get(position);
        Glide.with(holder.imageView.getContext())
                .load(resId)
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> listener.onClick(resId));
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageWallpaper);
        }
    }
}
