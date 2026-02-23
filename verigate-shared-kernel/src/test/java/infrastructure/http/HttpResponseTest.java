/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.exceptions.PermanentException;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.http.HttpStatusCode;

public class HttpResponseTest {

  @Test
  void isSuccess() {

    // 2xx range

    assertTrue(new HttpResponse(HttpStatusCode.OK, "").isSuccess());
    assertTrue(new HttpResponse(HttpStatusCode.ACCEPTED, "").isSuccess());
    assertTrue(new HttpResponse(HttpStatusCode.CREATED, "").isSuccess());
    assertTrue(new HttpResponse(HttpStatusCode.NO_CONTENT, "").isSuccess());

    // all others

    assertFalse(new HttpResponse(HttpStatusCode.MOVED_PERMANENTLY, "").isSuccess()); // 3xx
    assertFalse(new HttpResponse(HttpStatusCode.BAD_REQUEST, "").isSuccess()); // 4xx
    assertFalse(new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "").isSuccess()); // 5xx
    assertFalse(new HttpResponse(HttpStatusCode.CONTINUE, "").isSuccess()); // 1xx
  }

  @Test
  void orElseThrow() {

    // 2xx
    assertDoesNotThrow(() ->
        new HttpResponse(HttpStatusCode.OK, "")
            .orElseThrow()
    );

    // all others
    PermanentException e = assertThrows(PermanentException.class, () ->
        new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "some error message")
            .orElseThrow()
    );
    assertEquals("HTTP request failed with status code 500 and response body: some error message", e.getMessage());
  }

  @Test
  void onSuccessOrElseThrow() {

    // 2xx
    String body = assertDoesNotThrow(() ->
        new HttpResponse(HttpStatusCode.OK, "some body")
            .onSuccessOrElseThrow(HttpResponse::body) // could for example deserialize the body
    );
    assertEquals("some body", body);

    // all others
    PermanentException e = assertThrows(PermanentException.class, () ->
        new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "some error message")
            .onSuccessOrElseThrow(HttpResponse::body)
    );
    assertEquals("HTTP request failed with status code 500 and response body: some error message",
        e.getMessage()
    );
  }

  @Test
  void onSuccessOrElseThrowWithExceptionHandler() {

    // 2xx
    String body = assertDoesNotThrow(() ->
        new HttpResponse(HttpStatusCode.OK, "some body")
            .onSuccessOrElseThrow(
                HttpResponse::body, // success handler, could for example deserialize the body
                response -> new PermanentException("")  // exception mapper
            )
    );
    assertEquals("some body", body);

    // all others
    PermanentException e = assertThrows(PermanentException.class, () ->
        new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "some error message")
            .onSuccessOrElseThrow(
                HttpResponse::body,
                response -> new PermanentException("test")
            )
    );
    assertEquals("test", e.getMessage());
  }
}
