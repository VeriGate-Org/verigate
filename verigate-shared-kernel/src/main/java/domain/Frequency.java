/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import java.time.Period;

/**
 * The Frequency enum represents the different frequencies at which an event or action occurs.
 */
public enum Frequency {
  ANNUALLY {
    @Override
    public Period getPeriod() {
      return Period.ofYears(1);
    }
  },
  BI_ANNUALLY {
    @Override
    public Period getPeriod() {
      return Period.ofMonths(6);
    }
  },
  MONTHLY {
    @Override
    public Period getPeriod() {
      return Period.ofMonths(1);
    }
  },
  WEEKLY {
    @Override
    public Period getPeriod() {
      return Period.ofWeeks(1);
    }
  },
  ONCE_OFF {
    @Override
    public Period getPeriod() {
      return Period.ZERO;
    }
  };

  /**
   * Returns the Period associated with each frequency.
   *
   * @return the Period representing the frequency.
   */
  public abstract Period getPeriod();
}
