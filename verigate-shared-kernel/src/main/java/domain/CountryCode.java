/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

/**
 * Enum representing ISO country codes. This enumeration simplifies the management of country codes
 * throughout the application, ensuring consistent usage and avoiding common pitfalls with string
 * literals.
 *
 * <p>Currently, it includes a single country code: - {@code ZAF}: Represents the ISO country code
 * for South Africa. Additional country codes can be added as required by the application.
 *
 * <p>This enumeration is utilized within the {@code FuneralProductAggregateRoot} and {@code
 * FuneralProductDataModel} classes to represent countries associated with specific products or
 * services.
 */
public enum CountryCode {
  AFG("AFG"),
  ALB("ALB"),
  DZA("DZA"),
  AND("AND"),
  AGO("AGO"),
  ATG("ATG"),
  ARG("ARG"),
  ARM("ARM"),
  AUS("AUS"),
  AUT("AUT"),
  AZE("AZE"),
  BHS("BHS"),
  BHR("BHR"),
  BGD("BGD"),
  BRB("BRB"),
  BLR("BLR"),
  BEL("BEL"),
  BLZ("BLZ"),
  BEN("BEN"),
  BTN("BTN"),
  BOL("BOL"),
  BIH("BIH"),
  BWA("BWA"),
  BRA("BRA"),
  BRN("BRN"),
  BGR("BGR"),
  BFA("BFA"),
  BDI("BDI"),
  CPV("CPV"),
  KHM("KHM"),
  CMR("CMR"),
  CAN("CAN"),
  CAF("CAF"),
  TCD("TCD"),
  CHL("CHL"),
  CHN("CHN"),
  COL("COL"),
  COM("COM"),
  COD("COD"),
  COG("COG"),
  CRI("CRI"),
  CIV("CIV"),
  HRV("HRV"),
  CUB("CUB"),
  CYP("CYP"),
  CZE("CZE"),
  DNK("DNK"),
  DJI("DJI"),
  DMA("DMA"),
  DOM("DOM"),
  ECU("ECU"),
  EGY("EGY"),
  SLV("SLV"),
  GNQ("GNQ"),
  ERI("ERI"),
  EST("EST"),
  SWZ("SWZ"),
  ETH("ETH"),
  FJI("FJI"),
  FIN("FIN"),
  FRA("FRA"),
  GAB("GAB"),
  GMB("GMB"),
  GEO("GEO"),
  DEU("DEU"),
  GHA("GHA"),
  GRC("GRC"),
  GRD("GRD"),
  GTM("GTM"),
  GIN("GIN"),
  GNB("GNB"),
  GUY("GUY"),
  HTI("HTI"),
  HND("HND"),
  HUN("HUN"),
  ISL("ISL"),
  IND("IND"),
  IDN("IDN"),
  IRN("IRN"),
  IRQ("IRQ"),
  IRL("IRL"),
  ISR("ISR"),
  ITA("ITA"),
  JAM("JAM"),
  JPN("JPN"),
  JOR("JOR"),
  KAZ("KAZ"),
  KEN("KEN"),
  KIR("KIR"),
  PRK("PRK"),
  KOR("KOR"),
  KWT("KWT"),
  KGZ("KGZ"),
  LAO("LAO"),
  LVA("LVA"),
  LBN("LBN"),
  LSO("LSO"),
  LBR("LBR"),
  LBY("LBY"),
  LIE("LIE"),
  LTU("LTU"),
  LUX("LUX"),
  MDG("MDG"),
  MWI("MWI"),
  MYS("MYS"),
  MDV("MDV"),
  MLI("MLI"),
  MLT("MLT"),
  MHL("MHL"),
  MRT("MRT"),
  MUS("MUS"),
  MEX("MEX"),
  FSM("FSM"),
  MDA("MDA"),
  MCO("MCO"),
  MNG("MNG"),
  MNE("MNE"),
  MAR("MAR"),
  MOZ("MOZ"),
  MMR("MMR"),
  NAM("NAM"),
  NRU("NRU"),
  NPL("NPL"),
  NLD("NLD"),
  NZL("NZL"),
  NIC("NIC"),
  NER("NER"),
  NGA("NGA"),
  MKD("MKD"),
  NOR("NOR"),
  OMN("OMN"),
  PAK("PAK"),
  PLW("PLW"),
  PAN("PAN"),
  PNG("PNG"),
  PRY("PRY"),
  PER("PER"),
  PHL("PHL"),
  POL("POL"),
  PRT("PRT"),
  QAT("QAT"),
  ROU("ROU"),
  RUS("RUS"),
  RWA("RWA"),
  KNA("KNA"),
  LCA("LCA"),
  VCT("VCT"),
  WSM("WSM"),
  SMR("SMR"),
  STP("STP"),
  SAU("SAU"),
  SEN("SEN"),
  SRB("SRB"),
  SYC("SYC"),
  SLE("SLE"),
  SGP("SGP"),
  SVK("SVK"),
  SVN("SVN"),
  SLB("SLB"),
  SOM("SOM"),
  ZAF("ZAF"),
  SSD("SSD"),
  ESP("ESP"),
  LKA("LKA"),
  SDN("SDN"),
  SUR("SUR"),
  SWE("SWE"),
  CHE("CHE"),
  SYR("SYR"),
  TJK("TJK"),
  TZA("TZA"),
  THA("THA"),
  TLS("TLS"),
  TGO("TGO"),
  TON("TON"),
  TTO("TTO"),
  TUN("TUN"),
  TUR("TUR"),
  TKM("TKM"),
  TUV("TUV"),
  UGA("UGA"),
  UKR("UKR"),
  ARE("ARE"),
  GBR("GBR"),
  USA("USA"),
  URY("URY"),
  UZB("UZB"),
  VUT("VUT"),
  VEN("VEN"),
  VNM("VNM"),
  YEM("YEM"),
  ZMB("ZMB"),
  TCA("TCA"),
  AIA("AIA"),
  BMU("BMU"),
  IOT("IOT"),
  CXR("CXR"),
  CCK("CCK"),
  FLK("FLK"),
  GIB("GIB"),
  MSR("MSR"),
  PCN("PCN"),
  SHN("SHN"),
  SGS("SGS"),
  TKL("TKL"),
  VIR("VIR"),
  WLF("WLF"),
  ESH("ESH"),
  ATA("ATA"),
  BVT("BVT"),
  ATF("ATF"),
  HMD("HMD"),
  UMI("UMI"),
  COK("COK"),
  NIU("NIU"),
  NFK("NFK"),
  MNP("MNP"),
  PRI("PRI"),
  TWN("TWN"),
  HKG("HKG"),
  MAC("MAC"),
  CYM("CYM"),
  PSE("PSE"),
  GLP("GLP"),
  GUM("GUM"),
  MYT("MYT"),
  VGB("VGB"),
  PYF("PYF"),
  REU("REU"),
  GGY("GGY"),
  JEY("JEY"),
  MAF("MAF"),
  SXM("SXM"),
  BES("BES"),
  CUW("CUW"),
  GUF("GUF"),
  MTQ("MTQ"),
  ABW("ABW"),
  VAT("VAT"),
  FRO("FRO"),
  GRL("GRL"),
  SJM("SJM"),
  ALA("ALA"),
  NCL("NCL"),
  IMN("IMN"),
  BLM("BLM"),
  ASM("ASM"),
  SPM("SPM"),
  ZWE("ZWE");

  // Add more country codes as needed

  /** The ISO country code string. */
  private final String code;

  /**
   * Constructs a new {@code CountryCode}.
   *
   * @param code The ISO country code as a string.
   */
  CountryCode(String code) {
    this.code = code;
  }

  /**
   * Returns the ISO country code string associated with this enum constant.
   *
   * @return The ISO country code.
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the {@code CountryCode} constant corresponding to the given string code. This method
   * provides a case-insensitive lookup, enhancing robustness and usability.
   *
   * @param code The ISO country code string.
   * @return The corresponding {@code CountryCode} enum constant.
   * @throws IllegalArgumentException if the specified code does not match any constant defined in
   *     this enumeration.
   */
  public static CountryCode fromString(String code) {
    for (CountryCode countryCode : values()) {
      if (countryCode.getCode().equalsIgnoreCase(code)) {
        return countryCode;
      }
    }

    throw new IllegalArgumentException("No constant with text " + code + " found");
  }
}
