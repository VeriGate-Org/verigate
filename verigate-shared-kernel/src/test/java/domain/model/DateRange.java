/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import crosscutting.serialization.DataContract;
import java.io.Serializable;
import java.util.Date;

public final class DateRange implements Serializable {

  @DataContract
  private Date startDate;
  @DataContract
  private Date endDate;

  private DateRange() {
  }

  public DateRange(Date startDate, Date endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Date getStartDate() {
    return startDate;
  }
}
