package com.devoxx.data.cache;

import com.annimon.stream.Optional;

public interface QueryAwareRawCache {

    void upsert(String rawData, String query);

    Optional<String> getData(String query);

    boolean isValid(String query, long timestamp);

    void clearCache(String query);
}
