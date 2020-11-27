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

public class CreateWantedPeople extends AppCompatActivity {

    Button SaveData;
    EditText Name,CrimeDivision,CrimeLocation,CrimeDescription;
    ImageView CriminalImage;
    WantedData wantedData;

    int GalleryPick=1;
    private Uri ImageUri;
    private StorageReference CrimeImageRef;
    private String downloadUrl;
    private DatabaseReference Crimeref;
    private ProgressDialog progressDialog;
    private String check_username="",check_user_bio="";
    private long count=3;

    final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    String UID=firebaseUser.getUid();
    /*
    Renamed things....--->

profileImgRef as CrimeImageRef
saveUserData as saveCrimeData
userRef as Crimeref
profileMap as CrimeMap

UserName as UploadCriminalName
UserStatus as UploadCrimeDivision

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wanted_people);

        SaveData=findViewById(R.id.SaveData);
        Name=findViewById(R.id.Create_Wanted_Name);
        CrimeDescription=findViewById(R.id.Create_Wanted_Description);
        CrimeLocation=findViewById(R.id.Create_Wanted_Location);
        CrimeDivision=findViewById(R.id.Create_Wanted_Crime_Division);
        CriminalImage=findViewById(R.id.Create_Wanted_Image_View);


        CrimeImageRef= FirebaseStorage.getInstance().getReference().child("Wanted Images");
        progressDialog=new ProgressDialog(this);

        Crimeref= FirebaseDatabase.getInstance().getReference().child("Wanted People");
        Crimeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count=snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CriminalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent=new Intent();
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                GalleryIntent.setType("image/*");
                startActivityForResult(GalleryIntent,GalleryPick);
            }
        });
        SaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveCrimeData();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            CriminalImage.setImageURI(ImageUri);
        }
    }


    private void saveCrimeData()
    {
        final String UploadCriminalName=Name.getText().toString().trim();
        final String UploadCrimeDivision=CrimeDivision.getText().toString().trim();
        final String UploadCrimeLocation=CrimeLocation.getText().toString().trim();
        final String UploadCrimeDescription=CrimeDescription.getText().toString().trim();



        if (ImageUri==null)
        {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();

        }
        else if (UploadCriminalName.equals("") || UploadCrimeDivision.equals("") || UploadCrimeLocation.equals("") || UploadCrimeDescription.equals("") )
        {
            Toast.makeText(this, "Please fill in ALL the fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setTitle("Crime Settings");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            final StorageReference filePath=CrimeImageRef.child(String.valueOf(count+1));
            final UploadTask uploadTask=filePath.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(CreateWantedPeople.this, "Error occurred", Toast.LENGTH_SHORT).show();

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


                        wantedData=new WantedData(UploadCriminalName,UploadCrimeDivision,UploadCrimeLocation,downloadUrl,UploadCrimeDescription,UID);



                        /*HashMap<String,Object> CrimeMap=new HashMap<>();
                        CrimeMap.put("Criminal Name",UploadCriminalName);
                        CrimeMap.put("Crime Division",UploadCrimeDivision);
                        CrimeMap.put("Crime Location",UploadCrimeLocation);
                        CrimeMap.put("Crime Description",UploadCrimeDescription);
                        CrimeMap.put("UID",UID);

                        CrimeMap.put("image",downloadUrl);*/

                        Crimeref.child(String.valueOf(count+1)).setValue(wantedData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {

                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(CreateWantedPeople.this, "Criminal Data updated..", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(CreateWantedPeople.this,Wanted_List.class);
                                            intent.putExtra("access",1);
                                            startActivity(intent);


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
