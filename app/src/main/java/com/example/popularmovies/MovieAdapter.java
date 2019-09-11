package com.example.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.database.FavouriteMovieEntry;
import com.example.popularmovies.model.MovieDetail;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<MovieDetail> mMovieData;
    private final MovieAdapterOnClickHandler mClickHandler;
    private HashMap<Integer, FavouriteMovieEntry> mFavMap = new HashMap<>();

    MainActivity mainActivity;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieDetail md);
        void onFavouriteClick(MovieDetail md);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_iv) ImageView mPoster;
        @BindView(R.id.button_favorite) ToggleButton mFavorite;
        MovieDetail md;

        private View view;

        private MovieAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        private void populateUI(MovieDetail md) {
            this.md = md;
            mFavorite.setChecked(md.isFavourite());
            Picasso.with(this.view.getContext())
                    .load(md.getImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher_round)
                    .into(mPoster);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            mClickHandler.onClick(md);
        }

        @OnClick(R.id.button_favorite)
        public void onFavouriteClick(View view) {
            mClickHandler.onFavouriteClick(md);
        }
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setFavourites(List<FavouriteMovieEntry> entryList) {
        if(entryList != null) {
            mFavMap = new HashMap<>();
            for(FavouriteMovieEntry entry : entryList) {
                mFavMap.put(entry.getId(), entry);
            }
        }

        if(mMovieData != null && !mMovieData.isEmpty()) {
            for(MovieDetail md : mMovieData) {
                md.setFavourite( mFavMap.get(md.getId()) != null);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        return mMovieData == null ? 0 : mMovieData.size();
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new MovieDetailAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.movie_list_item, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                               contents of the item at the given position in the data set.
     * @param position               The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        MovieDetail md = mMovieData.get(position);
        movieAdapterViewHolder.populateUI(md);
    }

    public void setData(List<MovieDetail> movieData, MainViewModel vm) {
        mMovieData = movieData;
        setFavourites(vm.getFavouriteMovieEntries());
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void addToFav(FavouriteMovieEntry entry) {
        mFavMap.put(entry.getId(), entry);
    }

    public void removeFromFav(FavouriteMovieEntry entry) {
        mFavMap.remove(entry.getId());
    }
}
