/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import crosscutting.patterns.SimpleFactory;

/** A factory interface for creating instances of {@link HttpClient}. */
public interface HttpClientFactory extends SimpleFactory<HttpClient> {}
