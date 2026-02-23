/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

public final class Address {
    @DataContract
    private String street;
    @DataContract
    private String city;

    // Constructor
    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    // Getters
    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
