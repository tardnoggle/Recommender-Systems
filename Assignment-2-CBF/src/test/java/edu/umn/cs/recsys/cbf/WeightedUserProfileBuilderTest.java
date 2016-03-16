package edu.umn.cs.recsys.cbf;

import com.google.common.collect.ImmutableList;
import edu.umn.cs.recsys.dao.ItemTagDAO;
import edu.umn.cs.recsys.dao.MockItemTagDAO;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import org.junit.Before;
import org.junit.Test;
import org.lenskit.data.dao.ItemDAO;
import org.lenskit.data.dao.ItemListItemDAO;
import org.lenskit.data.history.History;
import org.lenskit.data.history.UserHistory;
import org.lenskit.data.ratings.Rating;
import org.lenskit.util.collections.LongUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class WeightedUserProfileBuilderTest {
    private TFIDFModel model;
    private WeightedUserProfileBuilder profileBuilder;

    @Before
    public void buildModel() {
        ItemDAO items = new ItemListItemDAO(LongUtils.packedSet(1, 2, 3));
        Map<Long,List<String>> itemTags = new HashMap<>();
        itemTags.put(1L, ImmutableList.of("walrus"));
        itemTags.put(2L, ImmutableList.of("hamster", "walrus"));
        itemTags.put(3L, ImmutableList.of("jubjub bird"));
        ItemTagDAO tagDAO = new MockItemTagDAO(itemTags);
        TFIDFModelBuilder mb = new TFIDFModelBuilder(tagDAO, items);
        model = mb.get();
        profileBuilder = new WeightedUserProfileBuilder(model);
    }

    @Test
    public void testEmptyModel() throws Exception {
        UserHistory<Rating> empty = History.forUser(42);
        Long2DoubleMap vector = profileBuilder.makeUserProfile(empty);
        assertThat(vector.size(), equalTo(0));
    }

    @Test
    public void testSingleItemVector() throws Exception {
        UserHistory<Rating> empty = History.forUser(42, Rating.create(42, 1, 4.0));
        Long2DoubleMap vector = profileBuilder.makeUserProfile(empty);
        // item 1 only has 1 tag
        assertThat(vector.size(), equalTo(1));
    }

    @Test
    public void testTwoItemVector() throws Exception {
        UserHistory<Rating> empty = History.forUser(42, Rating.create(42, 1, 4.0),
                                                    Rating.create(42, 2, 3.0));
        Long2DoubleMap vector = profileBuilder.makeUserProfile(empty);
        // two tags!
        assertThat(vector.size(), equalTo(2));
    }
}
