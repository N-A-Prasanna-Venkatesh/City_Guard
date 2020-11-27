package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAuthorization extends AppCompatActivity {

    DatabaseReference userRef;
    private EditText phoneText,codeText;
    private Button continueNextButton;
    private CountryCodePicker ccp;
    private String checker="",phoneNumber="";
    private RelativeLayout relativeLayout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth auth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;
    int ct=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authorization);

        userRef= FirebaseDatabase.getInstance().getReference();

        phoneText=findViewById(R.id.phoneText);
        codeText=findViewById(R.id.codeText);
        continueNextButton=findViewById(R.id.continueNextButton);
        ccp=findViewById(R.id.ccp);
        relativeLayout=findViewById(R.id.phoneAuth);

        auth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        ccp.registerCarrierNumberEditText(phoneText);

        continueNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ct==0){

                    if (continueNextButton.getText().equals("Submit") || continueNextButton.getText().equals("Code Sent")) {
                        String verificationcode = codeText.getText().toString();
                        if (verificationcode.equals("")) {
                            Toast.makeText(PhoneAuthorization.this, "Please write the verification code first..", Toast.LENGTH_SHORT).show();

                        } else {
                            loadingBar.setTitle("Verification of code in Progress");
                            loadingBar.setCanceledOnTouchOutside(false);
                            loadingBar.setMessage("Please wait while we checking you inside...");
                            loadingBar.show();
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);

                            signInWithPhoneAuthCredential(credential);

                        }
                    } else {
                        phoneNumber = ccp.getFullNumberWithPlus();

                        if (!phoneText.toString().equals("")) {
                            loadingBar.setTitle("Authorization in Progress");
                            loadingBar.setCanceledOnTouchOutside(false);
                            loadingBar.setMessage("Please wait while we Authorize you inside...");
                            loadingBar.show();

                            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneAuthorization.this, mCallbacks);
                            Toast.makeText(PhoneAuthorization.this, "The phone number is"+phoneNumber, Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(PhoneAuthorization.this, "Please Enter a valid phone number.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(PhoneAuthorization.this, "Processing your existing data..", Toast.LENGTH_SHORT).show();
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


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid

                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);
                continueNextButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneAuthorization.this, "Verification failed...Please Try Again Later...", Toast.LENGTH_SHORT).show();
              /*  if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
*/
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                relativeLayout.setVisibility(View.GONE);
                checker="Code Sent";
                continueNextButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                Toast.makeText(PhoneAuthorization.this, "Code has been sent Please check..", Toast.LENGTH_SHORT).show();
                // ...
            }
        };

    }
    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            for (int i = 0; i < firebaseUser.getProviderData().size(); i++) {
                if (firebaseUser.getProviderData().get(i).equals("phone")) {

                    ct=1;
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("users").hasChild(firebaseUser.getUid())){
                                String Value=snapshot.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();
                                if (Value.equals("verified")){
                                    Intent intent=new Intent(PhoneAuthorization.this,Wanted_List.class);
                                    intent.putExtra("access",0);
                                    startActivity(intent);
                                }else{
                                    userRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("exists");
                                    Intent intent=new Intent(PhoneAuthorization.this,Citizen_Details.class);
                                    startActivity(intent);
                                }
                            }else{
                                userRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("exists");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else{
                   // updateUI(currentUser);
            }

        }
        }


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.child("users").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        String Value=snapshot.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();
                                        if (Value.equals("verified")){
                                            Intent intent=new Intent(PhoneAuthorization.this,Wanted_List.class);
                                            intent.putExtra("access",0);
                                            startActivity(intent);
                                        }else{
                                            userRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("exists");
                                            Intent intent=new Intent(PhoneAuthorization.this,Citizen_Details.class);
                                            startActivity(intent);
                                        }
                                    }else{
                                        userRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("exists");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(PhoneAuthorization.this, "Congratulations signed up in successfully", Toast.LENGTH_SHORT).show();
                            SendUserToWantedActivity();

                        } else {
                            loadingBar.dismiss();
                            String e=task.getException().toString();
                            Toast.makeText(PhoneAuthorization.this, "Error : "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToWantedActivity(){

        Intent intent=new Intent(PhoneAuthorization.this, Wanted_List.class);
        intent.putExtra("access",0);
        startActivity(intent);
        finish();
    }
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Exit this app ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(PhoneAuthorization.this);

                        // finish();
                        // System.exit(0);
                        //MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

}
