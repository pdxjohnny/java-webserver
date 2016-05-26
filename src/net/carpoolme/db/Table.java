package net.carpoolme.db;

import net.carpoolme.datastructures.DLL;
import net.carpoolme.datastructures.Tree23;
import net.carpoolme.utils.BasicParser;
import net.carpoolme.utils.JSONParser;

import java.io.InvalidObjectException;

/**
 * Created by John Andersen on 5/26/16.
 */
public class Table {
    // Stores all of our data
    private DLL<Object[][]> tableData = new DLL<Object[][]>();
    // The indexes for quick access to records
    private Tree23 searchIndexes = new Tree23();
    // Maintain these indexes on each record
    private DLL<String> maintainIndexes = new DLL<String>();

    // For parsing records
    private BasicParser parser = new BasicParser();

    public Table(final String[] indexes) throws IndexOutOfBoundsException {
        addIndexes(indexes);
    }

    public synchronized boolean addIndexes(final String[] indexes) throws IndexOutOfBoundsException {
        // Add the index to the tree of indexes that the table should have
        for (int i = indexes.length - 1; i >= 0; --i) {
            if (!searchIndexes.contains(indexes[i])) {
                try {
                    // Create a tree to store the index data
                    searchIndexes.add(indexes[i], new Tree23());
                    // Add to list of indexes to maintain
                    if (!maintainIndexes.contains(indexes[i])) {
                        maintainIndexes.add(indexes[i]);
                    }
                } catch (InvalidObjectException ignored) {
                    // Error adding
                    return false;
                }
            }
        }
        // Now add the indexes to the existing entries in the table
        Object[][] record;
        for (int i = 0; i < tableData.size(); ++i) {
            record = tableData.get(i);
            addIndexesToData(indexes, record);
        }
        return true;
    }

    private synchronized boolean addIndexesToData(final String[] indexes, final Object[][] record) {
        // Now add the indexes to the existing entries in the table
        Comparable fieldData;
        Tree23 index;
        for (int i = indexes.length - 1; i >= 0; --i) {
            // These always return a primitive type
            fieldData = (Comparable) parser.getKey(record, indexes[i]);
            if (fieldData == null) {
                continue;
            }
            index = (Tree23) searchIndexes.get(indexes[i]);
            try {
                index.add(fieldData, record);
            } catch (InvalidObjectException ignored) {
                // Somehow record was null, that is bad
                return false;
            }
        }
        return true;
    }

    public synchronized boolean add(final Object[][] addData) throws IndexOutOfBoundsException {
        System.out.println("DEBG: Inserting " + new JSONParser().toString(addData));
        String[] allIndexes = maintainIndexes.toArray(new String[maintainIndexes.size()]);
        return addIndexesToData(allIndexes, addData) && tableData.add(addData);
    }

    public synchronized Object get(final int index) throws IndexOutOfBoundsException {
        return tableData.get(index);
    }

    public Object[][] rawSearch(final String searchKey) {
        Object[][] record;
        String fieldData;
        for (int i = 0; i < tableData.size(); ++i) {
            record = (Object[][]) tableData.get(i);
            fieldData = (String) parser.getKey(record, searchKey);
            if (fieldData != null) {
                return record;
            }
        }
        // Did not find the data
        throw new IndexOutOfBoundsException();
    }

    // Get with index fallback to raw search
    public synchronized Object[][] get(final Comparable searchIndex, final String searchKey) throws IndexOutOfBoundsException {
        // Retrieve by index lookup
        Tree23 index = (Tree23) searchIndexes.get(searchIndex);
        try {
            return (Object[][]) index.get(searchKey);
        } catch (IndexOutOfBoundsException ignored) {}
        // Retrieve by dataTable search
        return rawSearch(searchKey);
    }

    // Try each index fallback to raw search
    public synchronized Object[][] get(final String searchKey) throws IndexOutOfBoundsException {
        // Retrieve by index lookup
        Tree23 index;
        for (int i = 0; i < searchIndexes.size(); ++i) {
            try {
                index = (Tree23) searchIndexes.value(i);
                return (Object[][]) index.get(searchKey);
            } catch (IndexOutOfBoundsException ignored) {}
        }
        return rawSearch(searchKey);
    }
}