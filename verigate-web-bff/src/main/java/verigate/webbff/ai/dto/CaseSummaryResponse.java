/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.dto;

import java.util.List;

public record CaseSummaryResponse(
    String summary,
    String suggestedDisposition,
    String reasoning,
    List<String> keySignals
) {
}
