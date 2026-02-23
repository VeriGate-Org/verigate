/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

public class Person {
    @DataContract
    private Name name;
    @DataContract
    private Address address;

    // Constructor
    public Person(Name name, Address address) {
        this.name = name;
        this.address = address;
    }

    // Getters
    public Name getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }
}
