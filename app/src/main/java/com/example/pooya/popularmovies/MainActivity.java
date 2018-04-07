package com.example.pooya.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.pooya.popularmovies.adapter.CategorizeMovie;
import com.example.pooya.popularmovies.adapter.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ForecastAdapterOnClickHandler {

    private static final int MOVIE_LIST_ITEM = 100;

    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private ProgressBar mLoadingIndicator;
    /** Put own api key from themoviedb.org to gradle.properties in the following form
     API_KEY = "api key goes here" */
    private static String urls = "http://api.themoviedb.org/3/movie/popular";
    private static String urlr = "http://api.themoviedb.org/3/movie/top_rated";
    final static String PARAM_QUERY = "api_key";
    final static String apikey = "";
    CategorizeMovie[] CategorizeMovies;
    int go = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesList = findViewById(R.id.recycler_view);
        mLoadingIndicator = findViewById(R.id.progress_bar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(mAdapter);
        goToPop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected=item.getItemId();
        if (menuItemThatWasSelected==R.id.action_sort_rating)
        {
            go=0;
            MovieAdapter.setData(null);
            goToRate();
            return true;
        }
        if (menuItemThatWasSelected==R.id.action_sort_popularity)
        {
            MovieAdapter.setData(null);
            go=1;
            goToPop();
            return true;
        }
        return true;
    }

    public void goToPop() {
        mMoviesList.setAdapter(null);
        CategorizeMovies = null;
        MovieAdapter.setData(null);
        URL query = uriBuilder(urls);
        new queryTask().execute(query);
    }

    public void goToRate() {
        mMoviesList.setAdapter(null);
        CategorizeMovies = null;
        MovieAdapter.setData(null);
        URL query = uriBuilder(urlr);
        new queryTask().execute(query);
    }

    public URL uriBuilder(String uri) {
        Uri builtUri = Uri.parse(uri).buildUpon()
                .appendQueryParameter(PARAM_QUERY, apikey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onClick(CategorizeMovie CategorizeMovieed, int idm) {
        Intent startChildActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle extras = new Bundle();
        extras.putString("ide", CategorizeMovieed.ip);
        startChildActivityIntent.putExtras(extras);
        startActivity(startChildActivityIntent);
    }

    private class queryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL searchURL = params[0];
            String searchResults = null;
            try {
                searchResults = getResponseFromHttpUrl(searchURL);
                if (searchResults != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(searchResults);
                        JSONArray contacts = jsonObj.getJSONArray("results");
                        CategorizeMovies = new CategorizeMovie[contacts.length()];
                        for (int i = 0; i <= contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String id = c.getString("id");
                            String title = c.getString("title");
                            String vote_count = c.getString("vote_count");
                            String overview = c.getString("overview");
                            String poster_path = c.getString("poster_path");
                            CategorizeMovies[i] = new CategorizeMovie(title, vote_count, overview, poster_path, id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Abhishek", "Chinmaya");
            if (s != null && !s.equals("")) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mMoviesList.setLayoutManager(layoutManager);
                mMoviesList.setHasFixedSize(true);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                MovieAdapter.setData(CategorizeMovies);
            }

        }

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {

                return scanner.next();
            } else {

                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
