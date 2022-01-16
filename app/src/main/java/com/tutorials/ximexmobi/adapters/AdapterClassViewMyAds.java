package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterClassViewMyAds extends RecyclerView.Adapter <AdapterClassViewMyAds.ViewHolder>{

    private List<AdPostModel> adPostModelList = new ArrayList<>();
   private Context context;
   public MyadClickListener myadClickListener;
   //public int totalresponses,totalviews;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mStatus,mViews,mResponses,mHeadline;
        private final ImageView mDeleteAd,mAdBanner;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStatus = (TextView) itemView.findViewById(R.id.listed_item_near_you_status);
            mViews = (TextView) itemView.findViewById(R.id.total_views);
            mResponses = (TextView) itemView.findViewById(R.id.total_responses);
            mAdBanner = (ImageView) itemView.findViewById(R.id.ad_banner);
            mDeleteAd = (ImageView) itemView.findViewById(R.id.delete_advert);
            mHeadline = (TextView) itemView.findViewById(R.id.headline);
           // mResponseRate = (TextView) itemView.findViewById(R.id.responserate);
        }

        public ImageView getmAdBanner() {
            return mAdBanner;
        }
    }


    public AdapterClassViewMyAds(List<AdPostModel> adPostModelList, Context context,MyadClickListener myadClickListener){
        this.adPostModelList = adPostModelList;
        this.context = context;
        this.myadClickListener = myadClickListener;
    }

    public interface MyadClickListener{
        void deleteAd(AdPostModel adPostModel);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_myads_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdapterClassViewMyAds.ViewHolder viewHolder = (AdapterClassViewMyAds.ViewHolder) holder;
        AdPostModel adPostModel = adPostModelList.get(position);
        try {

            Glide.with(holder.getmAdBanner()).load(adPostModel.getImg1())
                    .placeholder(R.mipmap.imageplaceholder)
                    .centerCrop()
                    .thumbnail(0.05f)
                    .into(holder.mAdBanner);

            holder.mStatus.setText("Status: " + adPostModel.getAvailability());
            holder.mViews.setText("Total views: " + adPostModel.getTotalviews());
            holder.mResponses.setText("Response: " + adPostModel.getResponses());
            holder.mHeadline.setText(adPostModel.getItemname());
           // int totalviews = Integer.parseInt(adPostModelList.get(holder.getLayoutPosition()).getTotalviews());
            // int totalresponses = Integer.parseInt(adPostModelList.get(holder.getLayoutPosition()).getResponses());
             //int rate =( totalresponses / totalviews)*100;
             //holder.mResponseRate.setText("Response rate: "+String.format(Locale.ROOT,"%.2f",rate));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        holder.mDeleteAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myadClickListener.deleteAd(adPostModel);
                    //Toast.makeText(context.getApplicationContext(), ""+adPostModel.getAdid(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return adPostModelList.size();
    }


}
