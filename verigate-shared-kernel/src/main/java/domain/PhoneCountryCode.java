/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Enumerates phone country codes along with their standard abbreviations. This enumeration
 * facilitates the management and validation of international phone numbers by providing a
 * standardized way to access country codes.
 *
 * <p>Each enum constant in this class represents a country or region and contains:
 *
 * <ul>
 *   <li>{@code code} - the international telephone dialing prefix for the country or region.
 *   <li>{@code abbreviation} - a commonly used abbreviation for the country or region.
 * </ul>
 * Examples include:
 *
 * <ul>
 *   <li>{@code UNKNOWN} - Represents an unknown country code "+000" with abbreviation "UNK".
 *   <li>{@code USA} and {@code CANADA} - Both use the country code "+1" but have different
 *       abbreviations: "USA" for the United States of America, and "CAN" for Canada.
 * </ul>
 * This enum can be used to programmatically determine the country based on the telephone number
 * prefix.
 */
public enum PhoneCountryCode {
  UNKNOWN("+000", "UNK"),
  USA("+1", "USA"),
  CANADA("+1", "CAN"),
  RUSSIA("+7", "RUS"),
  KAZAKHSTAN("+7", "KAZ"),
  UK("+44", "GBR"),
  GERMANY("+49", "DEU"),
  FRANCE("+33", "FRA"),
  ITALY("+39", "ITA"),
  SPAIN("+34", "ESP"),
  INDIA("+91", "IND"),
  CHINA("+86", "CHN"),
  JAPAN("+81", "JPN"),
  SOUTH_KOREA("+82", "KOR"),
  AUSTRALIA("+61", "AUS"),
  NEW_ZEALAND("+64", "NZL"),
  BRAZIL("+55", "BRA"),
  ARGENTINA("+54", "ARG"),
  SOUTH_AFRICA("+27", "ZAF"),
  NIGERIA("+234", "NGA"),
  EGYPT("+20", "EGY"),
  SAUDI_ARABIA("+966", "SAU"),
  MAURITIUS("+230", "MUS"),
  UAE("+971", "ARE"),
  ALBANIA("+355", "ALB"),
  ANDORRA("+376", "AND"),
  ARMENIA("+374", "ARM"),
  AUSTRIA("+43", "AUT"),
  AZERBAIJAN("+994", "AZE"),
  BELARUS("+375", "BLR"),
  BELGIUM("+32", "BEL"),
  BOSNIA_AND_HERZEGOVINA("+387", "BIH"),
  BULGARIA("+359", "BGR"),
  CROATIA("+385", "HRV"),
  CYPRUS("+357", "CYP"),
  CZECH_REPUBLIC("+420", "CZE"),
  DENMARK("+45", "DNK"),
  ESTONIA("+372", "EST"),
  FINLAND("+358", "FIN"),
  GEORGIA("+995", "GEO"),
  GREECE("+30", "GRC"),
  HUNGARY("+36", "HUN"),
  ICELAND("+354", "ISL"),
  IRELAND("+353", "IRL"),
  KOSOVO("+383", "XKX"),
  LATVIA("+371", "LVA"),
  LIECHTENSTEIN("+423", "LIE"),
  LITHUANIA("+370", "LTU"),
  LUXEMBOURG("+352", "LUX"),
  MALTA("+356", "MLT"),
  MOLDOVA("+373", "MDA"),
  MONACO("+377", "MCO"),
  MONTENEGRO("+382", "MNE"),
  NETHERLANDS("+31", "NLD"),
  NORTH_MACEDONIA("+389", "MKD"),
  NORWAY("+47", "NOR"),
  POLAND("+48", "POL"),
  PORTUGAL("+351", "PRT"),
  ROMANIA("+40", "ROU"),
  SAN_MARINO("+378", "SMR"),
  SERBIA("+381", "SRB"),
  SLOVAKIA("+421", "SVK"),
  SLOVENIA("+386", "SVN"),
  SWEDEN("+46", "SWE"),
  SWITZERLAND("+41", "CHE"),
  TURKEY("+90", "TUR"),
  UKRAINE("+380", "UKR"),
  UNITED_KINGDOM("+44", "GBR"),
  VATICAN_CITY("+379", "VAT"),
  ALGERIA("+213", "DZA"),
  ANGOLA("+244", "AGO"),
  BENIN("+229", "BEN"),
  BOTSWANA("+267", "BWA"),
  BURKINA_FASO("+226", "BFA"),
  BURUNDI("+257", "BDI"),
  CABO_VERDE("+238", "CPV"),
  CAMEROON("+237", "CMR"),
  CENTRAL_AFRICAN_REPUBLIC("+236", "CAF"),
  CHAD("+235", "TCD"),
  COMOROS("+269", "COM"),
  CONGO("+242", "COG"),
  DEMOCRATIC_REPUBLIC_OF_THE_CONGO("+243", "COD"),
  DJIBOUTI("+253", "DJI"),
  EQUATORIAL_GUINEA("+240", "GNQ"),
  ERITREA("+291", "ERI"),
  ESWATINI("+268", "SWZ"),
  ETHIOPIA("+251", "ETH"),
  GABON("+241", "GAB"),
  GAMBIA("+220", "GMB"),
  GHANA("+233", "GHA"),
  GUINEA("+224", "GIN"),
  GUINEA_BISSAU("+245", "GNB"),
  IVORY_COAST("+225", "CIV"),
  KENYA("+254", "KEN"),
  LESOTHO("+266", "LSO"),
  LIBERIA("+231", "LBR"),
  LIBYA("+218", "LBY"),
  MADAGASCAR("+261", "MDG"),
  MALAWI("+265", "MWI"),
  MALI("+223", "MLI"),
  MAURITANIA("+222", "MRT"),
  MOROCCO("+212", "MAR"),
  MOZAMBIQUE("+258", "MOZ"),
  NAMIBIA("+264", "NAM"),
  NIGER("+227", "NER"),
  RWANDA("+250", "RWA"),
  SAO_TOME_AND_PRINCIPE("+239", "STP"),
  SENEGAL("+221", "SEN"),
  SEYCHELLES("+248", "SYC"),
  SIERRA_LEONE("+232", "SLE"),
  SOMALIA("+252", "SOM"),
  SOUTH_SUDAN("+211", "SSD"),
  SUDAN("+249", "SDN"),
  TANZANIA("+255", "TZA"),
  TOGO("+228", "TGO"),
  TUNISIA("+216", "TUN"),
  UGANDA("+256", "UGA"),
  ZAMBIA("+260", "ZMB"),
  ZIMBABWE("+263", "ZWE"),
  AFGHANISTAN("+93", "AFG"),
  ALAND_ISLANDS("+358", "ALA"),
  AMERICAN_SAMOA("+1684", "ASM"),
  ANGUILLA("+1264", "AIA"),
  ANTARCTICA("+672", "ATA"),
  ANTIGUA_AND_BARBUDA("+1268", "ATG"),
  ARUBA("+297", "ABW"),
  BAHAMAS("+1242", "BHS"),
  BANGLADESH("+880", "BGD"),
  BARBADOS("+1246", "BRB"),
  BELIZE("+501", "BLZ"),
  BERMUDA("+1441", "BMU"),
  BHUTAN("+975", "BTN"),
  BOLIVIA("+591", "BOL"),
  BONAIRE_SINT_EUSTATIUS_AND_SABA("+599", "BES"),
  BOUVET_ISLAND("+55", "BVT"),
  BRITISH_INDIAN_OCEAN_TERRITORY("+246", "IOT"),
  CAYMAN_ISLANDS("+1345", "CYM"),
  CHRISTMAS_ISLAND("+61", "CXR"),
  COCOS_KEELING_ISLANDS("+61", "CCK"),
  COOK_ISLANDS("+682", "COK"),
  COSTA_RICA("+506", "CRI"),
  CUBA("+53", "CUB"),
  CURACAO("+599", "CUW"),
  DOMINICA("+1767", "DMA"),
  DOMINICAN_REPUBLIC("+1849", "DOM"),
  EAST_TIMOR("+670", "TLS"),
  EL_SALVADOR("+503", "SLV"),
  FALKLAND_ISLANDS("+500", "FLK"),
  FAROE_ISLANDS("+298", "FRO"),
  FIJI("+679", "FJI"),
  FRENCH_GUIANA("+594", "GUF"),
  FRENCH_POLYNESIA("+689", "PYF"),
  FRENCH_SOUTHERN_TERRITORIES("+262", "ATF"),
  GIBRALTAR("+350", "GIB"),
  GREENLAND("+299", "GRL"),
  GUADELOUPE("+590", "GLP"),
  GUAM("+1671", "GUM"),
  GUATEMALA("+502", "GTM"),
  GUERNSEY("+44", "GGY"),
  GUYANA("+592", "GUY"),
  HAITI("+509", "HTI"),
  HEARD_ISLAND_AND_MCDONALD_ISLANDS("+672", "HMD"),
  HONDURAS("+504", "HND"),
  HONG_KONG("+852", "HKG"),
  JAMAICA("+1876", "JAM"),
  JERSEY("+44", "JEY"),
  KIRIBATI("+686", "KIR"),
  KUWAIT("+965", "KWT"),
  MACAO("+853", "MAC"),
  MARSHALL_ISLANDS("+692", "MHL"),
  MARTINIQUE("+596", "MTQ"),
  MONTSERRAT("+1664", "MSR"),
  NAURU("+674", "NRU"),
  NEW_CALEDONIA("+687", "NCL"),
  NICARAGUA("+505", "NIC"),
  NIUE("+683", "NIU"),
  NORFOLK_ISLAND("+672", "NFK"),
  NORTHERN_MARIANA_ISLANDS("+1670", "MNP"),
  PAPUA_NEW_GUINEA("+675", "PNG"),
  PALAU("+680", "PLW"),
  PALESTINE("+970", "PSE"),
  PANAMA("+507", "PAN"),
  PITCAIRN("+64", "PCN"),
  PUERTO_RICO("+1939", "PRI"),
  REUNION("+262", "REU"),
  SAINT_BARTHELEMY("+590", "BLM"),
  SAINT_HELENA_ASCENSION_AND_TRISTAN_DA_CUNHA("+290", "SHN"),
  SAINT_KITTS_AND_NEVIS("+1869", "KNA"),
  SAINT_LUCIA("+1758", "LCA"),
  SAINT_MARTIN("+590", "MAF"),
  SAINT_PIERRE_AND_MIQUELON("+508", "SPM"),
  SAINT_VINCENT_AND_THE_GRENADINES("+1784", "VCT"),
  SAMOA("+685", "WSM"),
  SINT_MAARTEN("+1721", "SXM"),
  SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("+500", "SGS"),
  SVALBARD_AND_JAN_MAYEN("+47", "SJM"),
  SWAZILAND("+268", "SWZ"),
  TOKELAU("+690", "TKL"),
  TONGA("+676", "TON"),
  TRINIDAD_AND_TOBAGO("+1868", "TTO"),
  TURKS_AND_CAICOS_ISLANDS("+1649", "TCA"),
  TUVALU("+688", "TUV"),
  URUGUAY("+598", "URY"),
  VANUATU("+678", "VUT"),
  VIETNAM("+84", "VNM"),
  VIRGIN_ISLANDS_BRITISH("+1", "VGB"),
  VIRGIN_ISLANDS_US("+1", "VIR"),
  WALLIS_AND_FUTUNA("+681", "WLF"),
  WESTERN_SAHARA("+212", "ESH");

