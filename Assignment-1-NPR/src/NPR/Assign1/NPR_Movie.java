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
    Double dampedFactor = 5.0;
    Double totalRating = 0.0, numRatings, movRating, dampedRating;
    String title;
    List<Integer> users = new ArrayList<>();
    Double similarity = 0.0, similarityADV = 0.0, topRated = 0.0, notSeen = 0.0, usrTotal = 0.0;

    public NPR_Movie(int id, String title) {
        this.movieID = id;
        this.title = title;
        this.totalRating = 0.0;
        this.numRatings = 0.0;
        this.movRating = 0.0;
        this.dampedRating = 0.0;

    }

    public int compareTo(NPR_Movie another) {
        return Double.compare(another.movRating, this.movRating);
    }

    public int dampedSort(NPR_Movie another) {
        return Double.compare(another.dampedRating, this.dampedRating);
    }

    public int simpleSort(NPR_Movie another) {
        return Double.compare(another.similarity, this.similarity);
    }

    public int advSort(NPR_Movie another) {
        return Double.compare(another.similarityADV, this.similarityADV);
    }

    public String toString(){
        return "ID: " + movieID + "Title: " + title + "Rating: " + movRating;
    }
}