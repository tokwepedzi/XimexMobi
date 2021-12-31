package com.tutorials.ximexmobi.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutorials.ximexmobi.adapters.AdapterClassAllListedItems;
import com.tutorials.ximexmobi.adapters.AdapterClassItemsNearYou;
import com.tutorials.ximexmobi.databinding.FragmentHomeBinding;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterClassItemsNearYou.ListedAdClickListener,AdapterClassAllListedItems.AllListedAdClickListener {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewAdsNearYou, recyclerViewListedItems;
    private AdapterClassItemsNearYou viewAllListedAdsAdapter;
    private AdapterClassAllListedItems secondAdapter;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
     recyclerViewAdsNearYou.setLayoutManager(linearLayoutManager);
     LinearLayoutManager secondLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
      recyclerViewListedItems.setLayoutManager(secondLayoutManager);
     adPostModelNearYouList= new ArrayList<>();
        adPostModelListedItems = new ArrayList<>();

        CollectionReference ListedAdsRef = ListedItemsRef.collection("Adverts");
        ListedAdsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
        viewAllListedAdsAdapter = new AdapterClassItemsNearYou(adPostModelNearYouList, getContext(), this::viewSelectedAd);
        recyclerViewAdsNearYou.setAdapter(viewAllListedAdsAdapter);
        secondAdapter = new AdapterClassAllListedItems(adPostModelListedItems,getContext(),this::viewAllSelectedAd);
        recyclerViewListedItems.setAdapter(secondAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void viewSelectedAd(AdPostModel adPostModel) {

    }

    @Override
    public void viewAllSelectedAd(AdPostModel adPostModel) {

    }
}