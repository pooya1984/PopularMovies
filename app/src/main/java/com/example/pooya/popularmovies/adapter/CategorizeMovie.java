package com.example.pooya.popularmovies.adapter;

/**
 * Created by pooya on 14/03/2018.
 */

public class CategorizeMovie {
    String title;
    String vote_count;
    String overview;
    String poster_path;
    public String ip;

    public CategorizeMovie(String n, String m, String g, String p, String i) {
        title = n;
        vote_count = m;
        overview = g;
        poster_path = p;
        ip = i;
    }
}
