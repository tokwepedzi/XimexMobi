package com.tutorials.ximexmobi.adapters;

import android.content.Context;
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
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdapterClassViewAdInBottomSheet extends RecyclerView.Adapter<AdapterClassViewAdInBottomSheet.ViewHolder> {
    public AdPostModel adPostModel = new AdPostModel();
    private List<URL> urlList = new ArrayList<>();
    public Context context;
    //public ActionClickListener actionClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //private final TextView mHeading,mPrice,mDateposted,mDistance,mCondition,mContactnumber,mContactemail;
        // private final ImageButton mCallAdvertiser,mWhatsappAdevertiser;
        private final ImageView mItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = (ImageView) itemView.findViewById(R.id.item_image);
            // mHeading = (TextView) itemView.findViewById(R.id.heading_text_view);
            //  mPrice = (TextView) itemView.findViewById(R.id.price);
            // mDateposted = (TextView) itemView.findViewById(R.id.posted);
            // mDistance = (TextView) itemView.findViewById(R.id.distance);
            // mCondition = (TextView) itemView.findViewById(R.id.condition);
            // mContactnumber = (TextView) itemView.findViewById(R.id.condition);
            // mContactemail = (TextView) itemView.findViewById(R.id.email);
            // mContactnumber = (TextView) itemView.findViewById(R.id.condition);
            // mCallAdvertiser = (ImageButton) itemView.findViewById(R.id.call_advertiser);
            // mWhatsappAdevertiser = (ImageButton) itemView.findViewById(R.id.whatsapp_advertiser);

        }

        public ImageView getListedItemPic() {
            return mItemImage;
        }

       /* public AdPostModel getAdPostModel() {
            return adPostModel;
        }*/
    }

    public AdapterClassViewAdInBottomSheet(AdPostModel adPostModel, List<URL> urlList, Context context) {

        this.adPostModel = adPostModel;
        this.urlList = urlList;
        this.context = context;
    }

   /* public interface ActionClickListener{
        void lauchSelectedAction(AdPostModel adPostModel);
    }*/

    @NonNull
    @Override
    public AdapterClassViewAdInBottomSheet.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_advert_images, parent, true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassViewAdInBottomSheet.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        //AdPostModel adPostModel = holder.getAdPostModel();

       // Glide.with(holder.getListedItemPic()).load(urlList.get(position))
    }


    @Override
    public int getItemCount() {
        return 0;
    }


}
