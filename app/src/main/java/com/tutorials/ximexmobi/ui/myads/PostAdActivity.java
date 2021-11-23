package com.tutorials.ximexmobi.ui.myads;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.SendOTP;
import com.tutorials.ximexmobi.databinding.ActivityDashboardBinding;
import com.tutorials.ximexmobi.databinding.ActivityPostAdBinding;
import com.tutorials.ximexmobi.databinding.FragmentMyadsBinding;
import com.tutorials.ximexmobi.models.AdPostModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import com.tutorials.ximexmobi.adapters.AdapterClassPostAd;

public class PostAdActivity extends AppCompatActivity {
    private ActivityPostAdBinding activityPostAdBinding;
    private ArrayList<Uri> imageUris;
    private ArrayList<String> imageUrls;
    private FirebaseFirestore AdsRef ;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //position of selected image
    int position = 0;

    //request code for picking images
    private static final int PICK_IMAGES_REQUEST_CODE = 0;
    // private ImageSwitcher imageSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        activityPostAdBinding = ActivityPostAdBinding.inflate(getLayoutInflater());
        setContentView(activityPostAdBinding.getRoot());

        AdsRef = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final ImageButton addPhotosBtn = activityPostAdBinding.addImagesButton;
        final ImageSwitcher imageSwitcher = activityPostAdBinding.switchImage;
        final ImageButton nextImage = activityPostAdBinding.nextImage;
        final ImageButton previousImage = activityPostAdBinding.previousImage;
        final ImageButton mPostAd = activityPostAdBinding.doneAddingAdButton;
        final TextInputEditText mItemName = activityPostAdBinding.itemName;
        final TextInputEditText mItemDescription = activityPostAdBinding.itemDescription;
        final TextInputEditText mItemPrice = activityPostAdBinding.price;
        final TextInputEditText mItemCondition = activityPostAdBinding.condition;



