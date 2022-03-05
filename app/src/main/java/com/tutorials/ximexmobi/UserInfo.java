package com.tutorials.ximexmobi;

import static com.tutorials.ximexmobi.Constants.XIMEX_USERS_REF;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tutorials.ximexmobi.adapters.PlacesAutoCompleteAdapter;
import com.tutorials.ximexmobi.databinding.ActivitySubmitUserInfoBinding;
import com.tutorials.ximexmobi.models.XimexUser;
import com.tutorials.ximexmobi.ui.myads.PostAdActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {
    public static final String TAG = "UserInfoActvty";
    private ActivitySubmitUserInfoBinding binding;
    private FirebaseFirestore firebaseFirestoreRef;
    private FirebaseAuth mAuth;
    private Dialog dialog,mProgress;
    private Uri imageUri;
    private XimexUser ximexUser;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_user_info);


        //----------------------------------BIND VIEWS------------------------------------------
        binding = ActivitySubmitUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final CircleImageView mProfPic = binding.profFic;
        final ImageView mTakePhotoCam = binding.editPicCam;
        final TextInputEditText mUserFullname = binding.userName;
        final TextInputEditText mCellnumber = binding.cellNumber;
        final AutoCompleteTextView mSuburbAutoComplete = binding.suburb;
        final TextInputEditText mEmailAddress = binding.email;
        final ImageButton mSubmitButton = binding.submitUserInfoBtn;

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            imageUri = intent.getData();
                            mProfPic.setImageURI(imageUri);
                           // pre

                        }
                    }
                });

        mSuburbAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(UserInfo.this, android.R.layout.simple_expandable_list_item_1));

        //--------------------------DATABASE AND REFS-------------------------------------------
        firebaseFirestoreRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        DocumentReference XimexUserRef = firebaseFirestoreRef.collection(XIMEX_USERS_REF)
                .document(mAuth.getCurrentUser().getUid());

        //---------------------------GET USER FROM FIRESTORE------------------------------------
        XimexUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();
                ximexUser = new XimexUser();
                ximexUser = documentSnapshot.toObject(XimexUser.class);
                //Update fields with corresponding user details
                mUserFullname.setText(ximexUser.getFullname());
                mCellnumber.setText(ximexUser.getCallsnumber());
                mSuburbAutoComplete.setText(ximexUser.getSurburb());
                mEmailAddress.setText(ximexUser.getEmail());
               if(!(ximexUser.getProfilepic()==null)){
                   try{
                   Glide.with(getApplicationContext())
                           .load(ximexUser.getProfilepic())
                           .placeholder(getDrawable(R.drawable.ic_baseline_account_circle_24))
                           .into(mProfPic);}
                   catch (Exception e){
                       //todo remove toast later
                       Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onGetXimexUserInfoFailure: " + e.getMessage());

            }
        });

        try {



        //Ask user for permission to access internal storage using alert dialog
        dialog = new Dialog(UserInfo.this);
        dialog.setContentView(R.layout.storage_permission_request);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.setCancelable(false);

        mProgress = new Dialog(UserInfo.this);
        mProgress.setContentView(R.layout.progress_bar_custom_dialog);
        mProgress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        mProgress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgress.setCancelable(false);  }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "UserInfoErr: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(UserInfo.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            try{
            dialog.show();}
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "UserInfoErr 1"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Button okay = dialog.findViewById(R.id.permission_gran_btn);
        Button cancel = dialog.findViewById(R.id.permission_deny_btn);

        //User grants permissions
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        UserInfo.this,
                        new String[]
                                {
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                }, 0);
                dialog.dismiss();
                return;
            }
        });

        //User rejects permissions
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                return;
            }
        });

        //Listen for profile pic upload clicks
        mProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ContextCompat.checkSelfPermission(UserInfo.this, Manifest.permission
                            .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        dialog.show();
                        return;
                    }
                    // User  grants local storage access permission
                    okay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(UserInfo.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            }, 0);
                            dialog.dismiss();
                            Intent galleryIntent = new Intent();
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryIntent.setType("image/*");
                            activityResultLauncher.launch(galleryIntent);
                        }
                    });

                    //User denies access to local storage access permission
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            return;

                        }
                    });

                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    activityResultLauncher.launch(galleryIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Save Updated user details to firestore
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                //  check  that no fields are empty
                if (!validateFullNameInput()|!validateCellnumberInput()|!validateLocationInput()|
                        !validateEmailInput()|!validateProfilePicInput()) {
                    mProgress.dismiss();
                    return;
                } else if(ximexUser.getProfilepic()==null){


                    StorageReference UserProfilePictureRef = storageReference.child(mAuth.getCurrentUser()
                            .getUid()).child("Profile picture").child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
                    UserProfilePictureRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            UserProfilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set updated user details to ximexUser object
                                    String imageurl = uri.toString();
                                    getNewUserDetails().setProfilepic(imageurl);
                                    //ximexUser.setGeoPoint(getLocationFromAddress(ximexUser.getSurburb()));
                                    XimexUserRef.set(ximexUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            mProgress.dismiss();
                                            Toast.makeText(getApplicationContext(), "User details updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mProgress.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed to  update user details", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //progress bar set visibility to visible


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to upload profile picture!", Toast.LENGTH_SHORT).show();
                        }
                    });



                }else{
                    getNewUserDetails();
                    XimexUserRef.set(ximexUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            mProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "User details updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to  update user details", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });


    }

   /* private GeoPoint getLocationFromAddress(String surburb) {
        Geocoder  geocoder = new Geocoder(this);
        List<Address> address;
        GeoPoint point = null;

        try{
            address = geocoder.getFromLocationName(surburb,1);
            if(address==null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            point = new GeoPoint((double) (location.getLatitude()*1E6),(double) (location.getLongitude()*1E6));
        }catch (Exception e){
            e.printStackTrace();
        }

        return point;
    }*/

    private XimexUser getNewUserDetails() {
        ximexUser.setFullname(binding.userName.getEditableText().toString().trim());
        ximexUser.setCallsnumber(binding.cellNumber.getEditableText().toString().trim());
        ximexUser.setSurburb(binding.suburb.getEditableText().toString().trim());
        ximexUser.setEmail(binding.email.getEditableText().toString().trim());
        ximexUser.setWhatsappnumber(ximexUser.getCallsnumber());

        Geocoder  geocoder = new Geocoder(UserInfo.this);
        List<Address> address;
        GeoPoint point = null;

        try{
            address = geocoder.getFromLocationName(ximexUser.getSurburb(),1);
            if(address==null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            point = new GeoPoint((double) (location.getLatitude()),(double) (location.getLongitude()));
            ximexUser.setGeoPoint(point);
        }catch (Exception e){
            e.printStackTrace();
        }


        return ximexUser;
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }


    //-------------------------------------METHODS TO VALIDATE INPUT FIELDS-------------------------
    private boolean validateFullNameInput() {
        String val = binding.userName.getEditableText().toString();
        if (val.isEmpty()) {
            binding.userName.setError("Filed cannot be empty");
            return false;
        } else {
            binding.userName.setError(null);
            return true;
        }

    }

    private boolean validateCellnumberInput() {
        String val = binding.cellNumber.getEditableText().toString();
        if (val.isEmpty()) {
            binding.cellNumber.setError("Filed cannot be empty");
            return false;
        } else {
            binding.cellNumber.setError(null);
            return true;
        }

    }

    private boolean validateLocationInput() {
        String val = binding.suburb.getEditableText().toString();
        if (val.isEmpty()) {
            binding.suburb.setError("Filed cannot be empty");
            return false;
        } else {
            binding.suburb.setError(null);
            return true;
        }

    }

    private boolean validateEmailInput() {
        String val = binding.email.getEditableText().toString();
        if (val.isEmpty()) {
            binding.email.setError("Filed cannot be empty");
            return false;
        } else {
            binding.email.setError(null);
            return true;
        }

    }

    private boolean validateProfilePicInput() {
        Uri val = imageUri;

        if (val==null&ximexUser.getProfilepic()==null) {
            Toast.makeText(getApplicationContext(), "No profile picture has been set!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            binding.email.setError(null);
            return true;
        }

    }


}