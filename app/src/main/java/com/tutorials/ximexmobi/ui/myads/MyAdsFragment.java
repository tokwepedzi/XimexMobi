package com.tutorials.ximexmobi.ui.myads;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.adapters.AdapterClassViewMyAds;
import com.tutorials.ximexmobi.databinding.FragmentMyadsBinding;
import com.tutorials.ximexmobi.models.AdPostModel;
import com.tutorials.ximexmobi.models.XimexUser;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment implements AdapterClassViewMyAds.MyadClickListener {


    private FragmentMyadsBinding binding;
    private RecyclerView recyclerView;
    private AdapterClassViewMyAds viewMyAdsApater;
    private List<AdPostModel> adPostModelList;
    private FirebaseFirestore MyAdsRef;
    private XimexUser ximexUser;
    private FirebaseAuth firebaseAuth;
    private Dialog dialog;
    private Button mConfirmDelete, mCancelDelete;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMyadsBinding.inflate(inflater, container, false);
        recyclerView = binding.myadsRecyclerview;


        final FloatingActionButton floatingActionButton = binding.addAdvertBtn;
        View root = binding.getRoot();
        MyAdsRef = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ximexUser = new XimexUser();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        adPostModelList = new ArrayList<>();
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.delete_item_alert_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        mConfirmDelete = dialog.findViewById(R.id.confirm_delete);
        mCancelDelete = dialog.findViewById(R.id.cance_delete);
        CollectionReference ListedAdsRef = MyAdsRef.collection("Adverts");
        ListedAdsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List AdIdsLis = new ArrayList();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            AdPostModel adPostModel = documentSnapshot.toObject(AdPostModel.class);
                            String uid = firebaseAuth.getUid();
                            if (uid.equals(adPostModel.getUid())) {
                                adPostModelList.add(adPostModel);
                            }
                            setAdapter();

                        }
                    }
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), PostAdActivity.class));
            }
        });


        return root;
    }

    private void setAdapter() {
        viewMyAdsApater = new AdapterClassViewMyAds(adPostModelList, getContext(), this::deleteAd);
        recyclerView.setAdapter(viewMyAdsApater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void deleteAd(AdPostModel adPostModel) {

        /*Intent intent = new Intent(getActivity(), PostAdActivity.class);
        intent.putExtra("editad",adPostModel);
        intent.putExtra("editingclicked",true);
        startActivity(intent);*/
        //confirm delete action
        dialog.show();
        mConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start deleting advert
                MyAdsRef.collection("Adverts").document(adPostModel.getAdid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //cleanup images in Firebase Storage
                                dialog.dismiss();
                                StorageReference adStorageRef = FirebaseStorage.getInstance()
                                        .getReference(firebaseAuth.getUid()).child(adPostModel.getAdid());
                                adStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Images deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to delete advert, please try again", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        //cancel delete action
        mCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                return;
            }
        });

    }
}