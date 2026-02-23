/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

public final class Child extends Parent {
    @DataContract
    private String childProperty;

    public Child(String parentProperty, String childProperty) {
        super(parentProperty);
        this.childProperty = childProperty;
    }

    // Getter and Setter
    public String getChildProperty() {
        return childProperty;
    }

    public void setChildProperty(String childProperty) {
        this.childProperty = childProperty;
    }
}
