package com.example.popularmovies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.popularmovies.model.MovieDetail;

public class DetailViewModel extends AndroidViewModel {
    private MovieDetail movieDetail = null;
    private MovieExpandableListAdapter trailerAdapter;
    private MovieExpandableListAdapter reviewAdapter;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public MovieDetail getMovieDetail() {
        return movieDetail;
    }

    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
    }

    public MovieExpandableListAdapter getTrailerAdapter() {
        return trailerAdapter;
    }

    public void setTrailerAdapter(MovieExpandableListAdapter trailerAdapter) {
        this.trailerAdapter = trailerAdapter;
    }

    public MovieExpandableListAdapter getReviewAdapter() {
        return reviewAdapter;
    }

    public void setReviewAdapter(MovieExpandableListAdapter reviewAdapter) {
        this.reviewAdapter = reviewAdapter;
    }
}
