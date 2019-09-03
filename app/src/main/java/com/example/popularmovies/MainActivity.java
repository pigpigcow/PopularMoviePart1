package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.model.MovieDetail;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    public static final NetworkUtils.FilterType DEFAULT_TYPE = NetworkUtils.FilterType.popular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movie);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        float valueInPixels = getResources().getDimension(R.dimen.poster_width) / getResources().getDisplayMetrics().density;
        int mNoOfColumns = calNumOfColumns(valueInPixels);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieData(DEFAULT_TYPE);
    }

    @Override
    public void onClick(MovieDetail md) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(context, destinationClass);
        movieDetailIntent.putExtra(MovieDetail.CLASSNAME, md);
        startActivity(movieDetailIntent);
    }

    private void loadMovieData(NetworkUtils.FilterType type) {
        showDataView(true);
        new movieTask().execute(type);
    }

    private void showDataView(boolean show) {
        int displayError = show ? View.INVISIBLE : View.VISIBLE;
        int displayData = !show ? View.INVISIBLE : View.VISIBLE;
        mErrorMessageDisplay.setVisibility(displayError);
        mRecyclerView.setVisibility(displayData);
    }

    public class movieTask extends AsyncTask<NetworkUtils.FilterType, Void, List<MovieDetail>> {
        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<MovieDetail> doInBackground(NetworkUtils.FilterType... params) {
            if (params.length == 0) {
                return null;
            }

            NetworkUtils.FilterType type = params[0];
            URL url = NetworkUtils.buildUrl(type);

            List<MovieDetail> movieDetailList = null;
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                movieDetailList = JsonUtils.parseMovieJson(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieDetailList;
        }

        @Override
        protected void onPostExecute(List<MovieDetail> movieDetailList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieDetailList != null) {
                showDataView(true);
                mMovieAdapter.setData(movieDetailList);
            } else {
                showDataView(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_rating || id == R.id.sort_popular) {
            NetworkUtils.FilterType type = id == R.id.sort_rating ? NetworkUtils.FilterType.topRated : NetworkUtils.FilterType.popular;
            loadMovieData(type);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int calNumOfColumns(float columnWidthDp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }
}
