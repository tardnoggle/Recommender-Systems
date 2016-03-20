package NPR.Assign1;

import static java.lang.System.out;
import java.io.*;
import java.math.RoundingMode;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.*;

/*********************************
 * Author: Corbin Gault          *
 * Date: 3/17/2016               *
 * ID: A00502826                 *
 * NetID: cg1259                 *
 * Professor: Ekstrand           *
 * Class: Recommender Systems    *
 * *******************************
 *
 ****************
 * Instructions *
 ****************
 *
 ************
 * Overview *
 ************
 *
 * This assignment is to acquaint you with working with ratings data, and with non-personalized recommendation.
 *
 * For this assignment, you may use any programming language you wish.
 *
 *************
 * Resources *
 *************
 *
 * You will use the data files attached to the assignment:
 *
 * ratings.csv contains user ratings of movies, one per line, in the format (user, movie, rating, timestamp). The first line is a header.
 * movies.csv contains the titles of the movies. The first line is a header.
 *
 *****************
 * Requirements: *
 *****************
 *
 * Write code to produce the following:
 *
 * An item's mean rating.
 *
 * An item's damped mean, with a damping term of 5.
 *
 * The similarity of one movie to another based on how likely the user is to rate one given that they rated the other
 * (ignoring the rating values), using the simple ((x ∧ y)/x) method.
 * That is, count the number of users who rated both movies x and y, and divide it by the number of users who rated x.
 *
 * The similarity of one movie to another, using the advanced (((x ∧ y)/x)/((!x ∧ y)/!x)) method.
 *
 * The top movies by each of these scores.
 *
 **********
 * Submit *
 **********
 *
 * Submit a zip file containing your code and a text file called results.txt containing the following:
 *
 * The top 10 movies by mean rating, with their mean.
 * The top 10 movies by damped mean rating, with their damped mean.
 * The 10 movies most similar to movie 260 (Star Wars) using the simple metric, with their similarity scores.
 * The 10 movies most similar to movie 153 (Batman Forever) using the advanced metric, with their similarity scores.
 * The file should look like this:
 *
 * TOP MOVIES BY MEAN
 * MovieID  mean
 * MovieID  mean
 *
 * TOP MOVIES BY DAMPED MEAN
 * MovieID  mean
 * MovieID  mean
 *
 * TOP MOVIES FOR Star Wars (SIMPLE)
 * MovieID  score
 * MovieID  score
 *
 * TOP MOVIES FOR Batman Forever (ADVANCED)
 * MovieID  score
 * MovieID  score
 */
public class NPR_Main {

