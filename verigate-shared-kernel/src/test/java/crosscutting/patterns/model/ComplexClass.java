package crosscutting.patterns.model;

/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

import crosscutting.serialization.DataContract;
import org.javamoney.moneta.Money;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplexClass extends BaseClass {
    @DataContract
    private String name;
    @DataContract
    private Money amount;
    @DataContract
    private NestedClass nestedObject;
    @DataContract
    private List<String> stringList;
    @DataContract
    private Map<String, Integer> map;
    @DataContract
    private Set<Double> doubleSet;
    private transient String transientField = "should not be serialized";

    public ComplexClass(int baseNumber, String name, Money amount, NestedClass nestedObject,
                        List<String> stringList, Map<String, Integer> map, Set<Double> doubleSet) {
        super(baseNumber);
        this.name = name;
        this.amount = amount;
        this.nestedObject = nestedObject;
        this.stringList = stringList;
        this.map = map;
        this.doubleSet = doubleSet;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Money getAmount() {
        return amount;
    }

    public NestedClass getNestedObject() {
        return nestedObject;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public Set<Double> getDoubleSet() {
        return doubleSet;
    }
}
