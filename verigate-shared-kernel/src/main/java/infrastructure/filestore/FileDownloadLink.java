/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.filestore;

import java.time.Instant;

/** The GeneratedPolicyScheduleLink class represents a link to a generated policy schedule. */
public record FileDownloadLink(String downloadLink, Instant validUntil) {}
