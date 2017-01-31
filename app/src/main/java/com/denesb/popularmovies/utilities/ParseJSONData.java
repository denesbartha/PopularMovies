package com.denesb.popularmovies.utilities;

import com.denesb.popularmovies.MovieListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.denesb.popularmovies.data.PopularMoviesPreferences.IMAGE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.OVERVIEW;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.RELEASE_DATE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.TITLE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.VOTE_AVERAGE;

public final class ParseJSONData {

    private static String RESULTS = "results";

    /**
     * Parses the given JSON string into List of Movies.
     * @param moviesString
     * @return movieLists
     * @throws JSONException
     */
    public static List<MovieListItem> parseMoviesStringFromJSON(String moviesString)
            throws JSONException {
        JSONObject moviesJson = new JSONObject(moviesString);
        JSONArray results = null;
        if (moviesJson.has(RESULTS)) {
            results = moviesJson.getJSONArray(RESULTS);
        }
        if (null == results) return null;

        List<MovieListItem> movieLists = new ArrayList<MovieListItem>(results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject movie = results.getJSONObject(i);
            if (movie.has(TITLE) && movie.has(IMAGE) && movie.has(OVERVIEW) &&
                    movie.has(VOTE_AVERAGE) && movie.has(RELEASE_DATE)) {
                movieLists.add(new MovieListItem(movie.getString(TITLE),
                                                 movie.getString(IMAGE),
                                                 movie.getString(OVERVIEW),
                                                 movie.getString(VOTE_AVERAGE),
                                                 movie.getString(RELEASE_DATE)));
            }
        }
        return movieLists;
    }
}
