package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergiopaniegoblanco.popularmovies.data.FavListContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by sergiopaniegoblanco on 10/02/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.PosterViewHolder>{
    private int mNumberItems;
    private Context context;
    private int screenWidth;
    private JSONObject json;
    private final ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private Cursor cursor;
    private int columns;

    public Adapter(int numberOfItems,int screenWidth,JSONObject json,ListItemClickListener mOnClickListener,int columns) {
        mNumberItems = numberOfItems;
        this.screenWidth=screenWidth;
        this.mOnClickListener=mOnClickListener;
        this.json=json;
        viewHolderCount = 0;
        this.columns=columns;
    }
    public Adapter(int numberOfItems, int screenWidth, Cursor cursor, ListItemClickListener mOnClickListener,int columns) {
        mNumberItems = numberOfItems;
        this.screenWidth=screenWidth;
        this.mOnClickListener=mOnClickListener;
        this.cursor=cursor;
        viewHolderCount = 0;
        this.columns=columns;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        int layoutIdForListItem = R.layout.poster_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PosterViewHolder viewHolder = new PosterViewHolder(view);
        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        String image="";
        if(cursor!=null){
            cursor.moveToPosition(position);
            image=cursor.getString(cursor.getColumnIndex(FavListContract.FavlistEntry.COLUMN_MOVIE_POSTER));
            Picasso.with(context).load(image).error(R.drawable.fff).resize(screenWidth/columns, 0).into(holder.listItemNumberView);
        }else{
            JSONArray jArray;
            try {
                jArray = json.getJSONArray("results");
                image=jArray.getJSONObject(position).get("poster_path").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException ex){
            }
            if(!image.equals(""))
                Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+image).error(R.drawable.fff).resize(screenWidth/columns, 0).into(holder.listItemNumberView);
            else{

            }
        }
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    /**
     * Cache of the children views for a list item.
     */
    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        ImageView listItemNumberView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link Adapter#onCreateViewHolder(ViewGroup, int)}
         */
        public PosterViewHolder(View itemView) {
            super(itemView);

            listItemNumberView = ButterKnife.findById(itemView,R.id.poster_item);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(Drawable listIndex) {
            listItemNumberView.setImageDrawable(listIndex);
        }
        @Override
        public void onClick(View view) {
            int clickedPosition=getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    interface ListItemClickListener{

        void onListItemClick(int clickedPosition);

    }

}