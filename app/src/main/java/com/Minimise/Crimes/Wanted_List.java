package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Wanted_List extends AppCompatActivity {

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button Create_Post,AddWanted;
    int access;
    Fragment fragment1,fragment2,fragment3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted__list);

        Create_Post=findViewById(R.id.Create_Post);
        AddWanted=findViewById(R.id.AddWanted);

        Intent intent=getIntent();
        access=(int)intent.getExtras().get("access");
        if(access==0){
            Create_Post.setVisibility(View.INVISIBLE);
            AddWanted.setVisibility(View.INVISIBLE);
        }
        AddWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Wanted_List.this,CreateWantedPeople.class);
                startActivity(intent);
            }
        });
        Create_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Wanted_List.this,CreatePost.class);
                startActivity(intent);
            }
        });

        fragment1=new WantedFragment();
        fragment2=new ComplaintFragment();
        fragment3=new WarningFragment();


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new WarningFragment()).commit();

        }
        private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment selectedFragment=null;

                        switch (menuItem.getItemId()){
                            case R.id.Warning_Alerts:
                                selectedFragment= fragment3;
                                break;
                            case R.id.WantedList:
                                selectedFragment=fragment1;
                                break;
                            case  R.id.Complaint:
                                selectedFragment=fragment2;
                                break;

                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                        return true;
                    }
                };


    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Exit this app ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(Wanted_List.this);

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