  private final String code;
  private final String isoCode;

  PhoneCountryCode(String code, String isoCode) {
    this.code = code;
    this.isoCode = isoCode;
  }

  public String getCode() {
    return code;
  }

  public String getIsoCode() {
    return isoCode;
  }

  /**
   * Searches for all entries within the enumeration that match the provided {@code
   * PhoneCountryCode}'s code. Returns a set of ISO codes corresponding to the matching country
   * codes.
   *
   * <p>This method iterates over all the values in the PhoneCountryCode enumeration, comparing each
   * entry's country code with the provided country code. If a match is found, the ISO code of the
   * matching entry is added to the result set. If no matches are found, an {@code
   * IllegalArgumentException} is thrown, indicating the provided country code is invalid.
   *
   * <p>Note: This method currently performs a linear search, which can be optimized for larger sets
   * of data.
   *
   * @param phoneCountryCode the {@code PhoneCountryCode} to search for among all available country
   *     codes.
   * @return a {@code Set<String>} containing all matching ISO codes.
   * @throws IllegalArgumentException if no entries match the provided country code.
   */
  public static Set<String> findByCode(PhoneCountryCode phoneCountryCode) {
    Set<String> matchingCodes = new HashSet<>();

    // Todo: this can be optimized
    for (PhoneCountryCode cc : values()) {
      if (cc.getCode().equals(phoneCountryCode.getCode())) {
        matchingCodes.add(cc.isoCode);
      }
    }

    if (matchingCodes.isEmpty()) {
      throw new IllegalArgumentException("Invalid country code: " + phoneCountryCode.getCode());
    }

    return matchingCodes;
  }
}
