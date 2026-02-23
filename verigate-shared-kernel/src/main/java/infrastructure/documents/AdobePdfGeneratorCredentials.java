/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

/**
 * This class represents the credentials required to access the Adobe PDF Generator API.
 */
public record AdobePdfGeneratorCredentials(String clientId, String clientSecret) {}
