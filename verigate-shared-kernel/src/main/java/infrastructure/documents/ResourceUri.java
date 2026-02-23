/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

/**
 * The ResourceUri class represents a resource URI.
 */
public class ResourceUri {
  private final String value;

  @SuppressWarnings("unused")
  private ResourceUri() {
    this.value = "";
  }

  public ResourceUri(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
