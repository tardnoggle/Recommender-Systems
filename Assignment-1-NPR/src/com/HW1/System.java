package com.HW1;

import static java.lang.System.out;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


/**
 * Created by stephen on 1/29/2016.
 */
public class System {

    public static void main(String[] args) {

        Map<Integer, Movie> movies = new HashMap();
        String fileMovies = "ml-latest-small/movies.csv", fileRatings = "ml-latest-small/ratings.csv", output = "results.csv";
        String line, line2;
        BufferedReader br = null, br2;
        int userIdNum, movieIdNum, numRating = 0;
        Float rating;
        BufferedWriter bw;
        List<Integer> userList = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(fileMovies));
            br.readLine();
            br2 = new BufferedReader(new FileReader(fileRatings));
            br2.readLine();

            while ((line = br.readLine()) != null) { //reads line in from file
                String[] information = line.split(","); //separates values by "," into array
                movieIdNum = Integer.valueOf(information[0]);
                movies.put(movieIdNum, new Movie(movieIdNum, information[1]));
            }
            while ((line2 = br2.readLine()) != null) {
                String[] information2 = line2.split(","); //separates values by "," into array
                userIdNum = Integer.valueOf(information2[0]);
                movieIdNum = Integer.valueOf(information2[1]);
                rating = Float.valueOf(information2[2]);
                if(!userList.contains(userIdNum))
                    userList.add(userIdNum);
                Movie item = movies.get(movieIdNum); //gets movie by ID num
                item.totalRating += rating;
                item.numRatings += 1.0f;
                item.stars = item.totalRating / item.numRatings;
                if (item.stars != null) {
                    item.dampedRating = (item.totalRating + 3 * item.dampedFactor)
                            / (item.numRatings + item.dampedFactor);
                }
                if (!item.viewers.contains(userIdNum)) item.viewers.add(userIdNum);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Float globalMean, totalRate = 0.0f;

        for (Map.Entry<Integer, Movie> item : movies.entrySet()) {
            totalRate += item.getValue().stars;
            numRating++;
        }

        globalMean = totalRate / numRating;

        for (Map.Entry<Integer, Movie> item : movies.entrySet()) {
            item.getValue().dampedRating = (item.getValue().totalRating + (globalMean * item.getValue().dampedFactor))
                    / (item.getValue().numRatings + item.getValue().dampedFactor);
        }


        for(Map.Entry<Integer, Movie> entry : movies.entrySet()){
            for(Integer user : userList){
                if(movies.get(260).viewers.contains(user) && entry.getValue().viewers.contains(user))
                    entry.getValue().top++;
            }
            entry.getValue().simpleSimilarity = entry.getValue().top / movies.get(260).viewers.size();
        }

        for(Map.Entry<Integer, Movie> entry : movies.entrySet()){
            for(Integer user : userList){
                if(entry.getValue().viewers.size() > 10) {
                    if ((!movies.get(153).viewers.contains(user)) && (entry.getValue().viewers.contains(user)))
                        entry.getValue().notSeen++;
                }
            }
            entry.getValue().advSimilarity = entry.getValue().simpleSimilarity/(entry.getValue().notSeen/(userList.size()-movies.get(153).viewers.size()));
            if(Float.isNaN(entry.getValue().advSimilarity))
                entry.getValue().advSimilarity = 0.0f;
        }

        List<Movie> movieList = new ArrayList<>(movies.values());
        List<Movie> topRated = new ArrayList<>();
        List<Movie> topDamped = new ArrayList<>();
        List<Movie> topSimple = new ArrayList<>();
        List<Movie> topAdvanced = new ArrayList<>();
        Collections.sort(movieList);
        topRated.addAll(movieList);
        Collections.sort(movieList, Movie::dampedSort);
        topDamped.addAll(movieList);
        Collections.sort(movieList, Movie::simpleSort);
        topSimple.addAll(movieList);
        Collections.sort(movieList, Movie::advSort);
        topAdvanced.addAll(movieList);

        out.println(userList.size());
        out.println(movies.get(1172).dampedRating);
        out.println(movies.get(480).simpleSimilarity);
        out.println(movies.get(257).advSimilarity);
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
                        bw.write(topSimple.get(i).movieID + ", " + topSimple.get(i).title + ", " + topSimple.get(i).simpleSimilarity);
                        bw.newLine();
                        bw.flush();

                    }
                }
                if (w == 3){
                    bw.write("TOP MOVIES FOR Batman Forever (Advanced)");
                    bw.newLine();
                    bw.flush();
                    for (int i = 1; i < 11; i++) { //skips the movie itself
                        bw.write(topAdvanced.get(i).movieID + ", " + topAdvanced.get(i).title + ", " + topAdvanced.get(i).advSimilarity);
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

