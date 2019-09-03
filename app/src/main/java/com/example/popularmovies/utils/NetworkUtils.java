package com.example.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185/";

    private static final String format = "json";
    private static final String FILTER_POPULAR = "popular";
    private static final String FILTER_TOP_RATING = "top_rated";
    private static final String DEFAULT_FILTER = FILTER_POPULAR;

    public enum FilterType {
        popular(NetworkUtils.FILTER_POPULAR),
        topRated(NetworkUtils.FILTER_TOP_RATING);

        private final String queryURL;

        FilterType(String url) {
            this.queryURL = url;
        }

        public String getQueryURL() {
            return this.queryURL;
        }
    }
    /**
     * Builds the URL used www.themoviedb.org api.
     *
     * @param filter The sort filter.
     * @return The URL to use to query movie by given filter.
     */
    public static URL buildUrl(FilterType filter) {
        String path = filter == null ? DEFAULT_FILTER : filter.getQueryURL();
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendPath(path).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

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
