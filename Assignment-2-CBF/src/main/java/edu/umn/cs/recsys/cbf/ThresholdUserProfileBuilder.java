package edu.umn.cs.recsys.cbf;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import org.lenskit.data.ratings.Rating;
import org.lenskit.data.history.UserHistory;
import org.lenskit.util.collections.LongUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class ThresholdUserProfileBuilder implements UserProfileBuilder {
    /**
     * The lowest rating that will be considered in the user's profile.
     */
    private static final double RATING_THRESHOLD = 3.5;

    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public ThresholdUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Long2DoubleMap makeUserProfile(@Nonnull UserHistory<Rating> history) {
        // Create a new vector over tags to accumulate the user profile
        Long2DoubleOpenHashMap profile = new Long2DoubleOpenHashMap();

        // Iterate over the user's ratings to build their profile
        for (Rating r: history) {
            // In LensKit, ratings can have null values to express the user
            // unrating an item
            if (r.hasValue() && r.getValue() >= RATING_THRESHOLD) {
                // TODO Get this item's vector and add it to the user's profile

                for (Map.Entry<Long, Double> entry : model.getItemVector(r.getItemId()).entrySet()) {
                    Double itemVector = entry.getValue();
                    if (profile.containsKey(entry.getKey())) {
                        itemVector += profile.get(entry.getKey());
                    }
                    profile.put(entry.getKey(), itemVector);
                }
            }
        }

        // The profile is accumulated, return it.
        // It is good practice to return a frozen vector.
        return LongUtils.frozenMap(profile);
    }
}
