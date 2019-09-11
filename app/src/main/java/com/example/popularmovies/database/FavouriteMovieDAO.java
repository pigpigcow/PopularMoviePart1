package com.example.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouriteMovieDAO {
    @Query("SELECT * FROM FM_FavouriteMovie")
    LiveData<List<FavouriteMovieEntry>> loadAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(FavouriteMovieEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(FavouriteMovieEntry taskEntry);

    @Delete
    void delete(FavouriteMovieEntry taskEntry);
}
