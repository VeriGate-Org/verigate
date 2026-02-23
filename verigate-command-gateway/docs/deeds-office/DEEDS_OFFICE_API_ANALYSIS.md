# South African Deeds Office SOAP API Analysis

**Service Name:** DeedsRegistrationEnquiryService  
**Endpoint:** http://deedssoap.deeds.gov.za:80/deeds-registration-soap/  
**Namespace:** http://enquiry.service.registration.deeds.gov.za/  
**Protocol:** SOAP 1.1/1.2 over HTTP  
**Style:** Document/Literal

---

## Overview

This is a comprehensive property registration and deeds enquiry service for South Africa. The API provides access to property ownership records, title deeds, company registrations, and personal property information maintained by the South African Deeds Office.

**Authentication:** Username/Password-based (passed in each request)

---

## Core Capabilities

### 1. Property Searches
- **Erf (Urban Stand) Properties** - Township plots/urban properties
- **Farm Properties** - Rural agricultural land
- **Agricultural Holdings** - Agricultural land holdings
- **Township Properties** - Township-level property data
- **Sectional Title Schemes** - Condominiums/apartments
- **Exclusive Use Areas** - Shared property exclusive use zones

### 2. Person & Company Searches
- **Individual Property Ownership** - Search by ID number
- **Company Property Holdings** - Search by company name/registration number
- **Property History** - Transaction and ownership history
- **Title Deed Information** - Deed numbers, registration dates, purchase prices

### 3. Supporting Data
- **Office Registry Information** - Deeds office locations and codes
- **Property Type Classifications** - Property type codes and descriptions

---

## Available SOAP Operations (15 Total)

### Person/Identity-Based Queries

#### 1. `getPropertySummaryInformationByIDNumber`
**Purpose:** Get property summary for a person by SA ID number  
**Input:**
- `idNumber` (string) - South African ID number
- `officeCode` (string) - Deeds office code
- `username` (string)
- `password` (string)

**Returns:** Person information with property summaries

#### 2. `getPropertySummaryInformationByIdentityNumberList`
**Purpose:** Batch query for multiple ID numbers  
**Input:**
- `identityNumberList` (string[]) - List of SA ID numbers
- `officeCode` (string)
- `username` (string)
- `password` (string)

**Returns:** List of person information responses

#### 3. `getPersonFullPropertyInformationByIdentityNumber`
**Purpose:** Complete property details for a person  
**Input:** Same as summary but returns full details
**Returns:** 
- Person details (name, ID, marital status, former names)
- Property ownership details
- Title deed information
- Property history
- Endorsements (bonds, servitudes, etc.)
- Contract details

#### 4. `getPersonFullPropertyInformationByIdentityNumberList`
**Purpose:** Batch full property query  
**Input:** List of identity numbers  
**Returns:** Complete property information for multiple persons

---

### Company-Based Queries

#### 5. `getPropertySummaryInformationByCompanyNameAndRegistrationNumber`
**Purpose:** Get properties owned by a company  
**Input:**
- `companyName` (string)
- `companyNumber` (string) - Company registration number
- `officeCode` (string)
- `username` (string)
- `password` (string)

**Returns:**
- Company details (name, registration number, type)
- Property summaries
- Contract details

---

### Property-Specific Queries

#### 6. `getErfPropertyInformation`
**Purpose:** Get erf (urban stand) property details  
**Input:**
- `erf` (string) - Erf number
- `townshipName` (string)
- `officeCode` (string)
- `portion` (string) - Portion number (if subdivided)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

**Returns:**
- Property details (extent, location, diagram deed number)
- Owner information
- Title deed details
- Property history
- Endorsements (bonds, caveats, etc.)

#### 7. `getErfPropertyInformationByPortionList`
**Purpose:** Batch query for multiple portions of an erf  
**Input:**
- `erf` (string)
- `townshipName` (string)
- `officeCode` (string)
- `portions` (string[]) - List of portion numbers
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

#### 8. `getFarmPropertyInformationByNumberAndPortionList`
**Purpose:** Get farm property details  
**Input:**
- `farmNumber` (string)
- `portionNumberList` (string[])
- `registrationDivision` (string)
- `officeCode` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

**Returns:**
- Farm details (name, number, extent, diagram deed)
- Local authority
- Province and registration division
- Owner information
- Title deeds

#### 9. `getTownshipPropertyInformation`
**Purpose:** Get township-level property information  
**Input:**
- `townshipName` (string)
- `officeCode` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

#### 10. `getAgriculturalHoldingPropertyInformation`
**Purpose:** Get agricultural holding details  
**Input:**
- `agriculturalHoldingNumber` (string)
- `agriculturalHoldingName` (string)
- `officeCode` (string)
- `portion` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

