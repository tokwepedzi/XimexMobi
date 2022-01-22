package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdapterClassViewAdInBottomSheet extends RecyclerView.Adapter<AdapterClassViewAdInBottomSheet.ViewHolder> {

    private List<String> urlList;
    public Context context;

    public AdapterClassViewAdInBottomSheet(Context context, ArrayList<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = (ImageView) itemView.findViewById(R.id.item_image);
           // mImageProgress = (ImageView) itemView.findViewById(R.id.loading_indicator);
        }
    }

    @NonNull
    @Override
    public AdapterClassViewAdInBottomSheet.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_advert_images, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Uri uri = Uri.parse(urlList.get(position));
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.image_progress_animation)
                .into(holder.mItemImage, new Callback() {
            @Override
            public void onSuccess() {
               // holder.mImageProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

}
