package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavouriteMovieEntry;
import com.example.popularmovies.model.JSONConvertible;
import com.example.popularmovies.model.MovieDetail;
import com.example.popularmovies.model.TrailerDetail;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {
    public static String YOUTUBE_INTENT_URI = "http://www.youtube.com/watch?v=";
    public static String YOUTUBE_INTENT_APP_URI = "vnd.youtube:";
    @BindView(R.id.origin_title_tv)
    TextView originalTitleTV;
    @BindView(R.id.plot_synopsis_tv)
    TextView synopsisTV;
    @BindView(R.id.user_rating_tv)
    TextView ratingTV;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTV;
    @BindView(R.id.image_poster_iv)
    ImageView mPoster;
    @BindView(R.id.button_favorite_detail)
    ToggleButton favButton;

    @BindView(R.id.loading_indicator)
    ProgressBar loadIndicator;
    @BindView(R.id.review_elv)
    ExpandableListView reviewELV;
    @BindView(R.id.trailer_elv)
    ExpandableListView trailerELV;
    @BindView(R.id.tv_error_message_display_review)
    TextView errorMSG_TV;

    int trailerELVHeight;
    int reviewELVHeight;
    MovieDetail movieDetail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
            }

            try {
                movieDetail = (MovieDetail) intent.getSerializableExtra(MovieDetail.CLASSNAME);
            } catch (NullPointerException e) {
                e.printStackTrace();
                closeOnError();
                return;
            }

            DetailViewModel vm = ViewModelProviders.of(this).get(DetailViewModel.class);
            vm.setMovieDetail(movieDetail);
        }
        populateUI();
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void populateUI() {
        DetailViewModel vm = ViewModelProviders.of(this).get(DetailViewModel.class);
        MovieDetail md = vm.getMovieDetail();
        setTitle(md.getTitle());
        Picasso.with(this)
                .load(md.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(mPoster);
        favButton.setChecked(md.isFavourite());
        originalTitleTV.setText(md.getOriginalTitle());
        synopsisTV.setText(md.getSynopsis());
        ratingTV.setText(String.valueOf(md.getRating()));
        releaseDateTV.setText(md.getReleaseDate());

        if (vm.getReviewAdapter() == null || vm.getTrailerAdapter() == null) {
            int id = md.getId();
            new reviewTask().execute(id);
        } else {
            setupExpandableListView(md);
        }
    }

    public void showLists(boolean show) {
        loadIndicator.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        reviewELV.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        trailerELV.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void setupExpandableListView(MovieDetail md) {
        DetailViewModel vm = ViewModelProviders.of(MovieDetailActivity.this).get(DetailViewModel.class);
        reviewELV.setAdapter(vm.getReviewAdapter());
        reviewELV.setOnGroupClickListener((ExpandableListView expandableListView, View view, int groupPosition, long l) -> {
            if(vm.getReviewAdapter() != null) {
                if (reviewELV.isGroupExpanded(groupPosition)) {
                    ViewUtils.setHeight(reviewELV, reviewELVHeight);
                } else {
                    reviewELVHeight = reviewELV.getHeight();
                    ViewUtils.expandListView(reviewELV, vm.getReviewAdapter().getChildrenCount(0) * 5000);
                }
            }
            return false;
        });

        trailerELV.setAdapter(vm.getTrailerAdapter());
        trailerELV.setOnGroupClickListener((ExpandableListView expandableListView, View view, int groupPosition, long l) -> {
            if(vm.getTrailerAdapter() != null) {
                if (trailerELV.isGroupExpanded(groupPosition)) {
                    ViewUtils.setHeight(trailerELV, trailerELVHeight);
                } else {
                    trailerELVHeight = trailerELV.getHeight();
                    ViewUtils.expandListView(trailerELV);
                }
            }
            return false;
        });
        trailerELV.setOnChildClickListener((ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) -> {
            TrailerDetail td = (TrailerDetail) vm.getTrailerAdapter().getChild(groupPosition, childPosition);
            watchYoutubeVideo(td.getKey());

            return true;
        });
        showLists(true);
    }

    @OnClick(R.id.button_favorite_detail)
    public void favClicked() {
        FavouriteMovieEntry entry = new FavouriteMovieEntry(movieDetail);
        movieDetail.setFavourite(!movieDetail.isFavourite());
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
            long id = mDb.favouriteMovieDAO().insert(entry);
            if (id == -1L) {
                mDb.favouriteMovieDAO().delete(entry);
            }
        });
    }

    public class reviewTask extends AsyncTask<Integer, Void, HashMap<String, List<JSONConvertible>>> {
        @Override
        protected void onPreExecute() {
            showLists(false);
            super.onPreExecute();
        }

        @Override
        protected HashMap<String, List<JSONConvertible>> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            int id = params[0];
            URL urlForReview = NetworkUtils.buildUrlForReview(id);
            URL urlForVideo = NetworkUtils.buildUrlForVideo(id);

            HashMap<String, List<JSONConvertible>> result = null;
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(urlForReview);
                List<JSONConvertible> reviewList = JsonUtils.parseReview(jsonResponse);

                jsonResponse = NetworkUtils.getResponseFromHttpUrl(urlForVideo);
                List<JSONConvertible> videoList = JsonUtils.parseTrailer(jsonResponse);

                result = MovieExpandableListAdapter.makeHashMap(reviewList, videoList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, List<JSONConvertible>> hashData) {
            if (hashData != null) {
                if (hashData.isEmpty()) {
                    errorMSG_TV.setVisibility(View.VISIBLE);
                } else {
                    DetailViewModel vm = ViewModelProviders.of(MovieDetailActivity.this).get(DetailViewModel.class);
                    List<JSONConvertible> reviewList = hashData.get(MovieExpandableListAdapter.REVIEWS_LABEL);
                    final MovieExpandableListAdapter reviewAdapter = new MovieExpandableListAdapter(MovieDetailActivity.this, reviewList, MovieExpandableListAdapter.REVIEWS_LABEL);
                    vm.setReviewAdapter(reviewAdapter);

                    List<JSONConvertible> trailerList = hashData.get(MovieExpandableListAdapter.TRAILER_LABEL);
                    final MovieExpandableListAdapter trailerAdapter = new MovieExpandableListAdapter(MovieDetailActivity.this, trailerList, MovieExpandableListAdapter.TRAILER_LABEL);
                    vm.setTrailerAdapter(trailerAdapter);

                    setupExpandableListView(vm.getMovieDetail());
                }
            } else {
                errorMSG_TV.setVisibility(View.VISIBLE);
            }
            loadIndicator.setVisibility(View.INVISIBLE);
        }
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_INTENT_APP_URI + id));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_INTENT_URI + id));
            this.startActivity(webIntent);
        }
    }
}

