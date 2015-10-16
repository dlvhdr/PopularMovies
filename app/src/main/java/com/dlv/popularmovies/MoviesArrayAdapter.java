package com.dlv.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dlv on 14/10/2015.
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    public MoviesArrayAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView movieImage = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(movie.getThumbnailURL())
            .resize(getContext().getResources().getInteger(R.integer.poster_width),
                    getContext().getResources().getInteger(R.integer.poster_height))
            .into(movieImage);

        return convertView;
    }
}
