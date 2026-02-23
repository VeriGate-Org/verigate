/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

/**
 * The DocumentStoredResponse class represents a document stored response.
 */
public record DocumentStoredResponse(DocumentId documentId, ResourceUri resourceUri) {}
