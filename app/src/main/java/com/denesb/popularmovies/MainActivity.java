package com.denesb.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.denesb.popularmovies.utilities.NetworkUtils;
import com.denesb.popularmovies.utilities.ParseJSONData;

import static com.denesb.popularmovies.data.PopularMoviesPreferences.OVERVIEW;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.OrderType;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.IMAGE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.RELEASE_DATE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.TITLE;
import static com.denesb.popularmovies.data.PopularMoviesPreferences.VOTE_AVERAGE;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;

public class MainActivity extends AppCompatActivity {
    private final String SELECTED_SORT_ODER = "selected sort order";

    private final String JSON_MOVIES_STRING = "json movies string";
    // mMenuSortOrderItems contains the mappings between the type elements and the menu items
    private Map<OrderType, MenuItem> mMenuSortOrderItems = null;

    private OrderType mOderOrderType = OrderType.POPULAR;

    private ProgressBar mLoadingIndicator = null;

    private TextView mErrorMessage = null;

    private MoviesAdapter mMoviesAdapter = null;

    private List<MovieListItem> mMoviesData = null;
    
    private String mJsonMoviesResponse = null;

    private GridView mGridView = null;

    /**
     * Creates the Main Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkUtils.setApiValue(getString(R.string.movies_api_key));

        mMoviesAdapter = new MoviesAdapter(this);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) findViewById(R.id.movies_list);
        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mMoviesData != null) {
                    Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                    MovieListItem movieListItem = mMoviesData.get(i);
                    intent.putExtra(TITLE, movieListItem.getTitle());
                    intent.putExtra(IMAGE, movieListItem.getImage());
                    intent.putExtra(OVERVIEW, movieListItem.getOverview());
                    intent.putExtra(VOTE_AVERAGE, movieListItem.getVoteAverage());
                    intent.putExtra(RELEASE_DATE, movieListItem.getReleaseDate());
                    startActivity(intent);
                }
            }
        });

        // if there is a saved state => load the sort order and the movies JSON string
        if (savedInstanceState != null) {
            mOderOrderType = OrderType.valueOf(savedInstanceState.getString(SELECTED_SORT_ODER));
            try {
                mJsonMoviesResponse = savedInstanceState.getString(JSON_MOVIES_STRING);
                mMoviesData = ParseJSONData.parseMoviesStringFromJSON(mJsonMoviesResponse);
                mMoviesAdapter.setMoviesData(mMoviesData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Here we save the menu's sort order.
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SELECTED_SORT_ODER, mOderOrderType.toString());
        savedInstanceState.putString(JSON_MOVIES_STRING, mJsonMoviesResponse);
    }

    /**
     * Creates the Options Menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        // create mappings between the sort types and the actual menu items
        if (mMenuSortOrderItems == null) {
            mMenuSortOrderItems = new EnumMap<>(OrderType.class);
            mMenuSortOrderItems.put(OrderType.POPULAR, menu.findItem(R.id.order_popular));
            mMenuSortOrderItems.put(OrderType.TOP_RATED, menu.findItem(R.id.order_top_rated));

            setSortOrder(mOderOrderType);
        }
        return true;
    }

    /**
     * An item gets selected in the menu.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.order_popular:
                setSortOrder(OrderType.POPULAR);
                break;
            case R.id.order_top_rated:
                setSortOrder(OrderType.TOP_RATED);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the appropriate sort order.
     * @param orderType - the desired order type
     */
    private void setSortOrder(OrderType orderType) {
        if (orderType != mOderOrderType || null == mMoviesData) {
            loadMovies(orderType);
        }
        mOderOrderType = orderType;
        mMenuSortOrderItems.get(orderType).setChecked(true);
    }

    /**
     * Starts a new FetchMovieTask.
     * @param orderType
     */
    private void loadMovies(OrderType orderType) { new FetchMoviesTask().execute(orderType); }

    /**
     * Checks whether the network connection is alive.
     * @return true iff there is network connection
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Fetches the Movies Data from the net.
     */
    public class FetchMoviesTask extends AsyncTask<OrderType, Void, List<MovieListItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<MovieListItem> doInBackground(OrderType... params) {
            // if there is no given order type or no internet connection => nothing to do
            if (null == params[0] || !isOnline()) {
                return null;
            }

            URL movieRequestUrl = NetworkUtils.builMovieListdUrl(params[0]);
            try {
                mJsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return ParseJSONData.parseMoviesStringFromJSON(mJsonMoviesResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieListItem> moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mMoviesData = moviesData;
            if (moviesData != null) {
                mGridView.setVisibility(View.VISIBLE);
                mMoviesAdapter.setMoviesData(moviesData);
            }
            else {
                // show error message...
                mGridView.setVisibility(View.INVISIBLE);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
