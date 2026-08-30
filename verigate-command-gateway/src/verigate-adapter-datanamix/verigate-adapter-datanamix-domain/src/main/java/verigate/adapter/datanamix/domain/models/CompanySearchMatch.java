/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.models;

/**
 * A single candidate returned by a company name search, before the full
 * {@link CompanyProfile} is resolved. Company names aren't unique in CIPC, so a name
 * search can (and often does) return multiple candidates — the caller resolves the one
 * it wants via {@code enquiryId}/{@code enquiryResultId}.
 *
 * @param businessName the candidate's business name
 * @param registrationNumber the candidate's CIPC registration number
 * @param enquiryId the identifier needed to resolve the full profile ({@code EnquiryID})
 * @param enquiryResultId the identifier needed to resolve the full profile
 *     ({@code EnquiryResultID})
 */
public record CompanySearchMatch(
    String businessName,
    String registrationNumber,
    long enquiryId,
    long enquiryResultId
) {
}
