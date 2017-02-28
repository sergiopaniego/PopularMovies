package com.example.sergiopaniegoblanco.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergiopaniegoblanco.popularmovies.data.FavListContract;
import com.example.sergiopaniegoblanco.popularmovies.data.FavListDBHelper;
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

import static android.R.attr.name;

public class DetailActivity extends AppCompatActivity {

    private String id;
    private final static String URL_BASE="http://api.themoviedb.org/3/movie/";
    private final static String URL_BASE_YOUTUBE="https://img.youtube.com/vi/";
    private final static String PARAM_QUERY = "api_key";
    //Here goes the API key
    private JSONObject jsonTrailer;
    private JSONObject jsonReview;
    private Display display;
    private Point size;
    private int screenWidth;
    private boolean fav=false;
    private SQLiteDatabase mDb;
    private String movieName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView imageDetail=(ImageView) findViewById(R.id.detailimage);
        String image="";
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
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
                ratingtx.setText(json.get("vote_average").toString());
                releasetx.setText(getString(R.string.release)+" "+json.get("release_date").toString()+"\n");
                plottx.setText(json.get("overview").toString()+"\n");
                image=json.get("poster_path").toString();
                movieName=json.get("title").toString();
                tx.setText(json.get("title").toString());
                id=json.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Uri builtUri = Uri.parse(URL_BASE).buildUpon().appendPath(id).appendPath("videos").appendQueryParameter(PARAM_QUERY,getString(R.string.api_key))
                .build();
        Uri builtUri2 = Uri.parse(URL_BASE).buildUpon().appendPath(id).appendPath("reviews").appendQueryParameter(PARAM_QUERY,getString(R.string.api_key))
                .build();

        URL url = null;
        String SearchResults = null;
        URL url2 = null;
        String SearchResults2 = null;

        if(!isOnline()){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
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
                new MoviesQueryTask().execute(url,url2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Picasso.with(DetailActivity.this).load("http://image.tmdb.org/t/p/w185/"+image).error(R.drawable.fff).resize(screenWidth/2, 0).into(imageDetail);
        //Action bar show film name
        setTitle(null);
        FavListDBHelper dbHelper = new FavListDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        // provide compatibility to all the versions
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showTrailer1(View view){
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
    public void showTrailer2(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        String videoKey= null;
        try {
            JSONArray jArray = jsonTrailer.getJSONArray("results");
            videoKey = jArray.getJSONObject(1).get("key").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Uri uri= Uri.parse("http://www.youtube.com/watch?v="+videoKey);
        intent.setData(uri);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }

    }
    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {


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
            String videoKey1= null;
            String videoKey2= null;
            try {
                jArray = jsonTrailer.getJSONArray("results");
                videoKey1 = jArray.getJSONObject(0).get("key").toString();
                videoKey2 = jArray.getJSONObject(1).get("key").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(videoKey2!=null){
                ImageButton trailerImage=(ImageButton)findViewById(R.id.trailerImage1);
                Double d = screenWidth/2.1;
                int i = d.intValue();
                Picasso.with(DetailActivity.this).load(URL_BASE_YOUTUBE+videoKey1+"/0.jpg").error(R.drawable.fff).resize(i, 0).into(trailerImage);
                trailerImage=(ImageButton)findViewById(R.id.trailerImage2);
                Picasso.with(DetailActivity.this).load(URL_BASE_YOUTUBE+videoKey2+"/0.jpg").error(R.drawable.fff).resize(i, 0).into(trailerImage);
            }else{
                ImageButton trailerImage=(ImageButton)findViewById(R.id.trailerImage1);
                Picasso.with(DetailActivity.this).load(URL_BASE_YOUTUBE+videoKey1+"/0.jpg").error(R.drawable.fff).resize(screenWidth/2, 0).into(trailerImage);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if(menuItemThatWasSelected==R.id.starred){
            if(fav){
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                mDb.delete(FavListContract.FavlistEntry.TABLE_NAME, FavListContract.FavlistEntry._ID+"="+id,null);
                fav=false;
            }else{
                ContentValues cv=new ContentValues();
                cv.put(FavListContract.FavlistEntry.COLUMN_MOVIE_NAME,movieName);
                mDb.insert(FavListContract.FavlistEntry.TABLE_NAME,null,cv);
                item.setIcon(R.drawable.ic_favorite_white_24dp);
                fav=true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
