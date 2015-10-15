package com.dlv.popularmovies;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Detail fragment for the DetailActivity
 */
public class DetailFragment extends Fragment {
    private Movie mMovie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra("movie")) return rootView;

        mMovie = intent.getParcelableExtra("movie");

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text);
        titleTextView.setText(mMovie.getName());

        ImageView movieImage = (ImageView) rootView.findViewById(R.id.poster_image);
        Picasso.with(getActivity()).load(mMovie.getThumbnailURL()).into(movieImage);

        TextView yearTextView = (TextView) rootView.findViewById(R.id.year_text);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(mMovie.getReleaseDate());
        yearTextView.setText(Integer.toString(calendar.get(Calendar.YEAR)));

        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_text);
        ratingTextView.setText(mMovie.getUserRating() + "/10");

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview);
        overviewTextView.setText(mMovie.getOverview());

        return rootView;
    }
}
