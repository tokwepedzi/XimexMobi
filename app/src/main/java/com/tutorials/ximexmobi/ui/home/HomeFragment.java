package com.tutorials.ximexmobi.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.adapters.AdapterClassViewAllListedAds;
import com.tutorials.ximexmobi.databinding.FragmentHomeBinding;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterClassViewAllListedAds.ListedAdClickListener {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewAdsNearYou, recyclerViewListedItems;
    private AdapterClassViewAllListedAds viewAllListedAdsAdapter;
    private AdapterClassViewAllListedAds viewAllListedItemsAdapter;
    private List<AdPostModel> adPostModelNearYouList,adPostModelListedItems;
    private androidx.appcompat.widget.SearchView mSearchbox;
    private FirebaseFirestore ListedItemsRef;
    //private AdPostModel adPostModel;

    @Override
    public void onStart() {
        super.onStart();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.itemsNearYouHeadingTxt;
        recyclerViewAdsNearYou = binding.itemsNearYouRecyclerview;
        recyclerViewListedItems = binding.listedItemsRecyclerview;
        ListedItemsRef = FirebaseFirestore.getInstance();



        //adPostModel = new AdPostModel();
        adPostModelNearYouList= new ArrayList<>();
        adPostModelListedItems = new ArrayList<>();


        CollectionReference ListedAdsRef = ListedItemsRef.collection("Adverts");

        ListedAdsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                recyclerViewAdsNearYou.setLayoutManager(linearLayoutManager);
                if(task.isSuccessful()){
                    List AdIdsLis = new ArrayList();
                    for(DocumentSnapshot documentSnapshot: task.getResult()){
                        if(documentSnapshot.exists()){
                            AdPostModel adPostModel1 = documentSnapshot.toObject(AdPostModel.class);
                            adPostModelNearYouList.add(adPostModel1);
                            adPostModelListedItems.add(adPostModel1);
                            setAdapter();
                        }
                    }
                }
            }
        });
        return root;
    }

    private void setAdapter() {
        viewAllListedAdsAdapter = new AdapterClassViewAllListedAds(adPostModelNearYouList, getContext(), this::viewSelectedAd);
        viewAllListedItemsAdapter = new AdapterClassViewAllListedAds(adPostModelListedItems,getContext(),this::viewSelectedAd);
        recyclerViewAdsNearYou.setAdapter(viewAllListedAdsAdapter);
        recyclerViewListedItems.setAdapter(viewAllListedItemsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void viewSelectedAd(AdPostModel adPostModel) {

    }
}