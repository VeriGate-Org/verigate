/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns.model;

import crosscutting.serialization.DataContract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class SimpleHolder {
    @DataContract
    private String text;
    @DataContract
    private Date date;
    @DataContract
    private List<String> list;
    @DataContract
    private BigDecimal number;
    @DataContract
    private Instant dateTime;
    @DataContract
    private Status status; // Assuming Status is an enum

    // Enum for demonstration

    public enum Status {
        ACTIVE(1),
        INACTIVE(2),
        ARCHIVED(3);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    // Constructor
    public SimpleHolder(String text, Date date, List<String> list, BigDecimal number, Instant dateTime, Status status) {
        this.text = text;
        this.date = date;
        this.list = list;
        this.number = number;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getters
    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getList() {
        return list;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public Status getStatus() {
        return status;
    }
}
