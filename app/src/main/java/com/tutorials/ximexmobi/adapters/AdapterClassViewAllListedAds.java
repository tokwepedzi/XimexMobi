package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterClassViewAllListedAds extends RecyclerView.Adapter<AdapterClassViewAllListedAds.ViewHolder> {
public List<AdPostModel> adPostModelList = new ArrayList<>();
public Context context;
public ListedAdClickListener listedAdClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPrice, mPostedDate;
        private final ImageView mListedItemNearYouImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPrice = (TextView) itemView.findViewById(R.id.listed_item_near_you_price);
            mPostedDate = (TextView) itemView.findViewById(R.id.listed_item_near_you_posted_date);
            mListedItemNearYouImage = (ImageView) itemView.findViewById(R.id.listed_item_near_you_image);

        }

        public ImageView getListedItemPic() {
            return mListedItemNearYouImage;
        }
    }

    public AdapterClassViewAllListedAds(List<AdPostModel> adPostModelList,Context context,ListedAdClickListener listedAdClickListener){
        this.adPostModelList = adPostModelList;
        this.context = context;
        this.listedAdClickListener=listedAdClickListener;
    }

        public interface ListedAdClickListener{
        void viewSelectedAd(AdPostModel adPostModel);
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      Context context = parent.getContext();
      View view = LayoutInflater.from(context).inflate(R.layout.layout_items_near_you,parent,false);
      return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassViewAllListedAds.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        AdPostModel adPostModel = adPostModelList.get(position);

        holder.mPrice.setText("Price: "+adPostModel.getPrice());
        holder.mPostedDate.setText("Posted on "+adPostModel.getPosteddate());



        Glide.with(holder.getListedItemPic()).load(adPostModel.getImg1())
                .placeholder(R.mipmap.imageplaceholder)
                .into(holder.mListedItemNearYouImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    //Recyclerview click listener suspended due to transaction too large runtime exception on android 8 device
                    /*debtoClickListener.selectedDebtor(debtor);*/
                }
                catch (Exception e){
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
