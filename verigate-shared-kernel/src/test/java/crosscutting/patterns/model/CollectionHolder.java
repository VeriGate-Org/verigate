/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class CollectionHolder {
    @DataContract
    private List<String> stringList;
    @DataContract
    private Set<Integer> integerSet;
    @DataContract
    private Map<String, String> stringMap;
    @DataContract
    private Queue<Double> doubleQueue;

    // Constructor
    public CollectionHolder(List<String> stringList, Set<Integer> integerSet, Map<String, String> stringMap, Queue<Double> doubleQueue) {
        this.stringList = stringList;
        this.integerSet = integerSet;
        this.stringMap = stringMap;
        this.doubleQueue = doubleQueue;
    }

    // Getters
    public List<String> getStringList() {
        return stringList;
    }

    public Set<Integer> getIntegerSet() {
        return integerSet;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public Queue<Double> getDoubleQueue() {
        return doubleQueue;
    }
}
