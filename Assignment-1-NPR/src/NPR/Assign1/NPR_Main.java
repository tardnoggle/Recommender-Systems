package NPR.Assign1;

import static java.lang.System.out;
import java.io.*;
import java.util.*;

/**
 * Created by noggleBase on 3/17/2016.
 */
public class NPR_Main {

    public static void main(String[] args){


        Map<Integer, NPR_Movie> movies = new HashMap();
        String inMovies = "ml-latest-small/movies.csv", inRatings = "ml-latest-small/ratings.csv", output = "results.csv";
        String line_1, line_2;
        BufferedReader br1 = null, br2;
        int userId, movieId, numRating = 0;
        Float rating;
        BufferedWriter bw;
        List<Integer> userList = new ArrayList<>();

        try {
            br1 = new BufferedReader(new FileReader(inMovies));
            br1.readLine();
            br2 = new BufferedReader(new FileReader(inRatings));
            br2.readLine();

            while ((line_1 = br1.readLine()) != null) { //reads line in from file

                String[] information = line_1.split(","); //separates values by "," into array
                movieId = Integer.valueOf(information[0]);
                movies.put(movieId, new NPR_Movie(movieId, information[1]));

            }

            while ((line_2 = br2.readLine()) != null) {

                String[] information2 = line_2.split(","); //separates values by "," into array
                userId = Integer.valueOf(information2[0]);
                movieId = Integer.valueOf(information2[1]);
                rating = Float.valueOf(information2[2]);

                if(!userList.contains(userId))
                    userList.add(userId);
                NPR_Movie item = movies.get(movieId); //gets movie by ID num
                item.totalRating += rating;
                item.numRatings += 1.0f;
                item.stars = item.totalRating / item.numRatings;

                if (item.stars != null) {
                    item.dampedRating = (item.totalRating + 3 * item.dampedFactor)
                            / (item.numRatings + item.dampedFactor);
                }
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
        Float globalMean, totalRate = 0.0f;

        for (Map.Entry<Integer, NPR_Movie> item : movies.entrySet()) {
            totalRate += item.getValue().stars;
            numRating++;
        }

        // calculation for global mean
        globalMean = totalRate / numRating;

        // damped mean
        for (Map.Entry<Integer, NPR_Movie> item : movies.entrySet()) {
            item.getValue().dampedRating = (item.getValue().totalRating + (globalMean * item.getValue().dampedFactor))
                    / (item.getValue().numRatings + item.getValue().dampedFactor);
        }


        for(Map.Entry<Integer, NPR_Movie> entry : movies.entrySet()){
            for(Integer user : userList){
                if(movies.get(260).users.contains(user) && entry.getValue().users.contains(user))
                    entry.getValue().topRated++;
            }
            entry.getValue().similarity = entry.getValue().topRated / movies.get(260).users.size();
        }

        for(Map.Entry<Integer, NPR_Movie> entry : movies.entrySet()){
            for(Integer user : userList){
                if(entry.getValue().users.size() > 10) {
                    if ((!movies.get(153).users.contains(user)) && (entry.getValue().users.contains(user)))
                        entry.getValue().notSeen++;
                }
            }
            entry.getValue().similarityADV = entry.getValue().similarity/(entry.getValue().notSeen/(userList.size()-movies.get(153).users.size()));
            if(Float.isNaN(entry.getValue().similarityADV))
                entry.getValue().similarityADV = 0.0f;
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

        out.println(userList.size());
        out.println(movies.get(1172).dampedRating);
        out.println(movies.get(480).similarity);
        out.println(movies.get(257).similarityADV);
        try {
            bw = new BufferedWriter(new FileWriter(output));
            for(int w = 0; w < 4; w++) {
                if (w == 0){
                    bw.write("TOP MOVIES BY MEAN");
                    bw.newLine();
                    bw.flush();
                    for (int i = 0; i < 10; i++) {
                        bw.write(topRated.get(i).movieID + ", " + topRated.get(i).title + ", " + topRated.get(i).stars);
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 1){
                    bw.write("TOP MOVIES BY DAMPED MEAN");
                    bw.newLine();
                    bw.flush();
                    for (int i = 0; i < 10; i++) {
                        bw.write(topDamped.get(i).movieID + ", " + topDamped.get(i).title + ", " + topDamped.get(i).dampedRating);
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 2){
                    bw.write("TOP MOVIES FOR Star Wars (Simple)");
                    bw.newLine();
                    bw.flush();
                    for (int i = 1; i < 11; i++) { //skips the movie itself
                        bw.write(topSimple.get(i).movieID + ", " + topSimple.get(i).title + ", " + topSimple.get(i).similarity);
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 3){
                    bw.write("TOP MOVIES FOR Batman Forever (Advanced)");
                    bw.newLine();
                    bw.flush();
                    for (int i = 1; i < 11; i++) { //skips the movie itself
                        bw.write(topAdvanced.get(i).movieID + ", " + topAdvanced.get(i).title + ", " + topAdvanced.get(i).similarityADV);
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