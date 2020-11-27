package com.Minimise.Crimes;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WarningFragment extends Fragment {


    public WarningFragment() {
        // Required empty public constructor
    }

    GridView listView;
    WarningFragment.MyAdapter myAdapter;
    DatabaseReference mRef;
    ArrayList<String> Crime_Desc;
    ArrayList<String> Crime_Div;
    ArrayList<String> Crime_Loc;
    ArrayList<String> Crime_Name;
    ArrayList<String> Crime_Image;
    ArrayList<Integer> Ids_of_Data;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_warning, container, false);

        Crime_Desc=new ArrayList<>();
        Crime_Div=new ArrayList<>();
        Crime_Loc=new ArrayList<>();
        Crime_Name=new ArrayList<>();
        Crime_Image=new ArrayList<>();
        Ids_of_Data=new ArrayList<>();
        listView=v.findViewById(R.id.Fragment_list_view_warning);

        mRef= FirebaseDatabase.getInstance().getReference("Posts");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count= (int) snapshot.getChildrenCount();
                for (int i = 1; i <=count+5 ; i++) {
                    if (snapshot.hasChild(String.valueOf(i))){

                        Crime_Desc.add(snapshot.child(String.valueOf(i)).child("description").getValue().toString());
                        Crime_Div.add(snapshot.child(String.valueOf(i)).child("division").getValue().toString());
                        Crime_Loc.add(snapshot.child(String.valueOf(i)).child("location").getValue().toString());
                        Crime_Name.add(snapshot.child(String.valueOf(i)).child("name").getValue().toString());
                        Crime_Image.add(snapshot.child(String.valueOf(i)).child("image").getValue().toString());
                        //Ids_of_Data.add(i);
                        Ids_of_Data.add(Integer.valueOf(i));
                        //Toast.makeText(getContext(), "ADDING ROWS", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                String[] names=Crime_Name.toArray(new String[Crime_Name.size()]);
                String[] div=Crime_Div.toArray(new String[Crime_Div.size()]);
                String[] loc=Crime_Loc.toArray(new String[Crime_Loc.size()]);
                String[] desc=Crime_Desc.toArray(new String[Crime_Desc.size()]);
                String[] images=Crime_Image.toArray(new String[Crime_Image.size()]);
                int[] ret = new int[Ids_of_Data.size()];
                for (int i=0; i < ret.length; i++)
                {
                    ret[i] = Ids_of_Data.get(i).intValue();
                }

                myAdapter= new WarningFragment.MyAdapter(getActivity(),names,div,loc,images,desc,ret);
                listView.setAdapter(myAdapter);
            }
        }, 1000);



        return v;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String list_names[];
        String list_division[];
        String list_location[];
        String Images_values[];
        String desc[];
        int ret[];

        public MyAdapter(Context context,String names[],String divisions[],String locations[],String ImageValue[] ,String desc[],int ret[]) {
            super(context, R.layout.post_element,R.id.post_element_name,names);
            this.context=context;
            this.list_names=names;
            this.list_division=divisions;
            this.list_location=locations;
            this.Images_values= ImageValue;
            this.desc=desc;
            this.ret=ret;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate(R.layout.post_element,parent,false);

            ImageView imageView=row.findViewById(R.id.post_element_image);
            TextView tv_name=row.findViewById(R.id.post_element_name);
            TextView tv_division=row.findViewById(R.id.post_element_division);
            TextView tv_location=row.findViewById(R.id.post_element_location);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,ViewArticle.class);
                    intent.putExtra("ID",ret[position]);
                    intent.putExtra("ImageUrl",Images_values[position]);
                    intent.putExtra("name",list_names[position]);
                    intent.putExtra("division",list_division[position]);
                    intent.putExtra("location",list_location[position]);
                    intent.putExtra("description",desc[position]);
                    intent.putExtra("selector","Warning");
                    startActivity(intent);
                }
            });
            tv_name.setText(list_names[position]);
            tv_division.setText(list_division[position]);
            tv_location.setText(list_location[position]);

            Picasso.get().load(Images_values[position]).placeholder(R.drawable.profile_image).into(imageView);

            return row;
        }
    }

}
