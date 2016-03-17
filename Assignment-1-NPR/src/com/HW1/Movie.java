package com.HW1;

import java.lang.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by stephen on 1/29/2016.
 */
public class Movie implements Comparable<Movie> {

    int movieID, dampedFactor = 5;
    Float totalRating, numRatings, stars, dampedRating;
    String title;
    List<Integer> viewers = new ArrayList<>();
    Float simpleSimilarity = 0.0f, advSimilarity = 0.0f, top = 0.0f, notSeen = 0.0f;

    public Movie(int id, String title){
        this.movieID = id;
        this.title = title;
        this.totalRating = 0.0f;
        this.numRatings = 0.0f;
        this.stars = 0.0f;
        this.dampedRating = 0.0f;
    }

    public int compareTo(Movie another) {
        return Float.compare(another.stars, this.stars);
    }

    public int dampedSort(Movie another) {
        return Float.compare(another.dampedRating, this.dampedRating);
    }

    public int simpleSort(Movie another) {
        return Float.compare(another.simpleSimilarity, this.simpleSimilarity);
    }

    public int advSort(Movie another) {
        return Float.compare(another.advSimilarity, this.advSimilarity);
    }

    public String toString(){
        return "ID: " + movieID + "Title: " + title + "Rating: " + stars;
    }
}