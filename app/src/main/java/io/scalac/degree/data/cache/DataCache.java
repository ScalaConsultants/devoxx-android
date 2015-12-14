package io.scalac.degree.data.cache;

/**
 * Jacek Modrakowski
 * modrakowski.pl
 * 30/10/2015.
 */
public interface DataCache<DataType, StorageType> {

    void upsert(StorageType rawData, String query);

    DataType getData();

    DataType getData(String query);

    boolean isValid();

    boolean isValid(String query);

    void clearCache();
}
