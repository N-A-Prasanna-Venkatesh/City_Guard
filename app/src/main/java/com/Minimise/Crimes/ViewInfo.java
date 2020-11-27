package com.Minimise.Crimes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class ViewInfo extends AppCompatActivity {

    ListView listView;
    TextView textView;
    ArrayList<String> uid,message,imgurl;
    String ID1;
    MyAdapter myAdapter;
    DatabaseReference databaseReference;
    long count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);

        Intent intent=getIntent();
        int ID=intent.getIntExtra("ID",1);
        String selector=intent.getStringExtra("selector");
        ID1=String.valueOf(ID);

        textView=findViewById(R.id.ViewInfo_Tv);
        listView=findViewById(R.id.ViewInfo_List_View);
        uid=new ArrayList<>();
        message=new ArrayList<>();
        imgurl=new ArrayList<>();

        if (selector.equals("Wanted")){
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Wanted People").child(ID1);
        }else{
            if (selector.equals("Complaint")){
                databaseReference = FirebaseDatabase.getInstance().getReference().child("complaint").child(ID1);
            }else {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(ID1);
            }
        }
        //Toast.makeText(this, ID1, Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Information")){
                    textView.setVisibility(View.INVISIBLE);
                     count = snapshot.child("Information").getChildrenCount();
                    for ( int i = 1; i <=count ; i++) {
                        if (snapshot.child("Information").child(String.valueOf(i)).exists()) {
                            uid.add(snapshot.child("Information").child(String.valueOf(i)).child("UserId").getValue().toString());
                            message.add(snapshot.child("Information").child(String.valueOf(i)).child("info_given").getValue().toString());

                            if (snapshot.child("Information").child(String.valueOf(i)).hasChild("downloadUrl")) {
                                imgurl.add(snapshot.child("Information").child(String.valueOf(i)).child("downloadUrl").getValue().toString());
                            } else {
                                imgurl.add("ImageNotThere");
                            }
                        }

                    }

                }else{

                    listView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.reverse(uid);
                Collections.reverse(imgurl);
                Collections.reverse(message);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String uid1[]=uid.toArray(new String[uid.size()]);
                String message1[]=message.toArray(new String[message.size()]);
                String imgurl1[]=imgurl.toArray(new String[imgurl.size()]);


                myAdapter=new MyAdapter(getApplicationContext(),uid1,message1,imgurl1);
                listView.setAdapter(myAdapter);

            }
        }, 1000);





    }


    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String Uid[],message[],imgurl[];


        public MyAdapter(Context context,String Uid[],String message[],String imgurl[]) {
            super(context, R.layout.viewinformation_list_element,R.id.list_element_view_information_text_view,Uid);
            this.context=context;
            this.Uid=Uid;
            this.message=message;
            this.imgurl=imgurl;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate(R.layout.viewinformation_list_element,parent,false);
            TextView tv=row.findViewById(R.id.list_element_view_information_text_view);
            ImageView imageView=row.findViewById(R.id.list_element_view_information_image_view);
            if (imgurl[position].equals("ImageNotThere")){
                imageView.setVisibility(View.GONE);
                tv.setText(message[position]);
            }else{
                tv.setText(message[position]);
                Picasso.get().load(imgurl[position]).placeholder(R.drawable.profile_image).into(imageView);

            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ViewInfo.this,ViewImage.class);
                    intent.putExtra("imageurl",imgurl[position]);
                    startActivity(intent);

                }
            });




           // Picasso.get().load(Images_values[position]).placeholder(R.drawable.profile_image).into(imageView);

            return row;
        }
    }
}
