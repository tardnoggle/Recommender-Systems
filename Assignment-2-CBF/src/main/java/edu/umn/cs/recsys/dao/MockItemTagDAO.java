package edu.umn.cs.recsys.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A mock implementation of the item-tag DAO for testing.
 */
public class MockItemTagDAO implements ItemTagDAO {
    private final Map<Long, List<String>> itemTagMap;
    private final ImmutableSet<String> vocabulary;

    public MockItemTagDAO(Map<Long,List<String>> tags) {
        itemTagMap = ImmutableMap.copyOf(tags);
        ImmutableSet.Builder<String> vocab = ImmutableSet.builder();
        for (List<String> itl: tags.values()) {
            vocab.addAll(itl);
        }
        vocabulary = vocab.build();
    }

    @Override
    public List<String> getItemTags(long item) {
        List<String> tags = itemTagMap.get(item);
        if (tags == null) {
            return Collections.emptyList();
        } else {
            return tags;
        }
    }

    @Override
    public Set<String> getTagVocabulary() {
        return vocabulary;
    }
}
