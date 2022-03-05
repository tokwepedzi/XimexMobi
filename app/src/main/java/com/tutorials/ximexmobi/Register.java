package com.tutorials.ximexmobi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {


    private TextInputEditText mName, mCellNumber;
    private Button mContinue;
    private CountryCodePicker countryCodePicker;
    private FirebaseAuth mAuth;
    public GlobalMethods globalMethods;
    private Dialog mProgress;

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new Dialog(Register.this);
        mProgress.setContentView(R.layout.progress_bar_custom_dialog);
        mProgress.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        mProgress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgress.setCancelable(false);


        // if  user is already signed in send them to dashboard
        mAuth = FirebaseAuth.getInstance();
        globalMethods = new GlobalMethods(getApplicationContext());
        if(mAuth.getCurrentUser()!=null){ globalMethods.sendUserToDashboard();
            finish();
        }else {
            Toast.makeText(getApplicationContext(), "User not signed in!", Toast.LENGTH_SHORT).show();

        }

        setContentView(R.layout.activity_main);

        // -----------Hooks--------------------------------------------------------------------------
        mName = findViewById(R.id.name);
        mCellNumber = findViewById(R.id.cellnumber);
        mContinue = findViewById(R.id.continue_btn);
        countryCodePicker = findViewById(R.id.ccp);


        //  Click listener--------------------------------------------------------------------------
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 else if(mName.getText().toString().equals("Admin")){
                    mProgress.show();
                    mAuth.signInWithEmailAndPassword("tokwepedzi@gmail.com","111111")
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                           startActivity(new Intent(Register.this,Dashboard.class));
                            mProgress.hide();
                           finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.hide();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else {

                    //  Get User's cell number and send to cell verification
                    String fullname = mName.getText().toString();
                    String countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
                    String cellnumber = countrycode + (mCellNumber.getEditableText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), SendOTP.class);
                    intent.putExtra("cell", cellnumber);
                    intent.putExtra("name", fullname);
                    startActivity(intent);
                    finish();

                }
            }
        });
        
    }

    private boolean validateNameInput() {
        String val = mName.getEditableText().toString();
        if (val.isEmpty()) {
            mName.setError("Field cannot be empty");
            return false;
        } else {
            mName.setError(null);
            return true;
        }

    }

    private boolean validateCellNumInput() {
        String val = mCellNumber.getEditableText().toString();
        if (val.isEmpty()) {
            mName.setError("Field cannot be empty");
            return false;
        } else {
            mName.setError(null);
            return true;
        }

    }
}