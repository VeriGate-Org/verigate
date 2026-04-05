/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.domain.models;

/**
 * Represents a single chunk from a streaming AI model response.
 *
 * @param delta the incremental text content
 * @param stopReason the stop reason if this is the final chunk, null otherwise
 */
public record AiStreamChunk(
    String delta,
    String stopReason
) {
}
