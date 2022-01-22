package com.tutorials.ximexmobi;

import static com.tutorials.ximexmobi.Constants.XIMEX_USERS_REF;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.tutorials.ximexmobi.databinding.ActivityDashboardBinding;
import com.tutorials.ximexmobi.models.XimexUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {
    public static final String TAG = "Dashboardactvty";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private XimexUser ximexUser;
    private FirebaseAuth firebaseAuth;
    private Dialog dialog,missingInfoDialog,mProgress;
    private FirebaseFirestore UsersRef;
    private TextView mUserEmail;
    private CircleImageView mProfPic;


    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Leaving", Toast.LENGTH_SHORT).show();
        finish();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new Dialog(Dashboard.this);
        mProgress.setContentView(R.layout.progress_bar_custom_dialog);
        mProgress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        mProgress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgress.setCancelable(false);
        mProgress.show();

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Get Users ref ----Firebase
        UsersRef = FirebaseFirestore.getInstance();


        setSupportActionBar(binding.appBarDashboard.toolbar);

        //sms/message floating action button onclickListener
       /* binding.appBarDashboard.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = binding.drawerLayout;
        //NavigationView navigationView = binding.navView;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hearderview = navigationView.getHeaderView(0);
        mUserEmail = (TextView) hearderview.findViewById(R.id.user_nav_email);
        mProfPic = (CircleImageView) hearderview.findViewById(R.id.user_prof_pic) ;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_ads, R.id.nav_ximex_store)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view_menu);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        firebaseAuth = FirebaseAuth.getInstance();

        ximexUser = new XimexUser();
        ximexUser.setUid(firebaseAuth.getUid());

        mProgress.dismiss();

        // Ask user for  location permission using dialog
        dialog = new Dialog(Dashboard.this);
        dialog.setContentView(R.layout.location_permission_request);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            dialog.show();
        }

        Button okay = dialog.findViewById(R.id.permission_gran_btn);
        Button cancel = dialog.findViewById(R.id.permission_deny_btn);


        // User accepts/grants location permission
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(
                        Dashboard.this,
                        new String[]
                                {
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.BLUETOOTH,
                                        Manifest.permission.BLUETOOTH_ADMIN,
                                        Manifest.permission.BLUETOOTH_PRIVILEGED,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                        //Manifest.permission.READ_EXTERNAL_STORAGE
                                }, 0);
                dialog.dismiss();
                return;

            }
        });

        //  User rejects location permission
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                return;

            }
        });

        // Update UI fields with the appropriate user details
        updateUI(ximexUser);
        mProgress.show();

    }

    // gets user details as Ximexuser object from Firestore and updates UI fields
    private void updateUI(XimexUser ximexUser) {
       try {DocumentReference XimexUserRef = UsersRef.collection(XIMEX_USERS_REF).document(ximexUser.getUid());
        XimexUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                XimexUser ximexUser1 = new XimexUser();
                DocumentSnapshot documentSnapshot = task.getResult();
                ximexUser1 = documentSnapshot.toObject(XimexUser.class);
                mUserEmail.setText(ximexUser1.getEmail());
                Picasso.get()
                        .load(Uri.parse(ximexUser1.getProfilepic()))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .into(mProfPic);
                mProgress.dismiss();
                // Dialog: if user has no full details ask user to submit full details
                if(ximexUser1.getGeoPoint()==(null)||ximexUser1.getSurburb()==null||ximexUser1.getCallsnumber()==null||ximexUser1.getWhatsappnumber()==null){
                    missingInfoDialog = new Dialog(Dashboard.this);
                    missingInfoDialog.setContentView(R.layout.missing_info_dialog);
                    missingInfoDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
                    missingInfoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    missingInfoDialog.setCancelable(false);

                    Button update = missingInfoDialog.findViewById(R.id.missing_info_update_btn);
                    Button notnow = missingInfoDialog.findViewById(R.id.missing_info_notnw_btn);
                    missingInfoDialog.show();

                    notnow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            missingInfoDialog.dismiss();
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            missingInfoDialog.dismiss();
                            startActivity(new Intent(Dashboard.this, UserInfo.class));
                            /*FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment_content_dashboard, new UserInfoFragment(),"move_to_userinfo");
                            fragmentTransaction.commit();*/
                           /* getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment_content_dashboard
                                    ,new UserInfoFragment()).commit();*/
                        }
                    });

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onUpdateUIFailure: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "User not found " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });}
       catch (Exception e){
           e.printStackTrace();
           Toast.makeText(getApplicationContext(), "Error: "+"PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
           Log.d(TAG, "updateUI: PERMISSION_DENIED: Missing or insufficient permissions.");
       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}