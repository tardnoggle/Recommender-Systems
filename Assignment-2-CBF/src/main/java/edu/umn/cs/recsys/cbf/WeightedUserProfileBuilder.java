package edu.umn.cs.recsys.cbf;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import org.lenskit.data.history.UserHistory;
import org.lenskit.data.ratings.Rating;
import org.lenskit.data.ratings.Ratings;
import org.lenskit.util.collections.LongUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class WeightedUserProfileBuilder implements UserProfileBuilder {
    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public WeightedUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Long2DoubleMap makeUserProfile(@Nonnull UserHistory<Rating> history) {
        // Create a new vector over tags to accumulate the user profile
        Long2DoubleMap profile = new Long2DoubleOpenHashMap();

        // TODO Normalize the user's ratings
        double mean = 0;
        int count = 0;
        for (Rating r: history) {
            if (r.hasValue()) {
                mean += r.getValue();
                ++count;
            }
        }
        mean = mean / count;

        // TODO Build the user's weighted profile

        for (Rating r: history) {
            if (r.hasValue()) {
                for (Map.Entry<Long, Double> entry : model.getItemVector(r.getItemId()).entrySet()) {
                    Double newValue = entry.getValue() * (r.getValue() - mean);
                    if (profile.containsKey(entry.getKey())) {
                        newValue += profile.get(entry.getKey());
                    }
                    profile.put(entry.getKey(), newValue);
                }
            }
        }
        // The profile is accumulated, return it.
        // It is good practice to return a frozen vector.
        return LongUtils.frozenMap(profile);
    }
}
