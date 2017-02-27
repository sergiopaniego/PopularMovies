package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Adapter.ListItemClickListener{
    private RecyclerView mNumbersList;
    private static final int NUM_LIST_ITEMS=20;
    private Adapter mAdapter;
    private Toast toast;
    private ProgressBar mLoadingIndicator;
    private TextView errorMessage;
    private ImageView images;
    private JSONObject json;
    private int width;
    private int clickedPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            mNumbersList = (RecyclerView) findViewById(R.id.recycledview);
            GridLayoutManager gd=new GridLayoutManager(getApplicationContext(), 2);
            mNumbersList.setLayoutManager(gd);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            images= (ImageView) findViewById(R.id.poster_item);
            mNumbersList.setHasFixedSize(true);
            mLoadingIndicator=(ProgressBar)findViewById(R.id.pb_loading_indicator);
            URL moviesSearchUrl = NetworkUtils.buildUrl("popular");
            new MoviesQueryTask().execute(moviesSearchUrl);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if(menuItemThatWasSelected==R.id.action_search&&item.getTitle().equals(getString(R.string.top_rated))){
            URL moviesSearchUrl = NetworkUtils.buildUrl("top_rated");
            if(!isOnline()){
                new AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(R.string.errorMessage)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                new MoviesQueryTask().execute(moviesSearchUrl);
                Context context=MainActivity.this;
                String message=getString(R.string.top_rated);
                if(toast!=null){
                    toast.cancel();
                }
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
                item.setTitle(getString(R.string.most_popular));
            }
        }else{
            if(!isOnline()){
                new AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(R.string.errorMessage)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else {
                URL moviesSearchUrl = NetworkUtils.buildUrl("popular");
                new MoviesQueryTask().execute(moviesSearchUrl);
                Context context = MainActivity.this;
                String message = getString(R.string.most_popular);
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
                item.setTitle(getString(R.string.top_rated));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedPosition) {
        this.clickedPosition=clickedPosition;
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        try {
            JSONArray jArray = json.getJSONArray("results");
            System.out.println(clickedPosition);
            startChildActivityIntent.putExtra(Intent.EXTRA_TEXT,jArray.getJSONObject(clickedPosition).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(startChildActivityIntent);
    }


    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String SearchResults = null;
            try {
                SearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                json= new JSONObject(SearchResults);
                //System.out.println(json.get("page"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            }
            return SearchResults;
        }

        @Override
        protected void onPostExecute(String SearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAdapter = new Adapter(NUM_LIST_ITEMS,width,json,MainActivity.this);
            mNumbersList.setAdapter(mAdapter);
        }
    }
}
