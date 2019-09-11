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

public class ReviewDetail implements JSONConvertible {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";

    @BindView(R.id.review_content) TextView contentTV;
    @BindView(R.id.review_author) TextView authorTV;

    private final String author;
    private final String content;

    public ReviewDetail(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public static ReviewDetail getFromJSONObject(JSONObject json) throws JSONException {
        ReviewDetail result = null;
        if (json != null) {
            String author = json.getString(KEY_AUTHOR);
            String content = json.getString(KEY_CONTENT);
            result = new ReviewDetail(author, content);
        }
        return result;
    }

    public static ReviewDetail getEmptyDetail(JSONObject json) {
        return new ReviewDetail(null, null);
    }

    @Override
    public View updateUI(View convertView, Context context, ViewGroup parent) {
        if (contentTV == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.review_list_item, parent, false);
            ButterKnife.bind(this, convertView);
        }

        contentTV.setText(getContent());
        authorTV.setText(getAuthor());
        return convertView;
    }

    public ReviewDetail getDetail(JSONObject json) throws JSONException {
        return ReviewDetail.getEmptyDetail(json);
    }

    public String getAuthor() {
        return "- \"" + (author != null ? author : "Author") + "\"";
    }

    public String getContent() {
        return content;
    }
}
