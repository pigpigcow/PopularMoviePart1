package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.model.MovieDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView originalTitleTV;
    private TextView synopsisTV;
    private TextView ratingTV;
    private TextView releaseDateTV;

    private ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPoster = findViewById(R.id.image_poster_iv);
        originalTitleTV = findViewById(R.id.origin_title_tv);
        synopsisTV = findViewById(R.id.plot_synopsis_tv);
        ratingTV = findViewById(R.id.user_rating_tv);
        releaseDateTV = findViewById(R.id.release_date_tv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        MovieDetail md = null;
        try {
            md = (MovieDetail) intent.getSerializableExtra(MovieDetail.CLASSNAME);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

        if (md == null) {
            closeOnError();
            return;
        }
        populateUI(md);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(MovieDetail md) {
        originalTitleTV.setText(md.getOriginalTitle());
        synopsisTV.setText(md.getSynopsis());
        ratingTV.setText(String.valueOf(md.getRating()));
        releaseDateTV.setText(md.getReleaseDate());

        setTitle(md.getTitle());

        Picasso.with(this)
                .load(md.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(mPoster);
    }

    public String formatArrayListAsString(final List l) {
        String result = "N/A";
        if (l != null && !l.isEmpty()) {
            String arrayString = l.toString();
            result = arrayString.substring(1, arrayString.length() - 1);
        }
        return result;
    }

    public String formatSentence(final String s) {
        String result = "N/A";
        if (s != null && !s.trim().equals("")) {
            result = s.replace(". ", ".\n\n");
            //result = s;
        }
        return result;
    }
}

