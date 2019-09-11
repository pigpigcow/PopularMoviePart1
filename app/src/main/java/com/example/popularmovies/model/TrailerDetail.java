package com.example.popularmovies.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerDetail implements JSONConvertible {
    public static final String KEY_VIDEO_KEY = "key";
    public static final String KEY_NAME = "name";

    @BindView(R.id.expandedTrailerListItem) TextView contentTV;

    private final String name;
    private final String key;

    public TrailerDetail(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public static TrailerDetail getFromJSONObject(JSONObject json) throws JSONException {
        TrailerDetail result = null;
        if (json != null) {
            String key = json.getString(KEY_VIDEO_KEY);
            String name = json.getString(KEY_NAME);
            result = new TrailerDetail(name, key);
        }
        return result;
    }

    public static TrailerDetail getEmptyDetail(JSONObject json) {
        return new TrailerDetail(null, null);
    }

    public TrailerDetail getDetail(JSONObject json) throws JSONException {
        return TrailerDetail.getEmptyDetail(json);
    }

    @Override
    public View updateUI(View convertView, Context context, ViewGroup parent) {
        if (contentTV == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trailer_list_item, parent, false);
            ButterKnife.bind(this, convertView);
        }
        contentTV.setText(getName());
        return convertView;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
