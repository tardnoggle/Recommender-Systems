package NPR.Assign1;


import java.lang.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by noggleBase on 3/17/2016.
 */
public class NPR_Movie implements Comparable<NPR_Movie> {

    int movieID;//, dampedFactor = 5;
    Float dampedFactor = 5.0f;
    Float totalRating, numRatings, stars, dampedRating;
    String title;
    List<Integer> users = new ArrayList<>();
    Float similarity = 0.0f, similarityADV = 0.0f, topRated = 0.0f, notSeen = 0.0f;

    public NPR_Movie(int id, String title) {
        this.movieID = id;
        this.title = title;
        this.totalRating = 0.0f;
        this.numRatings = 0.0f;
        this.stars = 0.0f;
        this.dampedRating = 0.0f;

    }

    public int compareTo(NPR_Movie another) {
        return Float.compare(another.stars, this.stars);
    }

    public int dampedSort(NPR_Movie another) {
        return Float.compare(another.dampedRating, this.dampedRating);
    }

    public int simpleSort(NPR_Movie another) {
        return Float.compare(another.similarity, this.similarity);
    }

    public int advSort(NPR_Movie another) {
        return Float.compare(another.similarityADV, this.similarityADV);
    }

    public String toString(){
        return "ID: " + movieID + "Title: " + title + "Rating: " + stars;
    }
}