#### 11. `getAgriculturalHoldingAreaNamePropertyInformation`
**Purpose:** Search agricultural holdings by area name  
**Input:**
- `agriculturalHoldingAreaName` (string)
- `officeCode` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

#### 12. `getSchemePropertyInformationByNameAndSchemeNumberList`
**Purpose:** Get sectional title scheme information (condos/apartments)  
**Input:**
- `schemeName` (string)
- `schemeNumberList` (string[])
- `officeCode` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

**Returns:**
- Scheme details (sectional plan number, address, unit quantity)
- Owner details
- Endorsements
- History

#### 13. `getExclusiveUseAreaPropertyInformation`
**Purpose:** Get exclusive use area details (shared property areas)  
**Input:**
- `schemeNumber` (string)
- `schemeName` (string)
- `exclusiveUsageNumber` (string[])
- `officeCode` (string)
- `propertyTypeCode` (string)
- `username` (string)
- `password` (string)

---

### Reference Data Queries

#### 14. `getDeedsPropertyTypeList`
**Purpose:** Get list of property type codes and descriptions  
**Input:** None (username/password only implied)  
**Returns:** List of property types with codes

#### 15. `getOfficeRegistryList`
**Purpose:** Get list of deeds office locations  
**Input:** None  
**Returns:** Office codes and descriptions

---

## Key Response Data Structures

### Person Details Response
```xml
<personDetailsResponse>
  <errorResponse/>
  <formerName>string</formerName>
  <fullName>string</fullName>
  <idNumber>string</idNumber>
  <maritalStatus>string</maritalStatus>
  <personType>string</personType>
  <personTypeCode>string</personTypeCode>
</personDetailsResponse>
```

### Property Owner Details
```xml
<propertyOwnerDetailsResponse>
  <errorResponse/>
  <fullName>string</fullName>
  <idNumber>string</idNumber>
  <share>string</share>          <!-- Ownership share (e.g., "1/2") -->
  <titleDeed>string</titleDeed>   <!-- Title deed number -->
</propertyOwnerDetailsResponse>
```

### Title Deed Details
```xml
<titleDeedDetailsResponse>
  <errorResponse/>
  <microfilmReference>string</microfilmReference>
  <purchaseDate>string</purchaseDate>
  <purchasePrice>string</purchasePrice>
  <registrationDate>string</registrationDate>
  <titleDeed>string</titleDeed>
</titleDeedDetailsResponse>
```

### Property Endorsement (Bonds, Servitudes, Restrictions)
```xml
<propertyEndorsementDetailsResponse>
  <amount>string</amount>           <!-- Bond amount -->
  <document>string</document>       <!-- Document type/number -->
  <errorResponse/>
  <holder>string</holder>           <!-- Bond holder (bank) -->
  <microfilmReference>string</microfilmReference>
</propertyEndorsementDetailsResponse>
```

### Property History
```xml
<propertyHistoryDetailsResponse>
  <amount>string</amount>
  <document>string</document>
  <errorResponse/>
  <holder>string</holder>
  <microfilmReference>string</microfilmReference>
</propertyHistoryDetailsResponse>
```

### Property Summary
```xml
<propertySummaryResponse>
  <erf>string</erf>
  <errorResponse/>
  <microFilmReference>string</microFilmReference>
  <officeCode>string</officeCode>
  <price>string</price>                      <!-- Purchase price -->
  <propertyCount>string</propertyCount>
  <propertyTypeCode>string</propertyTypeCode>
  <propertyTypeDescription>string</propertyTypeDescription>
  <purchaseDate>string</purchaseDate>
  <registrationDate>string</registrationDate>
  <share>string</share>                      <!-- Ownership share -->
  <titleDeedNumber>string</titleDeedNumber>
  <township>string</township>
</propertySummaryResponse>
```

### Error Response
```xml
<errorResponse>
  <errorCode>string</errorCode>
  <errorDescription>string</errorDescription>
  <errorMessage>string</errorMessage>
</errorResponse>
```

---

## Property Types

Based on the schema, the service supports these property types:
- **Erf** - Urban stands/plots
- **Farm** - Agricultural/rural properties
- **Agricultural Holding** - Agricultural land holdings
- **Township** - Township properties
- **Sectional Title Scheme** - Condominiums/apartments
- **Exclusive Use Area** - Shared property exclusive areas

Each query requires a `propertyTypeCode` which can be obtained from `getDeedsPropertyTypeList()`.

---

## Office Codes

Each query requires an `officeCode` specifying which Deeds Office registry to query:
- Different provinces and regions have different office codes
- Use `getOfficeRegistryList()` to get available offices
- Examples might include: Johannesburg, Cape Town, Pretoria, Durban, etc.

