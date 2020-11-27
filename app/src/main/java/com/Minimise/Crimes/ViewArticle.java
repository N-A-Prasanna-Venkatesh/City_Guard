package com.Minimise.Crimes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ViewArticle extends AppCompatActivity {

    ImageView imageView;
    TextView textView1,textView2,textView3,textView4;
    Button btn,btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);
        Intent intent=getIntent();
        String Description=intent.getStringExtra("description");
        final int ID=intent.getIntExtra("ID",1);
        final String ImageUrl=intent.getStringExtra("ImageUrl");
        String name=intent.getStringExtra("name");
        String division=intent.getStringExtra("division");
        String location=intent.getStringExtra("location");
        final String selector=intent.getStringExtra("selector");

        textView1=findViewById(R.id.View_Article_Name);
        textView2=findViewById(R.id.View_Article_Division);
        textView3=findViewById(R.id.View_Article_Location);
        textView4=findViewById(R.id.View_Article_Description);
        btn=findViewById(R.id.View_Article_Comment);
        imageView=findViewById(R.id.View_Article_Image_view);
        btn2=findViewById(R.id.View_Article_View_All_Comment);

        textView1.setText(name);
        textView2.setText(division);
        textView3.setText(location);
        textView4.setText(Description);
        Picasso.get().load(ImageUrl).placeholder(R.drawable.profile_image).into(imageView);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ViewArticle.this,ViewInfo.class);
                intent.putExtra("ID",ID);
                intent.putExtra("selector",selector);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ViewArticle.this,GiveInformation.class);
                intent.putExtra("ID",ID);
                intent.putExtra("selector",selector);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(ViewArticle.this,ViewImage.class);
                intent1.putExtra("imageurl",ImageUrl);
                startActivity(intent1);
            }
        });

    }
}
