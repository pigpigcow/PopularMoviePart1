package com.example.popularmovies.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONConvertible {
    Object getDetail(JSONObject json) throws JSONException;
    View updateUI(View convertView, Context context, ViewGroup parent);
}
