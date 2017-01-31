package com.denesb.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.net.URL;
import java.util.List;

import com.denesb.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends ArrayAdapter<MovieListItem> {
    private List<MovieListItem> mMoviesData = null;

    private Activity mContext = null;

    /**
     * Constructor for MoviesAdapter class.
     * @param context
     */
    public MoviesAdapter(Activity context) {
        super(context, 0);
        mContext = context;
    }

    /**
     * Gets the count of the Movies Data.
     * @return the size of mMoviesData
     */
    @Override
    public int getCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.size();
    }

    /**
     * Inflates each list item in the grid.
     * @param position
     * @param convertView
     * @param parent
     * @return convertView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext())
                                        .inflate(R.layout.movie_list_item, parent, false);
        }
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
        URL imageURL = NetworkUtils.buildImageUrl(mMoviesData.get(position).getImage(),
                NetworkUtils.ImageSize.W342);
        Picasso.with(getContext()).load(imageURL.toString()).into(thumbnail);
        return convertView;
    }

    /**
     * Updates moviesData.
     * @param moviesData
     */
    public void setMoviesData(List<MovieListItem> moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
