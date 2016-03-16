package edu.umn.cs.recsys.cbf;

import com.google.common.collect.ImmutableList;
import edu.umn.cs.recsys.dao.ItemTagDAO;
import edu.umn.cs.recsys.dao.MockItemTagDAO;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import org.junit.Before;
import org.junit.Test;
import org.lenskit.data.dao.ItemDAO;
import org.lenskit.data.dao.ItemListItemDAO;
import org.lenskit.util.collections.LongUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Basic tests for the TFIDF model builder.  These do not test value correctness, only that the
 * right tags are present.
 */
public class TFIDFModelBuilderTest {
    private TFIDFModel model;

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
    }

    @Test
    public void testTags() {
        assertThat(model, notNullValue());
        // test that the tags are there
        assertThat(model.getTagIds().keySet(),
                   containsInAnyOrder("walrus", "hamster", "jubjub bird"));
        // test that the IDs are reasonable
        assertThat(model.getTagIds().values(),
                   containsInAnyOrder(1L, 2L, 3L));
    }

    @Test
    public void testItemOne() {
        // start checking vectors
        Map<String,Long> tags = model.getTagIds();
        // Vector for item 1 should just have 'walrus' tag
        Long2DoubleMap v1 = model.getItemVector(1);
        assertThat(v1.keySet(), contains(tags.get("walrus")));
    }

    @Test
    public void testItemTwo() {
        Map<String,Long> tags = model.getTagIds();
        // Vector for item 2 should have 2 tags
        Long2DoubleMap v2 = model.getItemVector(2);
        assertThat(v2.keySet(),
                   containsInAnyOrder(tags.get("walrus"), tags.get("hamster")));
    }

    @Test
    public void testItemThree() {
        Map<String,Long> tags = model.getTagIds();
        // Vector for item 3 should have 1 tag again
        Long2DoubleMap v3 = model.getItemVector(3);
        assertThat(v3.keySet(),
                   contains(tags.get("jubjub bird")));
    }
}
