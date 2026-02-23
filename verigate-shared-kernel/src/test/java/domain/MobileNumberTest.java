/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class MobileNumberTest {

  @Test
  public void testValidMobileNumber_USA() {
    MobileNumber mobileNumber = new MobileNumber("+11234567890");
    assertEquals(PhoneCountryCode.USA, mobileNumber.phoneCountryCode());
    assertEquals("1234567890", mobileNumber.localNumber());
    assertEquals("+11234567890", mobileNumber.internationalNumberE164());
  }

  @Test
  public void testValidMobileNumber_SouthAfrica() {
    MobileNumber mobileNumber = new MobileNumber("+27123456789");
    assertEquals(PhoneCountryCode.SOUTH_AFRICA, mobileNumber.phoneCountryCode());
    assertEquals("123456789", mobileNumber.localNumber());
    assertEquals("+27123456789", mobileNumber.internationalNumberE164());
  }

  @Test
  public void testInvalidMobileNumber_NoPlusSign() {
    Executable executable = () -> new MobileNumber("11234567890");
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
    assertEquals("Number must be in E.164 format and start with '+'", exception.getMessage());
  }

  @Test
  public void testInvalidMobileNumber_InvalidCountryCode() {
    Executable executable = () -> new MobileNumber("+999123456789");
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
    assertEquals("Invalid country code in number: +999123456789", exception.getMessage());
  }
}
