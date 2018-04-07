package com.example.pooya.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DetailActivity extends AppCompatActivity {

    public static String ur = "http://image.tmdb.org/t/p/w500/";

    private static String urls = "http://api.themoviedb.org/3/movie/popular";
    private static String urlr = "http://api.themoviedb.org/3/movie/top_rated";
    /** Put own api key from themoviedb.org to gradle.properties in the following form
     API_KEY = "api key goes here" */
    final static String PARAM_QUERY = "api_key";
    final static String apikey = "";
    String id;
    int help;
    ImageView imageView;
    Intent intent;
    TextView textView2, textView3, textView5, textView6;
    private ProgressBar mLoadingIndicator;

    String textEntered, title, vote_count, overview, poster_path, release_date, vote_average;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textView2 = findViewById(R.id.titles);
        textView3 = findViewById(R.id.year);
        textView5 = findViewById(R.id.rating);
        textView6 = findViewById(R.id.detail);
        imageView = findViewById(R.id.images);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        intent = getIntent();
        Bundle extras = intent.getExtras();
        textEntered = extras.getString("ide");
        help = extras.getInt("democheck");
        if (help == 1) {
            URL query = uriBuilders(urls);
            new queryTasks().execute(query);

        } else {
            URL query = uriBuilders(urlr);
            new queryTasks().execute(query);

        }
    }



    public URL uriBuilders(String uri) {
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


    private class queryTasks extends AsyncTask<URL, Void, String> {
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
                        for (int i = 0; i <= contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            id = c.getString("id");
                            if (textEntered.equals(id)) {
                                release_date = c.getString("release_date");
                                vote_average = c.getString("vote_average");
                                title = c.getString("title");
                                vote_count = c.getString("vote_count");
                                overview = c.getString("overview");
                                poster_path = c.getString("poster_path");
                                i = contacts.length();

                            }

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
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (s != null && !s.equals("")) {
                textView2.setText(title);
                textView3.setText(release_date);
                String voting = vote_average + getString(R.string.complete);
                textView5.setText(voting);
                textView6.setText(overview);
                String web = ur + poster_path;
                Picasso.with(getApplicationContext()).load(web).into(imageView);
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