---

## Authentication

All operations require:
- `username` (string) - Service account username
- `password` (string) - Service account password

These credentials must be obtained from the Deeds Office and are likely tied to:
- IP whitelisting (your IP: 16.28.8.25)
- Billing/usage agreements
- Service level agreements

---

## Integration Considerations

### 1. **Network Access**
- Service endpoint: `http://deedssoap.deeds.gov.za:80`
- **Requires IP whitelisting** - Provide IP 16.28.8.25
- No HTTPS - uses plain HTTP (security via network restrictions)

### 2. **Authentication Flow**
- Username/password in each SOAP request
- No session management or token-based auth
- Credentials embedded in SOAP envelope

### 3. **Data Volume**
- Batch operations available for multiple portions, identity numbers
- Returns comprehensive data including history, endorsements, owners
- Consider pagination or chunking for large datasets

### 4. **Error Handling**
- Each response contains an `errorResponse` element
- Check for errors at multiple levels (operation level, entity level)
- Error codes and descriptions provided

### 5. **Use Cases for VeriGate**

Based on your verification platform, key operations would be:

**Identity Verification:**
```java
// Verify person owns property
getPropertySummaryInformationByIDNumber(idNumber, officeCode, username, password)
```

**Company Verification:**
```java
// Verify company property holdings
getPropertySummaryInformationByCompanyNameAndRegistrationNumber(
    companyName, companyNumber, officeCode, username, password)
```

**Property Ownership Verification:**
```java
// Get full details of property ownership
getPersonFullPropertyInformationByIdentityNumber(idNumber, officeCode, username, password)
```

**Bulk Verification:**
```java
// Batch verification of multiple persons
getPropertySummaryInformationByIdentityNumberList(
    idNumberList, officeCode, username, password)
```

---

## Sample SOAP Request Structure

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Header/>
   <soapenv:Body>
      <enq:getPropertySummaryInformationByIDNumber>
         <enq:idNumber>8001015009087</enq:idNumber>
         <enq:officeCode>T</enq:officeCode>
         <enq:username>your_username</enq:username>
         <enq:password>your_password</enq:password>
      </enq:getPropertySummaryInformationByIDNumber>
   </soapenv:Body>
</soapenv:Envelope>
```

---

## Next Steps for Integration

1. **Obtain Credentials**
   - Contact Deeds Office for service account
   - Request IP whitelisting for 16.28.8.25
   - Get office codes and property type codes

2. **Test Connectivity**
   - Use the deployed Lambda function to verify access after whitelisting
   - Test with `getOfficeRegistryList()` (no parameters needed)
   - Test with `getDeedsPropertyTypeList()` for reference data

3. **Implementation Approach**
   - Use JAX-WS or Apache CXF for SOAP client generation
   - Generate client from WSDL
   - Implement adapter following VeriGate clean architecture
   - Map responses to VeriGate verification results

4. **Create Deeds Office Adapter Module**
   ```
   verigate-adapter-deeds/
   ├── verigate-adapter-deeds-domain/
   │   └── Property verification domain models
   ├── verigate-adapter-deeds-application/
   │   └── Verification use cases
   └── verigate-adapter-deeds-infrastructure/
       └── SOAP client implementation
   ```

---

## Recommended Operations for VeriGate

| Verification Type | Operation | Use Case |
|------------------|-----------|----------|
| Person owns property | `getPropertySummaryInformationByIDNumber` | Quick property ownership check |
| Full property details | `getPersonFullPropertyInformationByIdentityNumber` | Detailed ownership verification |
| Company verification | `getPropertySummaryInformationByCompanyNameAndRegistrationNumber` | Corporate asset verification |
| Batch verification | `getPropertySummaryInformationByIdentityNumberList` | Bulk identity verification |
| Property lookup | `getErfPropertyInformation` | Verify specific property details |

---

## API Characteristics

- ✅ **Comprehensive** - Covers all SA property types
- ✅ **Batch Support** - Multiple identity/portion queries
- ✅ **Historical Data** - Property history and transactions
- ✅ **Ownership Details** - Full owner information with shares
- ✅ **Endorsements** - Bonds, servitudes, restrictions
- ⚠️ **Authentication** - Basic username/password (no OAuth/tokens)
- ⚠️ **Network Security** - HTTP only, relies on IP whitelisting
- ⚠️ **SOAP Protocol** - Older technology but stable and well-documented

---

## Documentation Location

WSDL File: `/docs/deeds-office/deeds-registration-soap.xml`

This WSDL can be used to:
- Generate SOAP client code
- Validate request/response structures
- Understand data types and operations
- Create integration tests
