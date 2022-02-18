package com.tutorials.ximexmobi.ui.home;

import static com.tutorials.ximexmobi.Constants.XIMEX_USERS_REF;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.adapters.AdapterClassAllListedItems;
import com.tutorials.ximexmobi.adapters.AdapterClassItemsNearYou;
import com.tutorials.ximexmobi.adapters.AdapterClassViewAdInBottomSheet;
import com.tutorials.ximexmobi.databinding.FragmentHomeBinding;
import com.tutorials.ximexmobi.models.AdPostModel;
import com.tutorials.ximexmobi.models.XimexUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterClassItemsNearYou.ListedAdClickListener,
        AdapterClassAllListedItems.AllListedAdClickListener {

    public static final String TAG = "HomeFragment";
    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewAdsNearYou, recyclerViewListedItems;
    private AdapterClassItemsNearYou viewAllListedAdsAdapter;
    private AdapterClassAllListedItems secondAdapter;
    AdapterClassViewAdInBottomSheet adapter;
    private List<AdPostModel> adPostModelNearYouList, adPostModelListedItems;
    private FirebaseFirestore ListedItemsRef, XimexUsersRef;
    private FirebaseAuth mAuth;
    private XimexUser mySelfUser, advertiser;
    private AutoCompleteTextView mCategoryAutocomplete, mSortByAutoComplete;
    private ArrayList<String> categoryList, sortCriteriaList;
    private ArrayAdapter<String> categoryAdapter, sortCriteriaAdapter;
    private String category, filterby;
    private Dialog mProgress;
    private Location myselfLocation;
    //private AdPostModel adPostModel;

    @Override
    public void onStart() {
        super.onStart();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        mProgress = new Dialog(getActivity());
        mProgress.setContentView(R.layout.progress_bar_custom_dialog);
        mProgress.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_bg));
        mProgress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgress.setCancelable(false);
        mProgress.show();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //-----------------------------------------BIND VIEWS---------------------------------------

        final TextView textView = binding.itemsNearYouHeadingTxt;
        recyclerViewAdsNearYou = binding.itemsNearYouRecyclerview;
        recyclerViewListedItems = binding.listedItemsRecyclerview;
        mCategoryAutocomplete = binding.autoCompleteCategory;
        mSortByAutoComplete = binding.autoCompleteSortBy;

        //Initialize db and auth instances
        ListedItemsRef = FirebaseFirestore.getInstance();
        XimexUsersRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //------------Set views, layouts and adapters----------------------------------------------

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAdsNearYou.setLayoutManager(linearLayoutManager);
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewListedItems.setLayoutManager(secondLayoutManager);
        adPostModelNearYouList = new ArrayList<>();
        adPostModelListedItems = new ArrayList<>();
        myselfLocation = new Location("");

        //Set Autocomplete lists for category and sortby
        //todo change this to an interface oe something because same code is used in Post Ad activity
        categoryList = new ArrayList<>();
        categoryList.add("All");
        categoryList.add("Laptop computers");
        categoryList.add("Smartphones");
        categoryList.add("Tablets");
        categoryList.add("Smart watches");
        categoryList.add("Other");
        sortCriteriaList = new ArrayList<>();
        sortCriteriaList.add("Date");
        // sortCriteriaList.add("Distance");
        sortCriteriaList.add("Price");
        category = "All";
        filterby = "timestamp";

        categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.textview_layout_entity, categoryList);
        sortCriteriaAdapter = new ArrayAdapter<>(getContext(), R.layout.textview_layout_entity, sortCriteriaList);
        mCategoryAutocomplete.setAdapter(categoryAdapter);
        // mCategoryAutocomplete.setThreshold(1);
        mSortByAutoComplete.setAdapter(sortCriteriaAdapter);
        //mSortByAutoComplete.setThreshold(1);
        mCategoryAutocomplete.setText(categoryAdapter.getItem(0).toString(), false);


        // Get Ximexuser object from Firestore
        DocumentReference MyUserDocRef = XimexUsersRef.collection(XIMEX_USERS_REF)
                .document(mAuth.getCurrentUser().getUid());

        MyUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    mySelfUser = documentSnapshot.toObject(XimexUser.class);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "User info not available, try later", Toast.LENGTH_SHORT).show();
                   // return;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailureHomeFragmnt_addmySelfUser: ");
                e.printStackTrace();
                Toast.makeText(getContext(), "User info not available, try later", Toast.LENGTH_SHORT).show();

            }
        });

        //Get listed and near user adverts from Firestore
        filterAdsforDisplay(category, filterby);

        /*mCategoryAutocomplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        mCategoryAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
                filterby = "timestamp";
                filterAdsforDisplay(category, filterby);
            }
        });

        mSortByAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("Date")) {
                    filterby = "timestamp";
                } else if (adapterView.getItemAtPosition(i).toString().equals("Price")) {
                    filterby = "price";
                }
                filterAdsforDisplay(category, filterby);
            }
        });

        return root;
    }

    /*private void sortBySelectedCriteria(String filterby) {
        switch (filterby) {
            default:
                for (int i = 0; i < adPostModelListedItems.size(); i++) {
                    AdPostModel adPostModel = new AdPostModel();
                    adPostModel = adPostModelListedItems.get(i);
                    ArrayList<AdPostModel> adPostModels = new ArrayList<>();


                }
                break;
        }


    }*/

    private void filterAdsforDisplay(String category, String filterby) {
        mProgress.show();
        adPostModelListedItems.clear();
        adPostModelNearYouList.clear();
        CollectionReference ListedAdsRef = ListedItemsRef.collection("Adverts");
        ListedAdsRef.orderBy(filterby, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            //
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            AdPostModel adPostModel1 = documentSnapshot.toObject(AdPostModel.class);
                            DocumentReference UserdocRef = XimexUsersRef.collection(XIMEX_USERS_REF).document(adPostModel1.getUid());
                            UserdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                    DocumentSnapshot documentSnapshot1 = task1.getResult();
                                    XimexUser advertiserUser = documentSnapshot1.toObject(XimexUser.class);
                                    try {


                                        Location advertiserLocation = new Location("");
                                        myselfLocation.setLatitude(mySelfUser.getGeoPoint().getLatitude());
                                        myselfLocation.setLongitude(mySelfUser.getGeoPoint().getLongitude());

                                        advertiserLocation.setLatitude(advertiserUser.getGeoPoint().getLatitude());
                                        advertiserLocation.setLongitude(advertiserUser.getGeoPoint().getLongitude());
                                        float distanceInMeters = myselfLocation.distanceTo(advertiserLocation);
                                        //filter ads, get only ads with 25km radius of the user address
                                        if (distanceInMeters < 25000) {
                                            adPostModelNearYouList.add(adPostModel1);

                                        }

                                        if (category.equals("Laptop computers") && adPostModel1.getCategory().equals("Laptop computers")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        } else if (category.equals("Smartphones") && adPostModel1.getCategory().equals("Smartphones")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        } else if (category.equals("Tablets") && adPostModel1.getCategory().equals("Tablets")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        } else if (category.equals("Smart watches") && adPostModel1.getCategory().equals("Smart watches")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        } else if (category.equals("Other") && adPostModel1.getCategory().equals("Other")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        } else if (category.equals("All")) {
                                            adPostModelListedItems.add(adPostModel1);
                                        }


                                    } catch (Exception e) {

                                    }
                                    setAdapter();
                                    mProgress.dismiss();
                                }

                            });

                        }

                    }

                }

            }

        });
    }

    public void callParentMethod() {
        getActivity().onBackPressed();
    }


    private void setAdapter() {
        viewAllListedAdsAdapter = new AdapterClassItemsNearYou(adPostModelNearYouList, getContext(), this::viewSelectedAd);
        recyclerViewAdsNearYou.setAdapter(viewAllListedAdsAdapter);
        secondAdapter = new AdapterClassAllListedItems(adPostModelListedItems, getContext(), this::viewAllSelectedAd);
        recyclerViewListedItems.setAdapter(secondAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void viewSelectedAd(AdPostModel adPostModel) {
        showSelectedAdInBottomSheet(adPostModel);

    }


    @Override
    public void viewAllSelectedAd(AdPostModel adPostModel) {

        showSelectedAdInBottomSheet(adPostModel);
    }

    private void showSelectedAdInBottomSheet(AdPostModel adPostModel) {
        mProgress.show();
        // XimexUser advertiserUser = new XimexUser();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.view_advert_bottom_sheet_layout,
                        (ConstraintLayout) bottomSheetDialog.findViewById(R.id.view_ad_bottom_sheet_container)
                );

        FirebaseFirestore AdvertiserRef = FirebaseFirestore.getInstance();

        TextView mHeading = bottomSheetView.findViewById(R.id.heading_text_view);
        //ImageSwitcher switcher = bottomSheetView.findViewById(R.id.switcher);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.ad_images_recyclerview);
        ImageButton mCallAdvertiser = bottomSheetView.findViewById(R.id.call_advertiser);
        ImageButton mWhatsAppAdevrtiser = bottomSheetView.findViewById(R.id.whatsapp_advertiser);
        TextView mPrice = bottomSheetView.findViewById(R.id.price);
        TextView mPostedDate = bottomSheetView.findViewById(R.id.posted);
        TextView mDistance = bottomSheetView.findViewById(R.id.distance);
        TextView mCondition = bottomSheetView.findViewById(R.id.condition);
        TextView mContact = bottomSheetView.findViewById(R.id.contact);
        TextView mEmail = bottomSheetView.findViewById(R.id.email);

        final ArrayList<String> urlList;
        urlList = new ArrayList<>();
        urlList.clear();
        if (adPostModel.getImg1() != null) {
            urlList.add(adPostModel.getImg1());
        }
        if (adPostModel.getImg2() != null) {
            urlList.add(adPostModel.getImg2());
        }
        if (adPostModel.getImg3() != null) {
            urlList.add(adPostModel.getImg3());
        }
        if (adPostModel.getImg4() != null) {
            urlList.add(adPostModel.getImg4());
        }
        if (adPostModel.getImg5() != null) {
            urlList.add(adPostModel.getImg5());
        }
        if (adPostModel.getImg6() != null) {
            urlList.add(adPostModel.getImg6());
        }

        LinearLayoutManager thirdlinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(thirdlinearLayoutManager);

        adapter = new AdapterClassViewAdInBottomSheet(getContext(), urlList);
        recyclerView.setAdapter(adapter);
        //

        try {
            // Get Advertiser Ximexuser object from Firestore
            DocumentReference AdevertiserDocRef = AdvertiserRef.collection(XIMEX_USERS_REF)
                    .document(adPostModel.getUid());

            AdevertiserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // documentSnapshot = task.getResult();

                    advertiser = documentSnapshot.toObject(XimexUser.class);
                    Date postedDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        postedDate = simpleDateFormat.parse(adPostModel.getPosteddate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Date today = new Date();
                    PrettyTime today = new PrettyTime();
                    Location selectedAdLocation = new Location("");
                    selectedAdLocation.setLatitude(advertiser.getGeoPoint().getLatitude());
                    selectedAdLocation.setLongitude(advertiser.getGeoPoint().getLongitude());
                    float distanceaway = myselfLocation.distanceTo(selectedAdLocation);
                    mHeading.setText(adPostModel.getItemname());
                    mPrice.setText(adPostModel.getPrice());
                    mPostedDate.setText(today.format(postedDate));
                    mDistance.setText(Math.round(distanceaway / 1000) + " Km away");
                    mCondition.setText(adPostModel.getCondition());
                    mContact.setText(advertiser.getCallsnumber());
                    mEmail.setText(advertiser.getEmail());
                    mProgress.dismiss();
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                    int totalviews = Integer.parseInt(adPostModel.getTotalviews());
                    totalviews++;
                    adPostModel.setTotalviews(Integer.toString(totalviews));
                    ListedItemsRef.collection("Adverts").document(adPostModel.getAdid()).set(adPostModel);


                }
            });


        } catch (Exception e) {

        }

        mWhatsAppAdevrtiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    Uri uri = Uri.parse("smsto:" + advertiser.getCallsnumber());
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setType("text/plain");
                    String messagebody = "Hi " + advertiser.getFullname() + "I saw your " + adPostModel.
                            getItemname() + " advert on Ximex Mobi,  is this item still available?";
                    // i.setType("text/plain");
                    // i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                    i.putExtra(android.content.Intent.EXTRA_TEXT, messagebody);
                    i.setData(Uri.parse("smsto:" + advertiser.getCallsnumber()));
                    i.setPackage("com.whatsapp");
                    increamentTotalResponses(adPostModel);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();

                }

/*
               if (isWhatsAppInstalled()){
                    Intent  intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://whatsapp.com/send?phone="
                            +advertiser.getCallsnumber()+"&text="+"Hey I saw your add on XimexMobi"));
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "WhatsApp not found!", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        mCallAdvertiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isPermissionGranted()) {
                    call_action();
                    increamentTotalResponses(adPostModel);
                }
            }
        });

    }

    private void increamentTotalResponses(AdPostModel adPostModel) {
        int totalresponses = Integer.parseInt(adPostModel.getResponses());
        totalresponses++;
        adPostModel.setResponses(Integer.toString(totalresponses));
        ListedItemsRef.collection("Adverts").document(adPostModel.getAdid()).set(adPostModel);

    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    private boolean isWhatsAppInstalled() {
        PackageManager packageManager = getActivity().getPackageManager();
        boolean whatsappinstalled;
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            whatsappinstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            whatsappinstalled = false;

        }
        return whatsappinstalled;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void call_action() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + advertiser.getCallsnumber()));
        // if(intent.resolveActivity(getActivity().getPackageManager())!=null){

        //}
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}