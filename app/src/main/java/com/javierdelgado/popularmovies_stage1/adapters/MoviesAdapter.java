/*
 * Copyright (C) 2017 Javier Delgado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javierdelgado.popularmovies_stage1.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.javierdelgado.popularmovies_stage1.R;
import com.javierdelgado.popularmovies_stage1.activities.DetailActivity;
import com.javierdelgado.popularmovies_stage1.data.MovieContract;
import com.javierdelgado.popularmovies_stage1.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Delga on 03/05/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movie> mMoviesList = null;
    private Context mContext;

    public MoviesAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MoviesViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMoviesList == null ? 0 : mMoviesList.size();
    }

    public void setData(List<Movie> movies){
        mMoviesList = movies;
        notifyDataSetChanged();
    }

    public void setData(Cursor movies) {
        List<Movie> list = new ArrayList<>();
        while (movies.moveToNext()) {
            int id = movies.getInt(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            String title = movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String cover = movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String backdrop = movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));
            String releaseDate = movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            double rating = movies.getDouble(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            String description = movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPOSIS));
            // Create movie
            Movie movie = new  Movie(id, title, cover, backdrop, releaseDate, rating, description);
            movie.setFavorite(true);
            list.add(movie);
        }
        mMoviesList = list;
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @BindView(R.id.item_moviecover) ImageView movieCover;

        public MoviesViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Picasso.with(itemView.getContext())
                    .load(context.getResources().getString(R.string.base_cover_url) + mMoviesList.get(position).getCover())
                    .error(R.drawable.sad_icon)
                    .into(movieCover);
        }

        @Override
        public void onClick(View view) {
            Intent detailIntent = new Intent(mContext, DetailActivity.class);

            // If the movie is in database, then it is favorite
            Movie movie = mMoviesList.get(getAdapterPosition());
            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
            String[] selectionArgs = new String[]{Integer.toString(movie.getId())};
            Cursor query = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);
            if (query.getCount() == 1) {
                movie.setFavorite(true);
            }
            detailIntent.putExtra(DetailActivity.KEY_MOVIE_ID,movie);

            //Set up transition
            Pair<View, String> pair_cover = Pair.create((View) movieCover, mContext.getString(R.string.transition_movie_image));

            //Animation
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mContext, pair_cover);

            //Start activity
            ActivityCompat.startActivity(mContext, detailIntent, options.toBundle());
        }
    }
}
