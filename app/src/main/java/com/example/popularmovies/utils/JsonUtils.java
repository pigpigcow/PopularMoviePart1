package com.example.popularmovies.utils;

import android.util.Log;

import com.example.popularmovies.model.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static final String KEY_RESULT = "results";

    public static List<MovieDetail> parseMovieJson(String json) {
        List<MovieDetail> result = new ArrayList<>();
        if (json != null && !json.trim().equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray results = jsonObject.getJSONArray(KEY_RESULT);

                for(int i = 0; i < results.length(); i++) {
                    result.add(MovieDetail.getFromJSONObject(results.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.e("Parsing Error", e.toString());
                e.printStackTrace();
            }
        }
        return result;
    }
}
