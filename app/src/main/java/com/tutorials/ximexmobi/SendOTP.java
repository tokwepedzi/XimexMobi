package com.tutorials.ximexmobi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutorials.ximexmobi.models.XimexUser;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SendOTP extends AppCompatActivity {
    public static final String TAG ="sendotp";
    private PinView mPinView ;
    private Button mContinue;
    private FirebaseAuth mAuth;
    private FirebaseFirestore XimexUserRef;
    private String codeFromFirebase;
    private PhoneAuthProvider.ForceResendingToken token;
    private String ximexuserfullanme,ximexusercellnum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        // -----------Hooks--------------------------------------------------------------------------
        mPinView = findViewById(R.id.pinview);
        mContinue = findViewById(R.id.continue_btn);
        mAuth = FirebaseAuth.getInstance();
        XimexUserRef = FirebaseFirestore.getInstance();

        //  Receive user details from register activity
        Intent intent =  getIntent();
        String fullanme = intent.getStringExtra("name");
        String cellnumber = intent.getStringExtra("cell");
        ximexuserfullanme=fullanme;
        ximexusercellnum = cellnumber;
        sendVerificationCode(cellnumber);
    }

    private void sendVerificationCode(String cellnumber) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)

                .setPhoneNumber(cellnumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeFromFirebase=s;
                    token = forceResendingToken;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        mPinView.setText(code);
                        //Verify code and sign in user
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(SendOTP.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                    // Make Resend button visible
                    // and impliment onclick listener here https://www.youtube.com/watch?v=A_AfylfINQM,   https://www.youtube.com/watch?v=UQ_y5Cq5tS4
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeFromFirebase, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                           // Add Ximexuser to firestore
                            addXimexUserToFirestore();

                        }else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

       /* mAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Signin user
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

                Toast.makeText(SendOTP.this, "Error verifying code: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SendOTP.this,Register.class));
                finish();
            }
        });*/


    }

    private void addXimexUserToFirestore() {
        try{
        Date date = new Date();
        DocumentReference documentReference = XimexUserRef
                .collection("Ximexusers")
                .document(mAuth.getCurrentUser().getUid());

        XimexUser ximexUser = new XimexUser();
        ximexUser.setFullname(ximexuserfullanme);
        ximexUser.setCallsnumber(ximexusercellnum);
        ximexUser.setUsersince(date.toString());
        ximexUser.setUid(mAuth.getUid());

        //set ximexuser in firestore ximex users ref
        documentReference.set(ximexUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Signin successfull", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SendOTP.this,Dashboard.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: Registration did not complete", Toast.LENGTH_SHORT).show();
           return;
            }
        });}
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}