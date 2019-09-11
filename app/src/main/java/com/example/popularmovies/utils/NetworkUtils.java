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
    private static final String API_KEY_LABEL = "api_key";
    private static final String API_KEY = "8fa0ff56b85ef702fb09a563ebc37fb3";

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDOE_URL_PART = "videos";
    private static final String MOVIE_REVIEW_URL_PART = "reviews";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185/";

    private static final String format = "json";
    private static final String FILTER_POPULAR = "popular";
    private static final String FILTER_TOP_RATING = "top_rated";
    private static final String FILTER_FAVOURITE= "favourite";
    private static final String DEFAULT_FILTER = FILTER_POPULAR;

    public enum FilterType {
        popular(NetworkUtils.FILTER_POPULAR),
        topRated(NetworkUtils.FILTER_TOP_RATING),
        favourite(NetworkUtils.FILTER_FAVOURITE);

        private final String queryURL;

        FilterType(String url) {
            this.queryURL = url;
        }
        public String getQueryURL() {
            return this.queryURL;
        }
    }

    public static URL getURLFromUri(Uri builtUri) {
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
     * Builds the URL used www.themoviedb.org api.
     *
     * @param filter The sort filter.
     * @return The URL to use to query movie by given filter.
     */
    public static URL buildUrl(FilterType filter) {
        String path = filter == null ? DEFAULT_FILTER : filter.getQueryURL();
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendPath(path)
                .appendQueryParameter(API_KEY_LABEL, API_KEY).build();
        return getURLFromUri(builtUri);
    }

    /**
     * Builds the URL used www.themoviedb.org api.
     *
     * @param id movie id.
     * @return The URL to use to query review.
     */
    public static URL buildUrlForReview(int id) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(MOVIE_REVIEW_URL_PART)
                .appendQueryParameter(API_KEY_LABEL, API_KEY)
                .build();
        return getURLFromUri(builtUri);
    }

    /**
     * Builds the URL used www.themoviedb.org api.
     *
     * @param id movie id.
     * @return The URL to use to query review.
     */
    public static URL buildUrlForVideo(int id) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(MOVIE_VIDOE_URL_PART)
                .appendQueryParameter(API_KEY_LABEL, API_KEY)
                .build();
        return getURLFromUri(builtUri);
    }

    /**
     * Builds the URL to get movie info given id
     *
     * @param id movie id.
     * @return The URL to use to query review.
     */
    public static URL buildUrlMovie(int id) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_KEY_LABEL, API_KEY)
                .build();
        return getURLFromUri(builtUri);
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
        String result = null;
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                result = scanner.next();
            }
        } catch (IOException e) {
            Log.v("NetworkUtils", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }
}
