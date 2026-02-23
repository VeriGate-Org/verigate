/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

public class PrimitiveHolder {
    @DataContract
    public byte aByte;
    @DataContract
    public short aShort;
    @DataContract
    public int anInt;
    @DataContract
    public long aLong;
    @DataContract
    public float aFloat;
    @DataContract
    public double aDouble;
    @DataContract
    public char aChar;
    @DataContract
    public boolean aBoolean;

    // Constructor to initialize all fields
    public PrimitiveHolder(
            byte aByte,
            short aShort,
            int anInt,
            long aLong,
            float aFloat,
            double aDouble,
            char aChar,
            boolean aBoolean) {
        this.aByte = aByte;
        this.aShort = aShort;
        this.anInt = anInt;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.aChar = aChar;
        this.aBoolean = aBoolean;
    }
}
