package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.HashMap;

public class Police_SignUp extends AppCompatActivity {

    ImageView photo_id;
    Button Save;
    EditText Police_name,Police_designation,Police_id,Police_number,Police_mail,Police_password,Police_repassword;
    int GalleryPick=1;
    private Uri ImageUri;
    private StorageReference profileImgRef;
    private String downloadUrl;
    private DatabaseReference userRef;
    private long count=3;
    // Police Sign Up
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police__sign_up);

        photo_id=findViewById(R.id.Police_Photo_id);
        Save=findViewById(R.id.Save_Police);
        Police_designation=findViewById(R.id.Police_designation);
        Police_name=findViewById(R.id.Police_name);
        Police_id=findViewById(R.id.Police_id_number);
        Police_number=findViewById(R.id.Police_number);
        Police_mail=findViewById(R.id.Police_Username);
        Police_password=findViewById(R.id.Police_password);
        Police_repassword=findViewById(R.id.Police_password_retype);

        profileImgRef= FirebaseStorage.getInstance().getReference().child("Police Sign Up");
        progressDialog=new ProgressDialog(this);

        userRef= FirebaseDatabase.getInstance().getReference().child("PoliceSignUp");


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count=snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        photo_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent=new Intent();
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                GalleryIntent.setType("image/*");
                startActivityForResult(GalleryIntent,GalleryPick);
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveUserData();

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            photo_id.setImageURI(ImageUri);
        }
    }

    private void saveUserData()
    {
        final String UserName=Police_name.getText().toString().trim();
        final String UserStatus=Police_designation.getText().toString().trim();
        final String number=Police_number.getText().toString().trim();
        final String id=Police_id.getText().toString().trim();
        final String pass=Police_password.getText().toString().trim();
        final String re_pass=Police_repassword.getText().toString().trim();
        final String mail= Police_mail.getText().toString().trim();


        //Toast.makeText(this, String.valueOf(count), Toast.LENGTH_SHORT).show();

        if (ImageUri==null)
        {
            Toast.makeText(this, "Please add you ID's Photo", Toast.LENGTH_LONG).show();

        }
        else if(!pass.equals(re_pass)){
            Toast.makeText(this, "Please enter Password Carefully", Toast.LENGTH_SHORT).show();
        }
        else if (UserName.equals("") || UserStatus.equals("") || number.equals("") || id.equals("") || pass.equals("") || re_pass.equals("") ||mail.equals(""))
        {
            Toast.makeText(this, "Please enter all the information", Toast.LENGTH_LONG).show();

        }
        else
        {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            final StorageReference filePath=profileImgRef.child(String.valueOf(count+1));
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
                        profileMap.put("name",UserName);
                        profileMap.put("designation",UserStatus);
                        profileMap.put("image",downloadUrl);
                        profileMap.put("number",number);
                        profileMap.put("id",id);
                        profileMap.put("pass",pass);
                        profileMap.put("mail",mail);


                        userRef.child(String.valueOf(count+1)).updateChildren(profileMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {

                                        if (task.isSuccessful())
                                        {
                                            
                                            progressDialog.dismiss();

                                            Toast.makeText(Police_SignUp.this, "Your login has been sent up to the verification center.", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(Police_SignUp.this, "SignUp failed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                    }else{
                        Toast.makeText(Police_SignUp.this, "SignUp failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }


    }

}
