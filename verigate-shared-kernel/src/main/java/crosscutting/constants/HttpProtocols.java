/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.constants;

/**
 * This enum represents the HTTP protocols.
 */
public enum HttpProtocols {
  HTTP("http"),
  HTTPS("https");

  private final String protocol;

  HttpProtocols(String protocol) {
    this.protocol = protocol;
  }

  public String getProtocol() {
    return protocol;
  }
}
