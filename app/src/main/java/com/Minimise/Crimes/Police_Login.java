package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Police_Login extends AppCompatActivity {

    EditText Username,Password;
    Button Verify,signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police__login);

        Verify=findViewById(R.id.Verify);
        Username=findViewById(R.id.Username);
        Password=findViewById(R.id.Password);
        signup=findViewById(R.id.SignUp);

        mAuth = FirebaseAuth.getInstance();

        String email=Username.getText().toString();
        String password=Password.getText().toString();
        

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Police_Login.this,Police_SignUp.class);
                startActivity(intent);
            }
        });
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Username.getText().toString();
                String password=Password.getText().toString();
                Check_Login(email,password);
                
            }
        });

    }

   /* @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            // Toast.makeText(this, firebaseUser.toString(), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Police_Login.this, Wanted_List.class);
            startActivity(intent);
            finish();
        }


    }*/
   
   void Check_Login(String email,String password){
       mAuth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                           //Log.d(TAG, "signInWithEmail:success");
                           //FirebaseUser user = mAuth.getCurrentUser();
                           Toast.makeText(Police_Login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                           Intent intent =new Intent(Police_Login.this,Wanted_List.class);
                           intent.putExtra("access",1);
                           startActivity(intent);
                           //updateUI(user);
                       } else {
                           // If sign in fails, display a message to the user.
                           //Log.w(TAG, "signInWithEmail:failure", task.getException());
                           Toast.makeText(Police_Login.this, "Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                           //updateUI(null);
                           // ...
                       }

                       // ...
                   }
               });
   }
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Exit this app ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(Police_Login.this);

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
