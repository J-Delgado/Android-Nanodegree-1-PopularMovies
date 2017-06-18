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

package com.javierdelgado.popularmovies_stage1.activities;

import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javierdelgado.popularmovies_stage1.R;
import com.javierdelgado.popularmovies_stage1.adapters.ReviewsAdapter;
import com.javierdelgado.popularmovies_stage1.adapters.TrailersAdapter;
import com.javierdelgado.popularmovies_stage1.data.MovieContract;
import com.javierdelgado.popularmovies_stage1.model.Movie;
import com.javierdelgado.popularmovies_stage1.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Delga on 03/05/2017.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_MOVIE_ID = "KEY_MOVIE_ID";
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private Movie movie;
    //
    @BindView(R.id.detail_trailers_recycler) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.loading_trailers) ProgressBar mTrailersLoading;
    @BindView(R.id.toolbar_detail) Toolbar toolbar;
    @BindView(R.id.fab_favorite) FloatingActionButton fabFavorite;
    @BindView(R.id.fab_share) FloatingActionButton fabShare;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.movie_poster) ImageView moviePoster;
    @BindView(R.id.movie_cover) ImageView movieCover;
    @BindView(R.id.movie_description) TextView movieDescription;
    @BindView(R.id.movie_release_date) TextView movieReleaseDate;
    @BindView(R.id.movie_stars) TextView movieStars;
    @BindView(R.id.textview_error_trailer) TextView trailer_error;
    @BindView(R.id.detail_reviews_recycler) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.textview_error_reviews) TextView reviews_error;
    @BindView(R.id.loading_reviews) ProgressBar mReviewsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use later
        fabFavorite.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        if (getIntent().hasExtra(KEY_MOVIE_ID)){
            movie = (Movie) getIntent().getSerializableExtra(KEY_MOVIE_ID);
        }
        if (movie.isFavorite()){
            fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_selected_icon));
        } else {
            fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_unselected_icon));
        }

        if (toolbar != null){
            toolbar.setNavigationOnClickListener(this);
            toolbar.setTitle(R.string.app_name);
        }

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        if (movie != null){
            collapsingToolbar.setTitle(movie.getTitle());
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

            Picasso.with(this)
                    .load(getResources().getString(R.string.base_cover_url) + movie.getBackdrop())
                    .error(R.drawable.sad_icon)
                    .into(moviePoster);

            Picasso.with(this)
                    .load(getResources().getString(R.string.base_cover_url) + movie.getCover())
                    .error(R.drawable.sad_icon)
                    .into(movieCover);

            movieDescription.setText(movie.getDescription());
            movieReleaseDate.setText(movie.getReleaseDate());
            movieStars.setText(String.valueOf(movie.getRating()));

            // Trailers
            mTrailersRecyclerView.setHasFixedSize(true);
            mTrailersAdapter = new TrailersAdapter(this);
            mTrailersRecyclerView.setAdapter(mTrailersAdapter);
            new DetailActivity.TrailerDownloaderTask().execute();

            // Reviews
            mReviewsAdapter = new ReviewsAdapter();
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);
            new DetailActivity.ReviewsDownloaderTask().execute();

        } else {
            Toast.makeText(this, R.string.load_movie_error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_favorite){
            if (movie.isFavorite()) {
                boolean success = deleteFavorite();
                if (success) {
                    movie.setFavorite(!movie.isFavorite());
                    ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.star_unselected_icon));
                } else {
                    Toast.makeText(getApplicationContext(),R.string.not_deleted_favorite,Toast.LENGTH_LONG).show();
                }
            } else {
                boolean success = addFavorite();
                if (success) {
                    movie.setFavorite(!movie.isFavorite());
                    ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.star_selected_icon));
                } else {
                    Toast.makeText(getApplicationContext(),R.string.not_added_favorite,Toast.LENGTH_LONG).show();
                }            }
        } else if (view.getId() == R.id.fab_share) {
            String videoPath = getResources().getString(R.string.url_base_youtube)
                    + mTrailersAdapter.getTrailerUrl(0);
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle(R.string.share)
                    .setText(getString(R.string.share_text) + " " + movie.getTitle() + "? " + videoPath)
                    .startChooser();
        } else {
            onBackPressed();
        }
    }

    private boolean deleteFavorite() {
        // Prepare
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movie.getId())};
        // Delete
        return getContentResolver().delete(uri, selection, selectionArgs) == 1;
    }

    private boolean addFavorite() {
        // Prepare
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdrop());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getCover());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPOSIS, movie.getDescription());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        // Delete
        return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues) != null;
    }

    class TrailerDownloaderTask extends AsyncTask<Void, Void, Boolean> {

        private List<String> trailersList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            mTrailersLoading.setVisibility(View.VISIBLE);
            trailer_error.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url_base_movie)
                        + movie.getId()
                        + getResources().getString(R.string.url_part_trailer)
                        + getResources().getString(R.string.API_KEY_the_movie_db)
                );
                URLConnection connection = url.openConnection();
                String response = Utils.getString(connection.getInputStream());
                //
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0; i < results.length(); i++){
                    JSONObject item = results.getJSONObject(i);

                    //Build item
                    String key = item.getString("key");
                    trailersList.add(key);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTrailersLoading.setVisibility(View.INVISIBLE);
            if (success) {
                mTrailersAdapter.setData(trailersList);
            } else {
                mTrailersAdapter.setData(new ArrayList<String>());
                trailer_error.setVisibility(View.VISIBLE);
            }
        }
    }

    class ReviewsDownloaderTask extends AsyncTask<Void, Void, Boolean> {

        private List<String> reviewsList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            mReviewsLoading.setVisibility(View.VISIBLE);
            reviews_error.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url_base_movie)
                        + movie.getId()
                        + getResources().getString(R.string.url_part_reviews)
                        + getResources().getString(R.string.API_KEY_the_movie_db)
                );
                URLConnection connection = url.openConnection();
                String response = Utils.getString(connection.getInputStream());
                //
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0; i < results.length(); i++){
                    JSONObject item = results.getJSONObject(i);

                    //Build item
                    String key = item.getString("content");
                    reviewsList.add(key);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mReviewsLoading.setVisibility(View.INVISIBLE);
            if (success) {
                mReviewsAdapter.setData(reviewsList);
            } else {
                mReviewsAdapter.setData(new ArrayList<String>());
                reviews_error.setVisibility(View.VISIBLE);
            }
        }
    }

}
