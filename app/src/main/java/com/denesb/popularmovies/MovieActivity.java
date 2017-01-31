package com.denesb.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denesb.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static com.denesb.popularmovies.data.PopularMoviesPreferences.IMAGE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.OVERVIEW;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.RELEASE_DATE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.TITLE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.VOTE_AVERAGE;

public class MovieActivity extends AppCompatActivity {
    TextView mErrorMessage = null;

    LinearLayout mMovieLayout = null;

    TextView mTitleTextView = null;

    ImageView mImage = null;

    TextView mRatingTextView = null;

    TextView mOverviewTextView = null;

    /**
     * Creates a MovieActivity instance and fills it with the intent's data.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mErrorMessage = (TextView) findViewById(R.id.tv_movie_error_message);
        mMovieLayout = (LinearLayout) findViewById(R.id.movie_layout);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mImage = (ImageView) findViewById(R.id.iv_movie_poster);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);

        Intent parentIntent = getIntent();
        if (parentIntent != null && parentIntent.hasExtra(TITLE)
                                 && parentIntent.hasExtra(IMAGE)
                                 && parentIntent.hasExtra(OVERVIEW)
                                 && parentIntent.hasExtra(VOTE_AVERAGE)
                                 && parentIntent.hasExtra(RELEASE_DATE)) {
            mTitleTextView.setText(parentIntent.getStringExtra(TITLE)
                                   + " (" + parentIntent.getStringExtra(RELEASE_DATE) + ")");
            URL imageURL = NetworkUtils.buildImageUrl(parentIntent.getStringExtra(IMAGE),
                                                      NetworkUtils.ImageSize.W500);
            Picasso.with(this).load(imageURL.toString()).into(mImage);
            mRatingTextView.setText(parentIntent.getStringExtra(VOTE_AVERAGE));
            mOverviewTextView.setText(parentIntent.getStringExtra(OVERVIEW));
        }
        else {
            // An error occurred loading the movie...
            mErrorMessage.setVisibility(View.VISIBLE);
            mMovieLayout.setVisibility(View.INVISIBLE);
        }
    }
}
