/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.models;

import java.time.LocalDate;

/**
 * A single director record on a {@link CompanyProfile}, sourced from Datanamix's CIPC
 * Result API ({@code CIPCResult.CommercialDirectorInformation[]}).
 *
 * @param idNumber the director's South African ID number ({@code IDNumber})
 * @param fullName the director's full name ({@code FullName})
 * @param directorStatus the director's status on this company ({@code DirectorStatusCode})
 * @param appointmentDate when the director was appointed ({@code AppointmentDate})
 */
public record CompanyDirector(
    String idNumber,
    String fullName,
    String directorStatus,
    LocalDate appointmentDate
) {
}
