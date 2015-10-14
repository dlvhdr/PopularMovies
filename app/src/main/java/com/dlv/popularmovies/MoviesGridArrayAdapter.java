package com.dlv.popularmovies;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by dlv on 14/10/2015.
 */
public class MoviesGridArrayAdapter extends ArrayAdapter<Movie> {

    public MoviesGridArrayAdapter(Context context, List<Movie> moviesList) {
        super(context, R.layout.grid_item_movie, moviesList);
    }
}
