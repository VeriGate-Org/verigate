/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

public class NestedClass {
    @DataContract
    private String detail;

    public NestedClass(String detail) {
        this.detail = detail;
    }

    // Getter
    public String getDetail() {
        return detail;
    }
}
