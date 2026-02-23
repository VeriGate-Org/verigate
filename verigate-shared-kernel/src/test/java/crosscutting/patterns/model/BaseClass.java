/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

class BaseClass {
    @DataContract
    protected int baseNumber;

    public BaseClass(int baseNumber) {
        this.baseNumber = baseNumber;
    }

    // Getter
    public int getBaseNumber() {
        return baseNumber;
    }
}
