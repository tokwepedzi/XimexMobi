package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

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

public class AdapterClassItemsNearYou extends RecyclerView.Adapter<AdapterClassItemsNearYou.ViewHolder> implements Filterable {
public List<AdPostModel> adPostModelList = new ArrayList<>();
public Context context;
public ListedAdClickListener listedAdClickListener;

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }

        };
        return null;
    }

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

    public AdapterClassItemsNearYou(List<AdPostModel> adPostModelList, Context context, ListedAdClickListener listedAdClickListener){
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
      //View view1 = LayoutInflater.from(context).inflate(R.layout.layout_listed_items,parent,false);
      return new ViewHolder(view);
     //return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassItemsNearYou.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date postedDate = new Date();

        AdPostModel adPostModel = adPostModelList.get(position);
        try {
            postedDate = simpleDateFormat.parse(adPostModel.getPosteddate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime prettyTime = new PrettyTime();


        holder.mPrice.setText("Price: "+adPostModel.getPrice());
        holder.mPostedDate.setText(prettyTime.format(postedDate));



        Glide.with(holder.getListedItemPic()).load(adPostModel.getImg1())
                .placeholder(R.mipmap.imageplaceholder)
                .centerCrop()
                .thumbnail(0.05f)
                .into(holder.mListedItemNearYouImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

listedAdClickListener.viewSelectedAd(adPostModel);                }
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
