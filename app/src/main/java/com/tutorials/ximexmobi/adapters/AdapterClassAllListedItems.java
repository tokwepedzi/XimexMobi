package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.util.Log;
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

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterClassAllListedItems extends RecyclerView.Adapter<AdapterClassAllListedItems.ViewHolder> {

    public static final String TAG = "AdptAllListdItems";
    public List<AdPostModel> adPostModelList = new ArrayList<>();
    public Context context;
    public AllListedAdClickListener listedAdClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mItemName, mPrice, mPostedDate;
        private final ImageView mListedItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemName = (TextView) itemView.findViewById(R.id.listed_item_name);
            mPrice = (TextView) itemView.findViewById(R.id.listed_item_price);
            mPostedDate = (TextView) itemView.findViewById(R.id.listed_item_posted_date);
            mListedItemImage = (ImageView) itemView.findViewById(R.id.listed_item_image);

        }

        public ImageView getListedItemPic() {
            return mListedItemImage;
        }
    }

    public AdapterClassAllListedItems(List<AdPostModel> adPostModelList, Context context, AllListedAdClickListener listedAdClickListener) {
        this.adPostModelList = adPostModelList;
        this.context = context;
        this.listedAdClickListener = listedAdClickListener;
    }

    public interface AllListedAdClickListener {
        void viewAllSelectedAd(AdPostModel adPostModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_listed_items, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassAllListedItems.ViewHolder holder, int position) {
        AdapterClassAllListedItems.ViewHolder viewHolder = (AdapterClassAllListedItems.ViewHolder) holder;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date postedDate = new Date();

        AdPostModel adPostModel = adPostModelList.get(position);;
        try {
            postedDate = simpleDateFormat.parse(adPostModel.getPosteddate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime prettyTime = new PrettyTime();

        try{
        holder.mItemName.setText(adPostModel.getItemname());
        holder.mPrice.setText("Price: " + adPostModel.getPrice());
        holder.mPostedDate.setText(prettyTime.format(postedDate));


        Glide.with(holder.getListedItemPic()).load(adPostModel.getImg1())
                .placeholder(R.mipmap.imageplaceholder)
                .centerCrop()
                .thumbnail(0.05f)
                .into(holder.mListedItemImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                   listedAdClickListener.viewAllSelectedAd(adPostModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });}
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "ERR "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBindViewHolder: AllItems "+e.getMessage());
        }

    }


    @Override
    public int getItemCount() {
        return adPostModelList.size();
    }


}
