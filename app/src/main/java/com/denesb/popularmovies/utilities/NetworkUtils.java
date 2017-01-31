package com.denesb.popularmovies.utilities;

import android.media.Image;
import android.net.Uri;
import android.util.Log;

import static com.denesb.popularmovies.data.PopularMoviesPreferences.OrderType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_LIST_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String POPULAR = "popular";

    private static final String TOP_RATED = "top_rated";

    private static final String PAGE = "page";

    private static final String PAGE_NUMBER = "1";

    private static final String API_KEY = "api_key";

    private static String API_VALUE = "";

    private static final String LANGUAGE = "language";

    private static final String ENGLISH = "english";

    public static void setApiValue(String apiValue) {
        API_VALUE = apiValue;
    }

    // enum for storing the image sizes
    public enum ImageSize {
        W92("w92"), W154("w154"), W185("w185"), W342("w342"),
        W500("w500"), W780("w780"), ORIGINAL("original");

        private final String str;
        ImageSize(String pstr) { str = pstr; }

        @Override
        public String toString() {
            return str;
        }
    }

    /**
     * Builds the URL pointing to the movies database.
     *
     * @param orderType the sort order type
     * @return The URL to use to query the movies database.
     */
    public static URL builMovieListdUrl(OrderType orderType) {
        Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                .appendPath(orderType == OrderType.POPULAR ? NetworkUtils.POPULAR : NetworkUtils.TOP_RATED)
                .appendQueryParameter(API_KEY, API_VALUE)
                .appendQueryParameter(PAGE, PAGE_NUMBER)
                .appendQueryParameter(LANGUAGE, ENGLISH)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Builds the URL pointing to the appropriate image.
     * @param imageId
     * @param imageSize
     * @return
     */
    public static URL buildImageUrl(String imageId, ImageSize imageSize) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                                                .appendPath(imageSize.toString())
                                                .appendEncodedPath(imageId)
                                                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
