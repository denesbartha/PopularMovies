package com.denesb.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieListItem implements Parcelable {
    private String title;

    private String image;

    private String overview;

    private String vote_average;

    private String release_date;

    /**
     * Getter for title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for image.
     * @return image
     */
    public String getImage() {
        return image;
    }

    /**
     * Getter for overview.
     * @return overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Getter for vote_average.
     * @return vote_average
     */
    public String getVoteAverage() {
        return vote_average;
    }

    /**
     * Getter for release_date.
     * @return release_date
     */
    public String getReleaseDate() {
        return release_date;
    }

    /**
     * Constructor for the MovieListItem class
     * @param title
     * @param image
     */
    public MovieListItem(String title, String image, String overview,
                         String vote_average, String release_date)  {
        this.title = title;
        this.image = image;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    /**
     * Constructor for the MovieListItem class (Parcelable)
     * @param in
     */
    private MovieListItem(Parcel in){
        title = in.readString();
        image = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
    }

    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method for saving the attributes into the parcelable.
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(overview);
        parcel.writeString(vote_average);
        parcel.writeString(release_date);
    }

    /**
     * Creator for Parcelable.
     */
    public final Parcelable.Creator<MovieListItem> CREATOR = new Parcelable.Creator<MovieListItem>() {
        @Override
        public MovieListItem createFromParcel(Parcel parcel) {
            return new MovieListItem(parcel);
        }

        @Override
        public MovieListItem[] newArray(int i) {
            return new MovieListItem[i];
        }

    };
}