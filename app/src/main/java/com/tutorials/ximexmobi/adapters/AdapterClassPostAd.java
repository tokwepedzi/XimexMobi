package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.models.AdPostModel;
import com.tutorials.ximexmobi.models.XimexUser;

import java.util.ArrayList;
import java.util.List;

/*public class AdapterClassPostAd extends RecyclerView.Adapter<AdapterClassPostAd.ViewHolder>  {
    public List<AdPostModel> adPostModelList = new ArrayList<>();
    public Context context;
   // public AdImageClicklistener imageClicklistener;

   *//* @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface AdImageClicklistener{
        void selectImage(AdPostModel adPostModel);
    }
    public AdapterClassPostAd(List<AdPostModel> adPostModelList,Context context,AdImageClicklistener imageClicklistener){
        this.adPostModelList = adPostModelList;
        this.context= context;
        this.imageClicklistener= imageClicklistener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mAdImage;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            mAdImage = (ImageView) itemView.findViewById(R.id.ad_imagevw);
        }
    }*//*

}*/
