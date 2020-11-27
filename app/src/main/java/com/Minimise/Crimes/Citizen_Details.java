package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Citizen_Details extends AppCompatActivity {

    Button saveBtn;
    EditText userNameET,userBioET;
    ImageView profileImageView;
    int GalleryPick=1;
    private Uri ImageUri;
    private StorageReference profileImgRef;
    private String downloadUrl;
    private DatabaseReference userRef,userRef1;
    private ProgressDialog progressDialog;
    private String check_username="",check_user_bio="";
    TextToSpeech textToSpeech;
    private int ct=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen__details);


        saveBtn=findViewById(R.id.save_settings);
        userBioET=findViewById(R.id.bio_settings);
        userNameET=findViewById(R.id.username_settings);
        profileImageView=findViewById(R.id.settings_profile_image);

        profileImgRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        progressDialog=new ProgressDialog(this);

        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userRef1=FirebaseDatabase.getInstance().getReference().child("users");


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent=new Intent();
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                GalleryIntent.setType("image/*");
                startActivityForResult(GalleryIntent,GalleryPick);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    saveUserData();

            }
        });

        retriveData();



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }
    private void saveUserData()
    {
        final String UserName=userNameET.getText().toString().trim();
        final String UserStatus=userBioET.getText().toString().trim();



        if (ImageUri==null)
        {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image"))
                    {
                        SaveOnlyDataWithoutPhoto();
                    }
                    else
                    {
                        Toast.makeText(Citizen_Details.this, "Please Select an Image first", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (UserName.equals(""))
        {
            Toast.makeText(this, "Please enter you name", Toast.LENGTH_SHORT).show();
            userNameET.setError("Do not leave name empty.");
        }
        else if (UserStatus.equals(""))
        {
            Toast.makeText(this, "Please fill in your status", Toast.LENGTH_SHORT).show();
            userBioET.setError("Do not leave your Status empty.");

        }
        else
        {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            final StorageReference filePath=profileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask=filePath.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();

                    }
                    downloadUrl=filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        downloadUrl=task.getResult().toString();

                        HashMap<String,Object> profileMap=new HashMap<>();
                        profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name",UserName);
                        profileMap.put("status",UserStatus);
                        profileMap.put("image",downloadUrl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {

                                        if (task.isSuccessful())
                                        {

                                            userRef1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("verified");
                                            Intent intent=new Intent(Citizen_Details.this,Wanted_List.class);
                                            intent.putExtra("access",0);
                                            startActivity(intent);
                                            finish();

                                            progressDialog.dismiss();

                                            Toast.makeText(Citizen_Details.this, "Profile Settings has been updated..", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            });
        }


    }

    private void SaveOnlyDataWithoutPhoto()
    {
        final String UserName=userNameET.getText().toString().trim();
        final String UserStatus=userBioET.getText().toString().trim();

        if (UserName.equals(""))
        {
            Toast.makeText(this, "Please enter you name", Toast.LENGTH_SHORT).show();
            userNameET.setError("Do not leave name empty.");
        }
        else if (UserStatus.equals(""))
        {
            Toast.makeText(this, "Please fill in your status", Toast.LENGTH_SHORT).show();
            userBioET.setError("Do not leave your Status empty.");

        }
        else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name", UserName);
            profileMap.put("status", UserStatus);
            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if (task.isSuccessful())
                            {
                                userRef1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("verified");
                                Intent intent=new Intent(Citizen_Details.this,Wanted_List.class);
                                intent.putExtra("access",0);
                                startActivity(intent);
                                finish();

                                progressDialog.dismiss();
                                Toast.makeText(Citizen_Details.this, "Profile Settings has been updated..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void retriveData()
    {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    String imageDB= dataSnapshot.child("image").getValue().toString();
                    String nameDB= dataSnapshot.child("name").getValue().toString();
                    String statusDB= dataSnapshot.child("status").getValue().toString();

                    userBioET.setText(statusDB);
                    userNameET.setText(nameDB);

                    Picasso.get().load(imageDB).placeholder(R.drawable.ic_username).into(profileImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        ActivityCompat.finishAffinity(Citizen_Details.this);

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