    public static void main(String[] args){

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        double totalRatingTest = 0.0;
        double numRatingTest = 0.0;

        Map<Integer, NPR_Movie> movies = new HashMap();
        String inMovies = "ml-latest-small/movies.csv", inRatings = "ml-latest-small/ratings.csv",
                output = "results.txt";
        String line_1, line_2;
        BufferedReader br1 = null, br2;
        int userId, movieId, numRating = 0;
        List<Integer> movieKey = new ArrayList<>();
        Double rating;
        BufferedWriter bw;
        List<Integer> userList = new ArrayList<>();

        try {
            br1 = new BufferedReader(new FileReader(inMovies));
            br1.readLine();
            br2 = new BufferedReader(new FileReader(inRatings));
            br2.readLine();

            while ((line_1 = br1.readLine()) != null) { // reads line in from file as long as there in info

                String[] movieInfo = line_1.split(","); // separates values if a comma is read in
                movieId = Integer.valueOf(movieInfo[0]);
                movies.put(movieId, new NPR_Movie(movieId, movieInfo[1]));

                // storing movie key for similarity
                if (!movieKey.contains(movieId)) {

                    movieKey.add(movieId);
                }

            }

            while ((line_2 = br2.readLine()) != null) {

                String[] userInfo = line_2.split(","); // separates values if a comma is read in
                userId = Integer.valueOf(userInfo[0]);
                movieId = Integer.valueOf(userInfo[1]);
                rating = Double.valueOf(userInfo[2]);
                totalRatingTest = totalRatingTest + rating;
                numRatingTest += 1.0;
                if(!userList.contains(userId)){
                    userList.add(userId);
                }

                NPR_Movie item = movies.get(movieId); // gets the movie id

                item.totalRating += rating;
                item.numRatings += 1.0;
                //item.movRating = item.totalRating / item.numRatings;
                item.movRating = rating;

                //if (item.movRating != null) {
                //    item.dampedRating = (item.totalRating + 3 * item.dampedFactor)
                //            / (item.numRatings + item.dampedFactor);
                //}
                if (!item.users.contains(userId)) item.users.add(userId);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br1 != null) {
                try {
                    br1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // initializing global mean and total number of ratings
        Double globalMean, totalRate = 0.0, globalMeanTest;

        for (Map.Entry<Integer, NPR_Movie> item : movies.entrySet()) {
            totalRate += item.getValue().movRating;
            numRating++;
        }

        // calculation for global mean
        globalMean = totalRate / numRating;

        // correct calculation for global mean
        globalMeanTest = totalRatingTest / numRatingTest;

        // calculating the damped mean
        for (Map.Entry<Integer, NPR_Movie> item : movies.entrySet()) {
            item.getValue().dampedRating = (item.getValue().totalRating + (globalMeanTest * item.getValue().dampedFactor));
            item.getValue().dampedRating = item.getValue().dampedRating / (item.getValue().numRatings
                    + item.getValue().dampedFactor);
        }
          // calculation for damped mean
//        for (Map.Entry<Integer, NPR_Movie> item : movies.entrySet()) {
//            item.getValue().dampedRating = (item.getValue().totalRating + (globalMean * item.getValue().dampedFactor));
//            item.getValue().dampedRating = item.getValue().dampedRating / (item.getValue().numRatings
//                    + item.getValue().dampedFactor);
//      }

        // calculating global simple similarity
        for(Map.Entry<Integer, NPR_Movie> entry : movies.entrySet()) {
            for (Integer user : userList) {

                for(int i=0; i < movieKey.size(); i++){

                    int movieKey1 = movieKey.get(i);

                    if (movies.get(movieKey1).users.contains(user) && entry.getValue().users.contains(user))
                        entry.getValue().usrTotal++;

                    entry.getValue().similarity = entry.getValue().usrTotal / movies.get(movieKey1).users.size();
                }


            }


        }







        // calculation for simple similarity to movie 260 (Starwars)
        //for(Map.Entry<Integer, NPR_Movie> entry : movies.entrySet()){
        //    for(Integer user : userList){
        //        if(movies.get(260).users.contains(user) && entry.getValue().users.contains(user))
        //           entry.getValue().topRated++;
        //    }
        //   entry.getValue().similarity = entry.getValue().topRated / movies.get(260).users.size();
        //}


        for(Map.Entry<Integer, NPR_Movie> entry : movies.entrySet()){
            for(Integer user : userList){
                if(entry.getValue().users.size() > 10) {
                    if ((!movies.get(153).users.contains(user)) && (entry.getValue().users.contains(user)))
                        entry.getValue().notSeen++;
                }
            }

            entry.getValue().similarityADV = entry.getValue().similarity/(1-entry.getValue().similarity/(userList.size()-movies.get(153).users.size()));
            if(Double.isNaN(entry.getValue().similarityADV))
                entry.getValue().similarityADV = 0.0;
//            entry.getValue().similarityADV = entry.getValue().similarity/(entry.getValue().notSeen/(userList.size()-movies.get(153).users.size()));
//            if(Double.isNaN(entry.getValue().similarityADV))
//                entry.getValue().similarityADV = 0.0;
        }

        List<NPR_Movie> movieList = new ArrayList<>(movies.values());
        List<NPR_Movie> topRated = new ArrayList<>();
        List<NPR_Movie> topDamped = new ArrayList<>();
        List<NPR_Movie> topSimple = new ArrayList<>();
        List<NPR_Movie> topAdvanced = new ArrayList<>();
        Collections.sort(movieList);
        topRated.addAll(movieList);
        Collections.sort(movieList, NPR_Movie::dampedSort);
        topDamped.addAll(movieList);
        Collections.sort(movieList, NPR_Movie::simpleSort);
        topSimple.addAll(movieList);
        Collections.sort(movieList, NPR_Movie::advSort);
        topAdvanced.addAll(movieList);




        for(int i=0, j=1; i< movieList.size()+1; i++, j++){




        }









        out.println(userList.size());
        out.println("Global Mean: " + df.format(globalMean));
        out.println("Global Mean Test: " + df.format(globalMeanTest));
        out.println("Total Rating: " + df.format(totalRate));
        out.println("Total Rating Test: " + df.format(totalRatingTest));
        out.println("Number of Ratings: " + df.format(numRating));
        out.println("Number of RatingsTest: " + df.format(numRatingTest));
        out.println("Movie 1172 Damped " + df.format(movies.get(1172).dampedRating));
        out.println(df.format(movies.get(480).similarity));
        out.println(df.format(movies.get(257).similarityADV));

        try {
            bw = new BufferedWriter(new FileWriter(output));
            for(int w = 0; w < 4; w++) {
                if (w == 0){
                    bw.write("TOP MOVIES BY MEAN");
                    bw.newLine();
                    bw.flush();
                    for (int i = 0; i < 10; i++) {
                        bw.write(topRated.get(i).movieID + ", " + topRated.get(i).title + ", "
                                + topRated.get(i).movRating);
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 1){
                    bw.write("TOP MOVIES BY DAMPED MEAN");
                    bw.newLine();
                    bw.flush();
                    for (int i = 0; i < 10; i++) {
                        bw.write(topDamped.get(i).movieID + ", " + topDamped.get(i).title + ", "
                                + df.format(topDamped.get(i).dampedRating));
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 2){
                    bw.write("TOP MOVIES FOR Star Wars (Simple)");
                    bw.newLine();
                    bw.flush();
                    for (int i = 1; i < 11; i++) { //skips the movie itself
                        bw.write(topSimple.get(i).movieID + ", " + topSimple.get(i).title + ", "
                                + df.format(topSimple.get(i).similarity));
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 3){
                    bw.write("TOP MOVIES FOR Batman Forever (Advanced)");
                    bw.newLine();
                    bw.flush();
                    for (int i = 1; i < 11; i++) { //skips the movie itself
                        bw.write(topAdvanced.get(i).movieID + ", " + topAdvanced.get(i).title + ", "
                                + df.format(topAdvanced.get(i).similarityADV));
                        bw.newLine();
                        bw.flush();

                    }
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}