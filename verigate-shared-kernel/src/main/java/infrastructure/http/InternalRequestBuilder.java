/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import software.amazon.awssdk.http.SdkHttpMethod;

/** `RequestBuilder` is the interface that represents the builder for a request. */
public interface InternalRequestBuilder<OutT> {

  InternalRequestBuilder<OutT> setPayload(String payload);

  InternalRequestBuilder<OutT> setPath(String path);

  InternalRequestBuilder<OutT> setMethod(SdkHttpMethod method);

  InternalRequestBuilder<OutT> setDomain(String domain);

  OutT execute() throws TransientException, PermanentException;
}
