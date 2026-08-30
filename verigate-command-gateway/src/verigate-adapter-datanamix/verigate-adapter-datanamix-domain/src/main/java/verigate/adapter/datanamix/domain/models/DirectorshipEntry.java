/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.models;

import java.time.LocalDate;

/**
 * A single directorship record, sourced from Datanamix's CIPC Director Result API
 * ({@code Result.DirectorResults.ConsumerDirectorshipLink[]}).
 *
 * @param companyName the company's registered/commercial name ({@code CommercialName})
 * @param registrationNumber the CIPC enterprise number ({@code RegistrationNumber})
 * @param companyStatus the company's registration status ({@code CommercialStatus})
 * @param directorStatus the person's status as a director of this company
 *     ({@code DirectorStatus})
 * @param designation the director's role/designation, if provided
 *     ({@code DirectorDesignationDescription})
 * @param appointmentDate when the person was appointed a director of this company
 */
public record DirectorshipEntry(
    String companyName,
    String registrationNumber,
    String companyStatus,
    String directorStatus,
    String designation,
    LocalDate appointmentDate
) {
}
