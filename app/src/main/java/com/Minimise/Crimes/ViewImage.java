package com.Minimise.Crimes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {
    PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView=findViewById(R.id.View_Image_Fully);
        Intent intent=getIntent();
        String ImageUrl=intent.getStringExtra("imageurl");
        Picasso.get().load(ImageUrl).placeholder(R.drawable.profile_image).into(imageView);

    }
}
