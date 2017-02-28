package com.example.sergiopaniegoblanco.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by sergiopaniegoblanco on 27/02/2017.
 */

public class FavListContract {

    public static final class FavlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "favmovies";
        public static final String COLUMN_MOVIE_NAME = "movieName";
    }
}
