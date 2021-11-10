package com.tutorials.ximexmobi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    public Register(PhoneAuthOptions mPhoneAuthOptions) {

    }

    private TextInputEditText mName, mCellNumber;
    private Button mContinue;
    private CountryCodePicker countryCodePicker;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if(!validateNameInput()| !validateCellNumInput()){
                    return;
                }else {

                    //  Get User's cell number and begin Firebase registration process
                    String countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
                    String cellnumber = countrycode+(mCellNumber.getEditableText().toString().trim());
                    registerUser(cellnumber);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };


    }

    private boolean validateNameInput() {
        String val = mName.getEditableText().toString();
        if (val.isEmpty()) {
            mName.setError("Field cannot be empty");
            return false;
        }else {
            mName.setError(null);
            return true;
        }

    }

    private boolean validateCellNumInput() {
        String val = mCellNumber.getEditableText().toString();
        if (val.isEmpty()) {
            mName.setError("Field cannot be empty");
            return false;
        }else {
            mName.setError(null);
            return true;
        }

    }


    public void registerUser(String phonenumber) {


        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)

                .setPhoneNumber(phonenumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }


}