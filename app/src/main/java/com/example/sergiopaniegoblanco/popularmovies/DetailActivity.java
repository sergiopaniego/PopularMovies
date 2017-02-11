package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView imageDetail=(ImageView) findViewById(R.id.detailimage);
        String image="";
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        Intent intent=getIntent();
        JSONObject json;
        TextView tx=(TextView) findViewById(R.id.text);
        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            String text=intent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                json= new JSONObject(text);
                image=json.get("poster_path").toString();
                tx.setText(json.get("title").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Picasso.with(DetailActivity.this).load("http://image.tmdb.org/t/p/w185/"+image).error(R.drawable.fff).resize(screenWidth/2, 0).into(imageDetail);

    }
}
