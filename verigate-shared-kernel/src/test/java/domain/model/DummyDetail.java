/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import crosscutting.serialization.DataContract;

import java.io.Serializable;
import java.util.Date;

public final class DummyDetail implements Serializable {

  @DataContract
  private String detailName;
  @DataContract
  private Date creationDate;

  private DummyDetail() {}

  public DummyDetail(String detailName, Date creationDate) {
    this.detailName = detailName;
    this.creationDate = creationDate;
  }

  public String getDetailName() {
    return detailName;
  }
}
