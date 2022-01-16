package com.tutorials.ximexmobi.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.tutorials.ximexmobi.models.PlacesApi;

import java.util.ArrayList;

public class PlacesAutoCompleteAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> results= new ArrayList<>();
    int resource;
    Context context;

    PlacesApi placesApi = new PlacesApi();

    public PlacesAutoCompleteAdapter(Context context, int resId) {
        super(context, resId);
        this.context = context;
        this.resource = resId;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public String getItem(int pos) {
        return results.get(pos);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    results = placesApi.autoComplete(charSequence.toString());

                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }
}
