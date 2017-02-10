package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mNumbersList;
    private static final int NUM_LIST_ITEMS=100;
    private Adapter mAdapter;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumbersList = (RecyclerView) findViewById(R.id.recycledview);
        mNumbersList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new Adapter(NUM_LIST_ITEMS,width);

        mNumbersList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if(menuItemThatWasSelected==R.id.action_search&&item.getTitle()=="TOP RATED"){
            Context context=MainActivity.this;
            String message="Most popular";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            item.setTitle("MOST POPULAR");
        }else{
            Context context=MainActivity.this;
            String message="Top rated";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            item.setTitle("TOP RATED");
        }

        return super.onOptionsItemSelected(item);
    }
}