        //  init arraylist
        imageUris = new ArrayList<>();
        imageUrls = new ArrayList<>();

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        //  Click handl
        //  e, pick 6 or less images
        addPhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImagesIntent();
            }
        });

        // Click handle,show previous image
        previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    position--;
                    activityPostAdBinding.switchImage.setImageURI(imageUris.get(position));
                } else {
                    Toast.makeText(getApplicationContext(), "No more images to show!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //  Click handle, show next image
        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < imageUris.size() - 1) {
                    position++;
                    activityPostAdBinding.switchImage.setImageURI(imageUris.get(position));
                }else {
                    Toast.makeText(getApplicationContext(), "No more images to show!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityPostAdBinding.doneAddingAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAdDetailsAndPost();
            }
        });
    }

    private void getAdDetailsAndPost() {

        if (!validateItemNameInput() | !validateItemDescriptionInput() | !validateItemPriceInput()
                | !validateItemConditionInput()) {
            return;
        } else {

            AdPostModel adPostModel= new AdPostModel();
            adPostModel.setItemname(activityPostAdBinding.itemName.getEditableText().toString());
            adPostModel.setDescription(activityPostAdBinding.itemDescription.getEditableText().toString());
            adPostModel.setPrice(activityPostAdBinding.price.getEditableText().toString());
            adPostModel.setCondition(activityPostAdBinding.condition.getEditableText().toString());
            adPostModel.setUid(firebaseAuth.getUid());


            DocumentReference documentReference = AdsRef
                    .collection("Adverts")
                    .document();


            documentReference.set(adPostModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Toast.makeText(getApplicationContext(), "Ad Posted ", Toast.LENGTH_SHORT).show();
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                AdPostModel adPostModel1 = new AdPostModel();
                                String adid = documentReference.getId();
                                adPostModel1 = task.getResult().toObject(AdPostModel.class);
                                Date date = new Date(adPostModel1.getTimestamp().getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String todaydatestring = simpleDateFormat.format(date);
                                adPostModel1.setAdid(adid);
                                adPostModel1.setPosteddate(todaydatestring);
                                adPostModel1.setAvailability("Available");
                                adPostModel1.setTotalviews("0");
                                uploadImagesAndGetLinks(adPostModel1,documentReference);
                                documentReference.set(adPostModel1);
                            }

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

    }

    private void uploadImagesAndGetLinks(AdPostModel adPostModel1, DocumentReference documentReference) {
        for(int i=0;i<imageUris.size();i++){
            StorageReference ImagesRef = storageReference.child(firebaseAuth.getUid()).child(adPostModel1.getAdid()).child(System.currentTimeMillis() + "." + getFileExtension(imageUris.get(i)));
            int j= i;
            ImagesRef.putFile(imageUris.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           // mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Upload uri successful"+ uri, Toast.LENGTH_SHORT).show();
                            imageUrls.add(uri.toString());
                            try{
                           switch (j){
                               case 0:
                                   adPostModel1.setImg1(uri.toString());
                                   break;
                               case 1:
                                   adPostModel1.setImg2(uri.toString());
                                   break;
                               case 2:
                                   adPostModel1.setImg3(uri.toString());
                                   break;
                               case 3:
                                   adPostModel1.setImg4(uri.toString());
                                   break;
                               case 4:
                                   adPostModel1.setImg5(uri.toString());
                                   break;
                               case 5:
                                   adPostModel1.setImg6(uri.toString());
                                   break;
                           }}
                            catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                /*new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            adPostModel1.setImg1(imageUrls.get(0));
                                            adPostModel1.setImg2(imageUrls.get(1));
                                            adPostModel1.setImg3(imageUrls.get(2));
                                            adPostModel1.setImg4(imageUrls.get(3));
                                            adPostModel1.setImg5(imageUrls.get(4));
                                            adPostModel1.setImg6(imageUrls.get(5));}
                                        catch (Exception e){
                                            Toast.makeText(getApplicationContext(), "Error Handled", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },5000);*/
                                documentReference.set(adPostModel1);
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                   // mProgressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   // mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Uploading profile image failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }








    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            if (count > 6) {
                                Toast.makeText(getApplicationContext(), "A maximum of six images is allowed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (int i = 0; i < count; i++) {
                                //  get image uri at specific index
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                imageUris.add(imageUri);//  add to list

                            }
                            //  set first image to our image switcher
                            activityPostAdBinding.switchImage.setImageURI(imageUris.get(0));
                            position = 0;


                        } else {
                            int count = (imageUris.size());
                            if(count>5){
                                Toast.makeText(getApplicationContext(), "Here six images is allowed"+count, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Uri imageUri = data.getData();
                            imageUris.add(imageUri);
                            //set  image to switcher
                            activityPostAdBinding.switchImage.setImageURI(imageUris.get(0));
                            position = 0;
                        }
                    }
                }
            }
    );


    private void pickImagesIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.createChooser(intent, "Select images");
        //startActivityfor

        activityResultLauncher.launch(intent);
    }

    private boolean validateItemNameInput() {
        String val = activityPostAdBinding.itemName.getEditableText().toString();
        if (val.isEmpty()) {
            activityPostAdBinding.itemName.setError("Field cannot be empty");
            return false;
        } else {
            activityPostAdBinding.itemName.setError(null);
            return true;
        }

    }
    private boolean validateItemDescriptionInput() {
        String val = activityPostAdBinding.itemName.getEditableText().toString();
        if (val.isEmpty()) {
            activityPostAdBinding.itemName.setError("Field cannot be empty");
            return false;
        } else {
            activityPostAdBinding.itemName.setError(null);
            return true;
        }

    }
    private boolean validateItemPriceInput() {
        String val = activityPostAdBinding.itemName.getEditableText().toString();
        if (val.isEmpty()) {
            activityPostAdBinding.itemName.setError("Field cannot be empty");
            return false;
        } else {
            activityPostAdBinding.itemName.setError(null);
            return true;
        }

    }
    private boolean validateItemConditionInput() {
        String val = activityPostAdBinding.itemName.getEditableText().toString();
        if (val.isEmpty()) {
            activityPostAdBinding.itemName.setError("Field cannot be empty");
            return false;
        } else {
            activityPostAdBinding.itemName.setError(null);
            return true;
        }

    }
}