package com.example.sergiopaniegoblanco.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sergiopaniegoblanco on 27/02/2017.
 */

public class FavListContract {

    // The authority, which is how the code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.sergiopaniegoblanco.popularmovies";
    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // Define the possible paths for accessing data in this contract
    // This is the path for the "favmovies" directory
    public static final String PATH_FAVS = "favmovies";

    public static final class FavlistEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVS).build();
        public static final String TABLE_NAME = "favmovies";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_POSTER = "moviewPoster";
        public static final String COLUMN_MOVIE_JSON = "movieDetail";
    }
}
