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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.javierdelgado.popularmovies_stage1.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * Created by Delga on 03/05/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private List<String> mTrailersList = null;
    private Context mContext;

    public TrailersAdapter(Context context) {
        mContext = context;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailersViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailersList == null ? 0 : mTrailersList.size();
    }

    public void setData(List<String> trailers){
        mTrailersList = trailers;
        notifyDataSetChanged();
    }

    public String getTrailerUrl(int position){
        if (mTrailersList != null && mTrailersList.size() > 0 && position < mTrailersList.size()){
            return mTrailersList.get(position);
        }
        return "";
    }

    class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;

        @BindView(R.id.play_icon) ImageView playIcon;
        @BindView(R.id.loading_icon) ProgressBar loadingIcon;
        @BindView(R.id.trailer_thmb) ImageView trailerThmb;

        public TrailersViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            loadingIcon.setVisibility(View.VISIBLE);
            playIcon.setVisibility(View.INVISIBLE);
            Picasso.with(itemView.getContext())
                    .load(context.getResources().getString(R.string.url_base_youtube_video_tmhb) + mTrailersList.get(position) + "/0.jpg")
                    .error(R.drawable.sad_icon)
                    .into(trailerThmb, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingIcon.setVisibility(GONE);
                            playIcon.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            playIcon.setVisibility(GONE);
                            loadingIcon.setVisibility(GONE);
                        }
                    })
                    ;

        }

        @Override
        public void onClick(View view) {
            String videoPath = mContext.getResources().getString(R.string.url_base_youtube)
                    + mTrailersList.get(this.getAdapterPosition());
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(videoPath));
            mContext.startActivity(intent);
        }
    }
}
