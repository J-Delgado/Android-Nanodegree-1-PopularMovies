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
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.javierdelgado.popularmovies_stage1.R;
import com.javierdelgado.popularmovies_stage1.adapters.MoviesAdapter;
import com.javierdelgado.popularmovies_stage1.data.MovieContract;
import com.javierdelgado.popularmovies_stage1.model.Movie;
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

import static android.view.View.GONE;

/**
 * Created by Delga on 03/05/2017.
 */

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int DOWNLOAD_MOVIES_LOADER = 22;

    private static final String POPULAR = "POPULAR";
    private static final String TOP = "TOP";
    private static final String FAVORITE= "FAVORITE";
    private static final String KEY_FILTER_SELECTED = "KEY_FILTER_SELECTED";

    private String mLastOptionSelected = POPULAR;

    private MoviesAdapter mAdapter;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.empty_view) LinearLayout mEmptyView;
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
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_SELECTED)){
            mLastOptionSelected = (String) savedInstanceState.get(KEY_FILTER_SELECTED);
        }
        //
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        setToolbarSubtitle();
        //
        fab.setOnClickListener(this);
        //
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Loader<List<Movie>> loader = getSupportLoaderManager().getLoader(DOWNLOAD_MOVIES_LOADER);
                if (loader == null){
                    getSupportLoaderManager().initLoader(DOWNLOAD_MOVIES_LOADER, null, MainActivity.this);
                } else {
                    getSupportLoaderManager().restartLoader(DOWNLOAD_MOVIES_LOADER, null, MainActivity.this);
                }
            }
        });
        //
        Loader<List<Movie>> loader = getSupportLoaderManager().getLoader(DOWNLOAD_MOVIES_LOADER);
        if (loader == null){
            getSupportLoaderManager().initLoader(DOWNLOAD_MOVIES_LOADER, null, this);
        } else {
            getSupportLoaderManager().restartLoader(DOWNLOAD_MOVIES_LOADER, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the menu selected is favorite, update list
        if (mLastOptionSelected.equals(FAVORITE)){
            Cursor movies = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (movies.getCount() > 0) {
                mAdapter.setData(movies);
                mEmptyView.setVisibility(GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mAdapter.setData(new ArrayList<Movie>());
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            movies.close();
        }
    }

    private void setToolbarSubtitle() {
        int subtitle;
        switch (mLastOptionSelected){
            case TOP:
                mSwipeRefreshLayout.setEnabled(true);
                subtitle = R.string.highest_rated; break;
            case FAVORITE:
                mSwipeRefreshLayout.setEnabled(false);
                error.setVisibility(GONE);
                subtitle = R.string.favorites; break;
            case POPULAR:
            default:
                mSwipeRefreshLayout.setEnabled(true);
                subtitle = R.string.most_popular;
        }
        mToolbar.setSubtitle(subtitle);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FILTER_SELECTED, mLastOptionSelected);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_filter){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(R.string.choose_sort);
            alertBuilder.setItems(R.array.sortings_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Bundle queryBundle = new Bundle();
                    LoaderManager loaderManager = getSupportLoaderManager();
                    Loader<List<Movie>> githubSearchLoader = loaderManager.getLoader(DOWNLOAD_MOVIES_LOADER);
                    if (i == 0){
                        queryBundle.putString(KEY_FILTER_SELECTED, POPULAR);
                        if (githubSearchLoader == null) {
                            loaderManager.initLoader(DOWNLOAD_MOVIES_LOADER, queryBundle, MainActivity.this);
                        } else {
                            loaderManager.restartLoader(DOWNLOAD_MOVIES_LOADER, queryBundle, MainActivity.this);
                        }
                        mEmptyView.setVisibility(GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLastOptionSelected = POPULAR;
                    } else if (i == 1){
                        queryBundle.putString(KEY_FILTER_SELECTED, TOP);
                        if (githubSearchLoader == null) {
                            loaderManager.initLoader(DOWNLOAD_MOVIES_LOADER, queryBundle, MainActivity.this);
                        } else {
                            loaderManager.restartLoader(DOWNLOAD_MOVIES_LOADER, queryBundle, MainActivity.this);
                        }
                        mEmptyView.setVisibility(GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLastOptionSelected = TOP;
                    } else if (i == 2){
                        Cursor movies = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                        if (movies.getCount() > 0) {
                            mAdapter.setData(movies);
                            mEmptyView.setVisibility(GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.setData(new ArrayList<Movie>());
                            mEmptyView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                        movies.close();
                        mLastOptionSelected = FAVORITE;
                    }
                    setToolbarSubtitle();
                }
            });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            private final String popularUrl = getResources().getString(R.string.url_base_popular) + getResources().getString(R.string.API_KEY_the_movie_db);
            private final String topRatedUrl = getResources().getString(R.string.url_base_top_rated) + getResources().getString(R.string.API_KEY_the_movie_db);

            List<Movie> mMoviesResult;

            @Override
            protected void onStartLoading() {

                error.setVisibility(View.INVISIBLE);
                //
                if (mMoviesResult != null) {
                    deliverResult(mMoviesResult);
                } else {
                    mSwipeRefreshLayout.setRefreshing(true);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {

                URL url;
                String selector = args != null ? args.getString(KEY_FILTER_SELECTED) : null;
                List<Movie> moviesList = new ArrayList<>();

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
                    return null;
                }

                return moviesList;
            }

            // COMPLETED (3) Override deliverResult and store the data in mGithubJson
            // COMPLETED (4) Call super.deliverResult after storing the data
            @Override
            public void deliverResult(List<Movie> githubJson) {
                mMoviesResult = githubJson;
                super.deliverResult(githubJson);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (data != null) {
            mAdapter.setData(data);
        } else {
            mAdapter.setData(new ArrayList<Movie>());
            error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

}
