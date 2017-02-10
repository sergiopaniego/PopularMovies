package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mNumbersList;
    private static final int NUM_LIST_ITEMS=100;
    private Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumbersList = (RecyclerView) findViewById(R.id.recycledview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new Adapter(NUM_LIST_ITEMS);

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
        if(menuItemThatWasSelected==R.id.action_search&&item.getTitle()=="SORT BY TITLE"){
            Context context=MainActivity.this;
            String message="Item clicked";
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

            item.setTitle("SORT BY RELEVANCE");
        }else{
            item.setTitle("SORT BY TITLE");
            Context context=MainActivity.this;
            String message="Item clicked";
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
