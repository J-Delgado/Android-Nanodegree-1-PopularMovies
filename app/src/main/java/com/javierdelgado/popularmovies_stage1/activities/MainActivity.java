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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.javierdelgado.popularmovies_stage1.Movie;
import com.javierdelgado.popularmovies_stage1.MoviesAdapter;
import com.javierdelgado.popularmovies_stage1.R;
import com.javierdelgado.popularmovies_stage1.utils.Utils;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String POPULAR = "POPULAR";
    private static final String TOP = "TOP";
    private String mLastOptionSelected = POPULAR;

    private MoviesAdapter mAdapter;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fab_filter) FloatingActionButton fab;
    @BindView(R.id.error_textview) TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //
        setSupportActionBar(mToolbar);
        //
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mToolbar.setSubtitle(R.string.most_popular);
        //
        fab.setOnClickListener(this);
        //
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MovieDownloaderTask().execute(mLastOptionSelected);
            }
        });
        //
        new MovieDownloaderTask().execute(POPULAR);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_filter){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(R.string.choose_sort);
            alertBuilder.setItems(R.array.sortings_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0){
                        new MovieDownloaderTask().execute(POPULAR);
                        mToolbar.setSubtitle(R.string.most_popular);
                        mLastOptionSelected = POPULAR;
                    } else if (i == 1){
                        new MovieDownloaderTask().execute(TOP);
                        mToolbar.setSubtitle(R.string.highest_rated);
                        mLastOptionSelected = TOP;
                    }
                }
            });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }

    class MovieDownloaderTask extends AsyncTask<String, Void, Boolean>{

        private final String popularUrl = getResources().getString(R.string.url_base_popular) + getResources().getString(R.string.API_KEY_the_movie_db);
        private final String topRatedUrl = getResources().getString(R.string.url_base_top_rated) + getResources().getString(R.string.API_KEY_the_movie_db);

        private List<Movie> moviesList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            error.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            URL url;
            String selector = params[0];
            try {
                if (selector != null && selector.equals(TOP)){
                    url = new URL(topRatedUrl);
                } else {
                    url = new URL(popularUrl);
                }
                URLConnection connection = url.openConnection();
                String response = Utils.getString(connection.getInputStream());
                //
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0; i < results.length(); i++){
                    JSONObject item = results.getJSONObject(i);

                    //Build item
                    int id = item.getInt("id");
                    String title = item.getString("title");
                    String cover = item.getString("poster_path");
                    String backdrop = item.getString("backdrop_path");
                    String releaseDate = item.getString("release_date");
                    double stars = item.getDouble("vote_average");
                    String description = item.getString("overview");
                    moviesList.add(new Movie(id,title,cover,backdrop,releaseDate,stars,description));
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (success) {
                mAdapter.setData(moviesList);
            } else {
                mAdapter.setData(new ArrayList<Movie>());
                error.setVisibility(View.VISIBLE);
            }
        }
    }
}
