package com.example.popularmovies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.popularmovies.model.MovieDetail;

@Entity(tableName = "FM_FavouriteMovie")
public class FavouriteMovieEntry {
    @PrimaryKey
    private int id;
    private String title;

    @Ignore
    public FavouriteMovieEntry(MovieDetail md) {
        this(md.getId(), md.getTitle());
    }

    public FavouriteMovieEntry(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() { return id; }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
