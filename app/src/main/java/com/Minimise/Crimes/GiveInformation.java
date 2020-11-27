package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.EventListener;
import java.util.HashMap;

public class GiveInformation extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button btn_Add_Photo,btn_Save;
    ProgressDialog progressDialog;
    int GalleryPick=1;
    private Uri ImageUri;
    private StorageReference PostInformation;
    String comment;
    DatabaseReference databaseReference;
    private long count=3;
    String ID1;
    String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_information);

        Intent intent=getIntent();
        int ID=intent.getIntExtra("ID",1);
        String selector=intent.getStringExtra("selector");
        ID1=String.valueOf(ID);

        imageView=findViewById(R.id.Give_Information_ImageView);
        editText=findViewById(R.id.Give_Information_Comment);
        btn_Add_Photo=findViewById(R.id.Give_Information_Photo_Button);
        btn_Save=findViewById(R.id.Give_Information_Save);
        progressDialog=new ProgressDialog(this);

        //comment=editText.getText().toString().trim();

        if (selector.equals("Wanted")){
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Wanted People").child(ID1).child("Information");
            PostInformation= FirebaseStorage.getInstance().getReference().child("Wanted Images");
        }else{
            if (selector.equals("Complaint")){
                databaseReference= FirebaseDatabase.getInstance().getReference().child("complaint").child(ID1).child("Information");
                PostInformation= FirebaseStorage.getInstance().getReference().child("Complaint Images");
            }else {
                databaseReference= FirebaseDatabase.getInstance().getReference().child("Posts").child(ID1).child("Information");
                PostInformation= FirebaseStorage.getInstance().getReference().child("Post Images");
            }


        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count=snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_Add_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent=new Intent();
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                GalleryIntent.setType("image/*");
                startActivityForResult(GalleryIntent,GalleryPick);
            }
        });

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInformation();
            }
        });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(ImageUri);
        }
    }
    void uploadInformation(){
        if (editText.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please enter some Information", Toast.LENGTH_SHORT).show();
        }else{
            if (ImageUri==null){
                databaseReference.child(String.valueOf(count+1)).child("info_given").setValue(editText.getText().toString().trim());
                databaseReference.child(String.valueOf(count+1)).child("UserId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {

                                if (task.isSuccessful())
                                {

                                    Toast.makeText(GiveInformation.this, "Information Updated", Toast.LENGTH_SHORT).show();
                                    final Intent intent=new Intent(GiveInformation.this,Wanted_List.class);

                                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                    if(!firebaseUser.getProviderData().equals("password")){
                                        intent.putExtra("access",0);
                                    }else{
                                        intent.putExtra("access",1);
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2);


                                    progressDialog.dismiss();

                                }
                            }
                        });

            }else{
                progressDialog.setTitle("Saving Information");
                progressDialog.setMessage("Please Wait....");
                progressDialog.show();
                final StorageReference filePath=PostInformation.child(ID1).child(String.valueOf(count+1));
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

                            databaseReference.child(String.valueOf(count+1)).child("info_given").setValue(editText.getText().toString().trim());
                            databaseReference.child(String.valueOf(count+1)).child("UserId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            databaseReference.child(String.valueOf(count+1)).child("downloadUrl").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {

                                            if (task.isSuccessful())
                                            {

                                                Toast.makeText(GiveInformation.this, "Information Updated", Toast.LENGTH_SHORT).show();
                                               final Intent intent=new Intent(GiveInformation.this,Wanted_List.class);

                                                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                                if(!firebaseUser.getProviderData().equals("password")){
                                                    intent.putExtra("access",0);
                                                }else{
                                                    intent.putExtra("access",1);
                                                }

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 2);


                                                progressDialog.dismiss();

                                            }
                                        }
                                    });

                        }
                    }
                });

            }
        }
    }
}
