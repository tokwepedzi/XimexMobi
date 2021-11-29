package com.tutorials.ximexmobi.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.adapters.AdapterClassViewAllListedAds;
import com.tutorials.ximexmobi.databinding.FragmentHomeBinding;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterClassViewAllListedAds.ListedAdClickListener {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewAdsNearYou;
    private AdapterClassViewAllListedAds viewAllListedAdsAdapter;
    private List<AdPostModel> adPostModelNearYouList;
    private androidx.appcompat.widget.SearchView mSearchbox;
    private FirebaseFirestore ListedItemsRef;
    private AdPostModel adPostModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);*/

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.itemsNearYouHeadingTxt;
        recyclerViewAdsNearYou = binding.itemsNearYouRecyclerview;
        ListedItemsRef = FirebaseFirestore.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
     recyclerViewAdsNearYou.setLayoutManager(linearLayoutManager);
       /* homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        adPostModel = new AdPostModel();
        adPostModelNearYouList= new ArrayList<>();


        DocumentReference ListedAdsRef = ListedItemsRef
                .collection("Adverts")
                .document("gaXxvyHVOgNjA9gY2xd0");


        ListedAdsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    adPostModel= task.getResult().toObject(AdPostModel.class);
                    adPostModelNearYouList.add(adPostModel);
                    setAdapter();
                }
            }
        });

        return root;
    }

    private void setAdapter() {
        viewAllListedAdsAdapter = new AdapterClassViewAllListedAds(adPostModelNearYouList, getContext(), this::viewSelectedAd);
        recyclerViewAdsNearYou.setAdapter(viewAllListedAdsAdapter);
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