package com.example.popularmovies.model;

import com.example.popularmovies.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MovieDetail implements Serializable {
    public static final String CLASSNAME = MovieDetail.class.toString();
    public static final String KEY_ORITINAL_TITLE = "original_title";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SYNOPSIS = "overview";
    public static final String KEY_RATING = "vote_average";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_POSTER = "poster_path";

    private final String originalTitle;
    private final String title;
    private final String synopsis;
    private final float rating;
    private final String releaseDate;
    private final String image;

    public MovieDetail(String originalTitle, String title, String synopsis, float rating, String releaseDate, String image) {
        this.originalTitle = originalTitle;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.image = NetworkUtils.BASE_IMAGE_URL +  image;
    }

    public static MovieDetail getFromJSONObject(JSONObject json) throws JSONException {
        MovieDetail result = null;
        if (json != null) {
            String originalTitle = json.getString(KEY_ORITINAL_TITLE);
            String title = json.getString(KEY_TITLE);
            String synopsis = json.getString(KEY_SYNOPSIS);
            double rating = json.getDouble(KEY_RATING);
            String releaseDate = json.getString(KEY_RELEASE_DATE);
            String image = json.getString(KEY_POSTER);
            result = new MovieDetail(originalTitle, title, synopsis, (float) rating, releaseDate, image);
        }
        return result;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public float getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImage() {
        return image;
    }
}
