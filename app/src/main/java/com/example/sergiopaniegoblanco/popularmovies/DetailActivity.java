package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class DetailActivity extends AppCompatActivity {

    private String id;
    final static String URL_BASE="http://api.themoviedb.org/3/movie/";
    final static String PARAM_QUERY = "api_key";
    //Here goes the API key
    final static String key="";
    private JSONObject jsonTrailer;
    private JSONObject jsonReview;

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
        TextView ratingtx=(TextView) findViewById(R.id.rating);
        TextView releasetx=(TextView) findViewById(R.id.release_date);
        TextView plottx=(TextView) findViewById(R.id.plot);

        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            String text=intent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                json= new JSONObject(text);
                ratingtx.setText(json.get("vote_average").toString()+"/10");
                releasetx.setText("RELEASE DATE: "+json.get("release_date").toString()+"\n");
                plottx.setText(json.get("overview").toString()+"\n");
                image=json.get("poster_path").toString();
                tx.setText(json.get("title").toString());
                id=json.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Uri builtUri = Uri.parse(URL_BASE).buildUpon().appendPath(id).appendPath("videos").appendQueryParameter(PARAM_QUERY,key)
                .build();
        Uri builtUri2 = Uri.parse(URL_BASE).buildUpon().appendPath(id).appendPath("reviews").appendQueryParameter(PARAM_QUERY,key)
                .build();
        URL url = null;
        String SearchResults = null;
        URL url2 = null;
        String SearchResults2 = null;

        if(!isOnline()){
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(R.string.errorMessage)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else{
            try {
                url = new URL(builtUri.toString());
                url2 = new URL(builtUri2.toString());
                new MoviesQueryTask2().execute(url,url2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Picasso.with(DetailActivity.this).load("http://image.tmdb.org/t/p/w185/"+image).error(R.drawable.fff).resize(screenWidth/2, 0).into(imageDetail);

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showTrailer(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        String videoKey= null;
        try {
            JSONArray jArray = jsonTrailer.getJSONArray("results");
            videoKey = jArray.getJSONObject(0).get("key").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Uri uri= Uri.parse("http://www.youtube.com/watch?v="+videoKey);
        intent.setData(uri);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }

    }
    public class MoviesQueryTask2 extends AsyncTask<URL, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl1 = params[0];
            URL searchUrl2 = params[1];
            String SearchResults1 = null;
            String SearchResults2 = null;
            try {
                SearchResults1 = NetworkUtils.getResponseFromHttpUrl(searchUrl1);
                jsonTrailer= new JSONObject(SearchResults1);
                SearchResults2 = NetworkUtils.getResponseFromHttpUrl(searchUrl2);
                jsonReview= new JSONObject(SearchResults2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            }
            return SearchResults1;
        }

        @Override
        protected void onPostExecute(String SearchResults) {
            Button tx=(Button)findViewById(R.id.trailer);
            TextView text=(TextView) findViewById(R.id.review);
            JSONArray jArray = null;
            try {
                int number=Integer.parseInt(jsonReview.get("total_results").toString());
                jArray = jsonReview.getJSONArray("results");
                for(int i=0;i<number;i++){
                    String review = jArray.getJSONObject(i).get("content").toString();
                    text.append("Review "+String.valueOf(i+1)+":\n");
                    text.append(review);
                    text.append("\n\n\n\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    public void favMovie(View view){
        Button tx=(Button) findViewById(R.id.favbtn);
        TextView text=(TextView)findViewById(R.id.fav);
        if(tx.getText().toString().equals(getString(R.string.fav))){
            tx.setText(R.string.unfav);
            text.setText(("Favourite Movie"));
        }else{
            tx.setText(R.string.fav);
            text.setText((""));
        }
    }
}
