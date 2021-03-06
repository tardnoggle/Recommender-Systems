package edu.umn.cs.recsys.cbf;

import com.google.common.collect.Maps;
import edu.umn.cs.recsys.dao.ItemTagDAO;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.lenskit.data.dao.ItemDAO;
import org.lenskit.inject.Transient;
import org.lenskit.util.collections.LongUtils;
import org.lenskit.util.math.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lenskit.util.math.Vectors.euclideanNorm;

/**
 * Builder for computing {@linkplain TFIDFModel TF-IDF models} from item tag data.  Each item is
 * represented by a normalized TF-IDF vector.
 *
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class TFIDFModelBuilder implements Provider<TFIDFModel> {
    private static final Logger logger = LoggerFactory.getLogger(TFIDFModelBuilder.class);

    private final ItemTagDAO itemTagDAO;
    private final ItemDAO itemDAO;

    /**
     * Construct a model builder.  The {@link Inject} annotation on this constructor tells LensKit
     * that it can be used to build the model builder.
     *
     * @param itdao The item-tag DAO.  This is where the builder will get access to item tags.
     * @param idao The item DAO. This is where the builder will get access to items.
     */
    @Inject
    public TFIDFModelBuilder(@Transient ItemTagDAO itdao,
                             @Transient ItemDAO idao) {
        this.itemTagDAO = itdao;
        this.itemDAO = idao;
    }

    /**
     * This method is where the model should actually be computed.
     * @return The TF-IDF model (a model of item tag vectors).
     */
    @Override
    public TFIDFModel get() {
        // Build a map of tags to numeric IDs.  This lets you convert tags (which are strings)
        // into long IDs that you can use as keys in a tag vector.

        Map<String, Long> tagIds = buildTagIdMap();
        logger.info("Building model for {} tags", tagIds.size());


        // Create a map to accumulate document frequencies for the IDF computation
        Long2DoubleMap docFreq = new Long2DoubleOpenHashMap();

        // We now proceed in 2 stages. First, we build a TF vector for each item.
        // While we do this, we also build the DF vector.
        // We will then apply the IDF to each TF vector and normalize it to a unit vector.

        // Create a map to store the item TF vectors.
        Map<Long,Long2DoubleMap> itemVectors = new HashMap<>();


        // Iterate over the items to compute each item's vector.
        LongSet items = itemDAO.getItemIds();
        for (long item: items) {
            // Create a work vector to accumulate this item's tag vector.
            Long2DoubleMap work = new Long2DoubleOpenHashMap();

            // TODO Populate the work vector with the number of times each tag is applied to this item.
            work.clear();
            // work vector is empty

            for (String tag : itemTagDAO.getItemTags(item)){
                long tagID = tagIds.get(tag);

                if(!work.containsKey(tagID))
                    work.put(tagID, 0);

                work.put(tagID,1);
            }

            // TODO Increment the document frequency vector once for each unique tag on the item.

            Set<String> itemTags = new HashSet<String>(itemTagDAO.getItemTags(item));
            for (String tag : itemTags){
                long tagID = tagIds.get(tag);
                docFreq.put(tagID, 1);
            }

            // Save the vector in our map, we'll add IDF and normalize later.
            itemVectors.put(item, work);
        }

        // Now we've seen all the items, so we have each item's TF vector and a global vector
        // of document frequencies.
        // Invert and log the document frequency.  We can do this in-place.
        final double logN = Math.log(items.size());
        for (Map.Entry<Long,Double> e: docFreq.entrySet()) {
            docFreq.put((long)e.getKey(), logN - Math.log(e.getValue()));
        }

        // Now docFreq is a log-IDF vector.
        // So we can use it to apply IDF to each item vector to put it in the final model.
        // Create a map to store the final model data.
        Map<Long,Long2DoubleMap> modelData = new HashMap<>();
        for (Map.Entry<Long,Long2DoubleMap> entry: itemVectors.entrySet()) {
            Long2DoubleMap tv = new Long2DoubleOpenHashMap(entry.getValue());

            // TODO Convert this map (vector) to a TF-IDF vector
            LongSet tags = entry.getValue().keySet();

            for (Long tag : tags){
                Double TFIDF = tv.get(tag) * docFreq.get(tag);
                tv.put(tag, TFIDF);
            }


            // TODO Normalize the TF-IDF vector to be a unit vector
            // HINT Take a look at the Vectors class in org.lenskit.util.math.

            double eNorm = euclideanNorm(tv);

            // Store a packed version of the vector in the model data.
            modelData.put(entry.getKey(), LongUtils.frozenMap(tv));
        }

        // We don't need the IDF vector anymore, as long as as we have no new tags
        return new TFIDFModel(tagIds, modelData);
    }

    /**
     * Build a mapping of tags to numeric IDs.
     *
     * @return A mapping from tags to IDs.
     */
    private Map<String,Long> buildTagIdMap() {
        // Get the universe of all tags
        Set<String> tags = itemTagDAO.getTagVocabulary();
        // Allocate our new tag map
        Map<String,Long> tagIds = Maps.newHashMap();

        for (String tag: tags) {
            // Map each tag to a new number.
            tagIds.put(tag, tagIds.size() + 1L);
        }
        return tagIds;
    }
}
