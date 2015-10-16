package com.dlv.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dlv on 14/10/2015.
 */
public class MoviesGridFragment extends Fragment {
    private final static String MOVIES_LIST_KEY = "movies";
    private static final String POPULARITY_DESC = "popularity.desc";
    private static final String VOTE_AVERAGE_DESC = "vote_average.desc";
    private static final String SORT_BY = "sort_by";

    private MoviesArrayAdapter mArrayAdapter;
    private ArrayList<Movie> mMovies;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_LIST_KEY)) {
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);
        } else {
            mMovies = new ArrayList<Movie>();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        GridView moviesGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        mArrayAdapter = new MoviesArrayAdapter(getActivity(), mMovies);
        moviesGridView.setAdapter(mArrayAdapter);
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mArrayAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_KEY, mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies_fragment, menu);

        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortBy = settings.getString(SORT_BY, POPULARITY_DESC);

        if (sortBy.equals(POPULARITY_DESC)) {
            menu.findItem(R.id.action_most_popular).setChecked(true);
        } else if (sortBy.equals(VOTE_AVERAGE_DESC)) {
            menu.findItem(R.id.action_highest_rating).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        } else if (id == R.id.action_most_popular) {
            if (!item.isChecked()) item.setChecked(true);
            saveSortSetting(POPULARITY_DESC);
            updateMovies();
            return true;
        } else if (id == R.id.action_highest_rating) {
            if (!item.isChecked()) item.setChecked(true);
            saveSortSetting(VOTE_AVERAGE_DESC);
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSortSetting(String value) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SORT_BY, value);
        editor.commit();
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Only update if we have no movies from the savedInstanceBundle
        if (mMovies.size() == 0) updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(Void... params) {

            String moviesJsonStr = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = SORT_BY;
            final String KEY_PARAM ="api_key";

            String apiKey = ApiKey.API_SSH_KEY;
            SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
            String sortBy = settings.getString(SORT_BY, POPULARITY_DESC);

            try {
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error paring the JSON", e);
                return null;
            }

            try {
                return buildMoviesFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies == null) return;

            mArrayAdapter.clear();
            for (Movie movie : movies) {
                mArrayAdapter.add(movie);
            }
        }

        private Movie[] buildMoviesFromJson(String moviesJsonStr) throws JSONException {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            SimpleDateFormat dateFormat = new SimpleDateFormat(Movie.DATE_FORMAT);

            if (moviesArray == null || moviesArray.length() <= 0) return null;
            ArrayList<Movie> results = new ArrayList<>();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJson = moviesArray.getJSONObject(i);

                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(movieJson.getString("release_date"));
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error parsing release date", e);
                    return null;
                }

                //Avoid movies without a poster
                String poster_path = movieJson.getString("poster_path");
                if (poster_path.equals("null")) continue;

                Movie movie = new Movie(movieJson.getLong("id"));
                movie.setName(movieJson.getString("original_title"));
                movie.setOverview(movieJson.getString("overview"));
                movie.setThumbnailFileName(poster_path);
                movie.setUserRating(movieJson.getDouble("vote_average"));
                movie.setReleaseDate(releaseDate);

                results.add(movie);
            }

            return results.toArray(new Movie[results.size()]);
        }
    }
}