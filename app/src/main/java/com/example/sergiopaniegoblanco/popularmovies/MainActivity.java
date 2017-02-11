package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mNumbersList;
    private static final int NUM_LIST_ITEMS=20;
    private Adapter mAdapter;
    Toast toast;
    ProgressBar mLoadingIndicator;
    TextView errorMessage;
    ImageView images;
    JSONObject json;
    int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumbersList = (RecyclerView) findViewById(R.id.recycledview);
        GridLayoutManager gd=new GridLayoutManager(getApplicationContext(), 2);
        mNumbersList.setLayoutManager(gd);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        images= (ImageView) findViewById(R.id.poster_item);
        //errorMessage=(TextView) findViewById(R.id.error_message_display);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */


        mLoadingIndicator=(ProgressBar)findViewById(R.id.pb_loading_indicator);
        URL moviesSearchUrl = NetworkUtils.buildUrl("popular");
        new MoviesQueryTask().execute(moviesSearchUrl);


    }
    /*private void showJsonDataView(){
        errorMessage.setVisibility(View.INVISIBLE);
        images.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
        images.setVisibility(View.INVISIBLE);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if(menuItemThatWasSelected==R.id.action_search&&item.getTitle()=="TOP RATED"){
            URL moviesSearchUrl = NetworkUtils.buildUrl("top_rated");
            new MoviesQueryTask().execute(moviesSearchUrl);
            Context context=MainActivity.this;
            String message="Most popular";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            item.setTitle("MOST POPULAR");
        }else{
            URL moviesSearchUrl = NetworkUtils.buildUrl("popular");
            new MoviesQueryTask().execute(moviesSearchUrl);
            Context context=MainActivity.this;
            String message="Top rated";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            item.setTitle("TOP RATED");
        }

        return super.onOptionsItemSelected(item);
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
                System.out.println(json.get("page"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            }
            return SearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAdapter = new Adapter(NUM_LIST_ITEMS,width,json);
            mNumbersList.setAdapter(mAdapter);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
               // showJsonDataView();
            }else{
                //showErrorMessage();
            }
        }
    }
}
