package com.example.popularmovies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavouriteMovieEntry;
import com.example.popularmovies.model.MovieDetail;
import com.example.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<FavouriteMovieEntry>> entryList;
    private NetworkUtils.FilterType filterByType = null;
    List<MovieDetail> movieDetailList = new ArrayList<>();

    List<FavouriteMovieEntry> favouriteMovieEntries = null;
    HashMap<Integer, MovieDetail> movieMap = new HashMap<>();
    List<MovieDetail> favouriteMovieList = null;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        entryList = db.favouriteMovieDAO().loadAll();
    }

    public LiveData<List<FavouriteMovieEntry>> getEntryList() {
        return entryList;
    }

    public NetworkUtils.FilterType getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(NetworkUtils.FilterType filterByType) {
        this.filterByType = filterByType;
    }

    public List<MovieDetail> getMovieDetailList() {
        return movieDetailList;
    }

    public void setMovieDetailList(List<MovieDetail> movieDetailList) {
        this.movieDetailList = movieDetailList;
    }

    public boolean needUPdate(NetworkUtils.FilterType filter) {
        return filterByType != filter;
    }

    public List<FavouriteMovieEntry> getFavouriteMovieEntries() {
        return favouriteMovieEntries;
    }

    public void setFavouriteMovieEntries(List<FavouriteMovieEntry> favouriteMovieEntries) {
        this.favouriteMovieEntries = favouriteMovieEntries;
    }

    public HashMap<Integer, MovieDetail> getMovieMap() {
        return movieMap;
    }

    public void setMovieMap(HashMap<Integer, MovieDetail> movieMap) {
        this.movieMap = movieMap;
    }

    public List<MovieDetail> getFavouriteMovieList() {
        return favouriteMovieList;
    }

    public void setFavouriteMovieList(List<MovieDetail> favouriteMovieList) {
        this.favouriteMovieList = favouriteMovieList;
    }
}
