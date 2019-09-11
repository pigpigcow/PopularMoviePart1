package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavouriteMovieEntry;
import com.example.popularmovies.model.MovieDetail;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private MovieAdapter mMovieAdapter;
    private AppDatabase mDb;

    @BindView(R.id.rv_movie)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    Menu menu;
    HashMap<Integer, MovieDetail> movieMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        if(savedInstanceState == null) {
            mDb = AppDatabase.getInstance(getApplicationContext());
            getAndSetFavourite();
        }

        float valueInPixels = getResources().getDimension(R.dimen.poster_width_landscape) / getResources().getDisplayMetrics().density;
        int mNoOfColumns = calNumOfColumns(valueInPixels);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mMovieAdapter.setMainActivity(this);
        MainViewModel vm = ViewModelProviders.of(this).get(MainViewModel.class);
        loadMovieData(vm.getFilterByType());
    }

    public void getAndSetFavourite() {
        MainViewModel vm = ViewModelProviders.of(this).get(MainViewModel.class);
        vm.getEntryList().observe(this, (List<FavouriteMovieEntry> entryList) -> {
            vm.setFavouriteMovieEntries(entryList);
            if (mMovieAdapter != null) {
                mMovieAdapter.setFavourites(entryList);
                movieMap = vm.getMovieMap();
                new singleMovieTask().execute(entryList.toArray(new FavouriteMovieEntry[entryList.size()]));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(MovieDetail md) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(context, destinationClass);
        movieDetailIntent.putExtra(MovieDetail.CLASSNAME, md);
        startActivity(movieDetailIntent);
    }

    public void onFavouriteClick(MovieDetail md) {
        FavouriteMovieEntry entry = new FavouriteMovieEntry(md);
        md.setFavourite(!md.isFavourite());
        Executors.newSingleThreadExecutor().execute(() -> {
            long id = mDb.favouriteMovieDAO().insert(entry);
            if (id == -1L) {
                mMovieAdapter.removeFromFav(entry);
                mDb.favouriteMovieDAO().delete(entry);
            } else {
                mMovieAdapter.addToFav(entry);
            }
        });
    }

    private void loadMovieData(NetworkUtils.FilterType type) {
        MainViewModel vm = ViewModelProviders.of(this).get(MainViewModel.class);
        type = type == null ? NetworkUtils.FilterType.popular: type;

        if(vm.needUPdate(type)) {
            if(type == NetworkUtils.FilterType.topRated || type == NetworkUtils.FilterType.popular) {
                showDataView(true);
                new movieTask().execute(type);
            } else {
                if (vm.getFavouriteMovieList() != null) {
                    mMovieAdapter.setData(vm.getFavouriteMovieList(),vm);
                    vm.setMovieDetailList(vm.getFavouriteMovieList());
                }
            }
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mMovieAdapter.setData(vm.getMovieDetailList(),vm);
        }
        vm.setFilterByType(type);
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
                MainViewModel vm = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);
                mMovieAdapter.setData(movieDetailList, vm);
                vm.setMovieDetailList(movieDetailList);
                for (MovieDetail md : movieDetailList) {
                    vm.getMovieMap().put(md.getId(), md);
                }
            }
            showDataView(movieDetailList != null && !movieDetailList.isEmpty());
        }
    }

    public class singleMovieTask extends AsyncTask<FavouriteMovieEntry, Void, List<MovieDetail>> {
        @Override
        protected List<MovieDetail> doInBackground(FavouriteMovieEntry... params) {
            if (params.length == 0) {
                return null;
            }

            ArrayList<MovieDetail> favouriteMovieList = new ArrayList<>();
            for (FavouriteMovieEntry entry : params) {
                MovieDetail md = movieMap.get(entry.getId());
                if(md == null) {
                    URL url = NetworkUtils.buildUrlMovie(entry.getId());
                    try {
                        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                        md = JsonUtils.parseSingleMovieJson(jsonResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                favouriteMovieList.add(md);
            }
            return favouriteMovieList;
        }

        @Override
        protected void onPostExecute(List<MovieDetail> movieDetailList) {
            MainViewModel vm = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);
            vm.setFavouriteMovieList(movieDetailList);
            if (vm.getFilterByType() == NetworkUtils.FilterType.favourite) {
                mMovieAdapter.setData(movieDetailList, vm);
            } else {
                mMovieAdapter.setFavourites(vm.getFavouriteMovieEntries());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_rating || id == R.id.sort_popular) {
            NetworkUtils.FilterType type = id == R.id.sort_rating ? NetworkUtils.FilterType.topRated : NetworkUtils.FilterType.popular;
            loadMovieData(type);
            return true;
        } else if (id == R.id.action_favourite) {
            loadMovieData(NetworkUtils.FilterType.favourite);
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
