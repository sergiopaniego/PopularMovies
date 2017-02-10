package com.example.sergiopaniegoblanco.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Created by sergiopaniegoblanco on 10/02/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.PosterViewHolder>{
    private int mNumberItems;
    Context context;
    int screenWidth;
    public Adapter(int numberOfItems,int screenWidth) {
        mNumberItems = numberOfItems;
        this.screenWidth=screenWidth;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        int layoutIdForListItem = R.layout.poster_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PosterViewHolder viewHolder = new PosterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").error(R.drawable.fff).resize(screenWidth/2, 0)
                .into(holder.listItemNumberView);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    /**
     * Cache of the children views for a list item.
     */
    class PosterViewHolder extends RecyclerView.ViewHolder {

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
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(Drawable listIndex) {
            listItemNumberView.setImageDrawable(listIndex);
        }
    }
}
