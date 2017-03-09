package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergiopaniegoblanco.popularmovies.data.FavListContract;
import com.example.sergiopaniegoblanco.popularmovies.data.FavListDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Adapter.ListItemClickListener{
    private static final int NUM_LIST_ITEMS=20;
    private Adapter mAdapter;
    private Toast toast;
    private TextView errorMessage;
    private JSONObject json;
    private int width;
    private int clickedPosition;
    private boolean fav=false;
    private Cursor cursor;
    private Menu menu;
    private String previuosMenuOption;
    private GridLayoutManager gd;
    private int currentScrollPosition;

    private static final String MENU_TEXT_KEY = "menu";
    private static final String CURRENT_SCROLL_POSITION = "scroll_position";

    @BindView(R.id.recycledview) RecyclerView mNumbersList;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindString(R.string.top_rated) String menuOptionSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
            gd = new GridLayoutManager(getApplicationContext(), calculateNoOfColumns(getApplicationContext()));
            mNumbersList.setLayoutManager(gd);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            //images = ButterKnife.findById(this, R.id.poster_item);
            mNumbersList.setHasFixedSize(true);
            if (savedInstanceState != null) {
                currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);
                if (savedInstanceState.containsKey(MENU_TEXT_KEY)) {
                    previuosMenuOption = savedInstanceState.getString(MENU_TEXT_KEY);
                    System.out.println(previuosMenuOption);
                    if(previuosMenuOption.equals(getString(R.string.fav))){
                        menuTopRated(null);
                    }else if(previuosMenuOption.equals(getString(R.string.most_popular))){
                        menuFavMovies(null);
                    }else{
                        URL moviesSearchUrl = NetworkUtils.buildUrl("popular",getString(R.string.api_key));
                        new MoviesQueryTask().execute(moviesSearchUrl);
                    }
                    menuOptionSelected=previuosMenuOption;
                }
            }else{
                URL moviesSearchUrl = NetworkUtils.buildUrl("popular",getString(R.string.api_key));
                new MoviesQueryTask().execute(moviesSearchUrl);
            }
        }


    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(menuOptionSelected.equals(getString(R.string.most_popular))){
            menuFavMovies(null);
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
        MenuItem item = menu.findItem(R.id.action_search);
        if(previuosMenuOption!=null)
            item.setTitle(previuosMenuOption);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if(menuItemThatWasSelected==R.id.action_search&&item.getTitle().equals(getString(R.string.top_rated))){
            menuTopRated(item);
        }else if(menuItemThatWasSelected==R.id.action_search&&item.getTitle().equals(getString(R.string.most_popular))){
            menuMostPopular(item);
        }else{
            menuFavMovies(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedPosition) {
        this.clickedPosition=clickedPosition;
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        if(fav){
            cursor.moveToPosition(clickedPosition);
            startChildActivityIntent.putExtra(Intent.EXTRA_TEXT,cursor.getString(cursor.getColumnIndex(FavListContract.FavlistEntry.COLUMN_MOVIE_JSON)));
        }else{
            try {
                JSONArray jArray = json.getJSONArray("results");
                System.out.println(clickedPosition);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT,jArray.getJSONObject(clickedPosition).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            mAdapter = new Adapter(NUM_LIST_ITEMS,width,json,MainActivity.this,calculateNoOfColumns(getApplicationContext()));
            mNumbersList.setAdapter(mAdapter);
            System.out.println(currentScrollPosition);
            mNumbersList.smoothScrollToPosition(currentScrollPosition);
        }
    }
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MENU_TEXT_KEY, menuOptionSelected);
        outState.putInt(CURRENT_SCROLL_POSITION, gd.findFirstVisibleItemPosition());
    }

    public void menuTopRated(@Nullable MenuItem item){
        URL moviesSearchUrl = NetworkUtils.buildUrl("top_rated",getString(R.string.api_key));
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
            menuOptionSelected=getString(R.string.fav);
            new MoviesQueryTask().execute(moviesSearchUrl);
            Context context=MainActivity.this;
            String message=getString(R.string.top_rated);
            if(toast!=null){
                toast.cancel();
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            if(item!=null){
                item.setTitle(getString(R.string.fav));
            }
            fav=false;

        }
    }

    public void menuMostPopular(MenuItem item){
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
            menuOptionSelected=getString(R.string.top_rated);
            URL moviesSearchUrl = NetworkUtils.buildUrl("popular",getString(R.string.api_key));
            new MoviesQueryTask().execute(moviesSearchUrl);
            Context context = MainActivity.this;
            String message = getString(R.string.most_popular);
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            item.setTitle(getString(R.string.top_rated));
            fav=false;
        }
    }
    public void menuFavMovies(@Nullable MenuItem item){
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
            menuOptionSelected=getString(R.string.most_popular);
            cursor=getContentResolver().query(FavListContract.FavlistEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavListContract.FavlistEntry.COLUMN_MOVIE_NAME);
            mAdapter = new Adapter(cursor.getCount(),width,cursor,MainActivity.this,calculateNoOfColumns(getApplicationContext()));
            mNumbersList.setAdapter(mAdapter);
            Context context = MainActivity.this;
            String message = getString(R.string.fav);
            if (toast != null) {
                toast.cancel();
            }
            fav=true;
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            if(item!=null){
                item.setTitle(getString(R.string.most_popular));
            }
            fav=true;
        }
    }
}
