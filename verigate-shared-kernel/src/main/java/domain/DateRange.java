/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import java.time.LocalDate;

/**
 * Represents a date range that has a start date and an end date.
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {}
