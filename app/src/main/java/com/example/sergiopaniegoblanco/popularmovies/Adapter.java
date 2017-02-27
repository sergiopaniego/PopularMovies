package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public Adapter(int numberOfItems,int screenWidth,JSONObject json,ListItemClickListener mOnClickListener) {
        mNumberItems = numberOfItems;
        this.screenWidth=screenWidth;
        this.mOnClickListener=mOnClickListener;
        this.json=json;
        viewHolderCount = 0;
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
        JSONArray jArray;
        String image="";
        try {
            jArray = json.getJSONArray("results");
            image=jArray.getJSONObject(position).get("poster_path").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException ex){
        }
        if(!image.equals(""))
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+image).error(R.drawable.fff).resize(screenWidth/2, 0).into(holder.listItemNumberView);
        else{

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

            listItemNumberView = (ImageView) itemView.findViewById(R.id.poster_item);
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
            System.out.println("CLICKED");
        }
    }

    interface ListItemClickListener{

        void onListItemClick(int clickedPosition);

    }

}