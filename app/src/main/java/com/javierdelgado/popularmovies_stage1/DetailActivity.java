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

package com.javierdelgado.popularmovies_stage1;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delga on 03/05/2017.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_MOVIE_ID = "KEY_MOVIE_ID";
    private RecyclerView mTrailersRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private Movie movie;
    private ProgressBar mTrailersLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use later
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        movie = null;
        if (getIntent().hasExtra(KEY_MOVIE_ID)){
            movie = (Movie) getIntent().getSerializableExtra(KEY_MOVIE_ID);
        }

        if (toolbar != null){
            toolbar.setNavigationOnClickListener(this);
            toolbar.setTitle(R.string.app_name);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        if (movie != null){
            collapsingToolbar.setTitle(movie.getTitle());
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

            ImageView moviePoster = (ImageView) findViewById(R.id.movie_poster);
            Picasso.with(this)
                    .load(getResources().getString(R.string.base_cover_url) + movie.getBackdrop())
                    .error(R.drawable.sad_icon)
                    .into(moviePoster);

            ImageView movieCover = (ImageView) findViewById(R.id.movie_cover);
            Picasso.with(this)
                    .load(getResources().getString(R.string.base_cover_url) + movie.getCover())
                    .error(R.drawable.sad_icon)
                    .into(movieCover);

            TextView movieDescription = (TextView) findViewById(R.id.movie_description);
            movieDescription.setText(movie.getDescription());
            TextView movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
            movieReleaseDate.setText(movie.getReleaseDate());
            TextView movieStars = (TextView) findViewById(R.id.movie_stars);
            movieStars.setText(String.valueOf(movie.getRating()));

            mTrailersLoading = (ProgressBar) findViewById(R.id.loading_trailers);
            mTrailersRecyclerView = (RecyclerView) findViewById(R.id.detail_trailers_recycler);
            mTrailersRecyclerView.setHasFixedSize(true);
            mTrailersAdapter = new TrailersAdapter(this);
            mTrailersRecyclerView.setAdapter(mTrailersAdapter);
            new DetailActivity.TrailerDownloaderTask().execute();

        } else {
            Toast.makeText(this, R.string.load_movie_error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    class TrailerDownloaderTask extends AsyncTask<Void, Void, Boolean> {

        private List<String> trailersList = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            mTrailersLoading.setVisibility(View.VISIBLE);
            TextView error = (TextView) findViewById(R.id.textview_error_trailer);
            error.setVisibility(View.INVISIBLE);
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
                TextView error = (TextView) findViewById(R.id.textview_error_trailer);
                error.setVisibility(View.VISIBLE);
            }
        }
    }

}
