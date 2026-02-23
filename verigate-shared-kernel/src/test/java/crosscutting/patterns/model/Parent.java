/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

class Parent {
    @DataContract
    protected String parentProperty;

    public Parent(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    // Getter and Setter
    public String getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }
}
