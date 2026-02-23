# Deeds Office API - Quick Reference Guide

## Service Endpoint
```
http://deedssoap.deeds.gov.za:80/deeds-registration-soap/
```

## Authentication
All requests require:
- **username:** Service account username
- **password:** Service account password
- **IP Whitelisting:** 16.28.8.25 must be whitelisted

---

## Top 5 Most Useful Operations

### 1. Check Person's Property Ownership
**Operation:** `getPropertySummaryInformationByIDNumber`

**Use Case:** Quick verification if person owns any property

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
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

**Response Data:**
- Person details (name, ID, marital status)
- List of properties owned
- Title deed numbers
- Purchase dates and prices
- Property shares/ownership percentages

---

### 2. Get Full Property Details for Person
**Operation:** `getPersonFullPropertyInformationByIdentityNumber`

**Use Case:** Comprehensive property ownership verification with history

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getPersonFullPropertyInformationByIdentityNumber>
         <enq:idNumber>8001015009087</enq:idNumber>
         <enq:officeCode>T</enq:officeCode>
         <enq:username>your_username</enq:username>
         <enq:password>your_password</enq:password>
      </enq:getPersonFullPropertyInformationByIdentityNumber>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response Data:**
- All property summaries PLUS:
- Property endorsements (bonds, mortgages)
- Complete ownership history
- Contract details
- Microfilm references

---

### 3. Batch Verification (Multiple People)
**Operation:** `getPropertySummaryInformationByIdentityNumberList`

**Use Case:** Verify multiple persons in single API call

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getPropertySummaryInformationByIdentityNumberList>
         <enq:identityNumberList>8001015009087</enq:identityNumberList>
         <enq:identityNumberList>7505205167081</enq:identityNumberList>
         <enq:identityNumberList>6309285432089</enq:identityNumberList>
         <enq:officeCode>T</enq:officeCode>
         <enq:username>your_username</enq:username>
         <enq:password>your_password</enq:password>
      </enq:getPropertySummaryInformationByIdentityNumberList>
   </soapenv:Body>
</soapenv:Envelope>
```

---

### 4. Check Company Property Holdings
**Operation:** `getPropertySummaryInformationByCompanyNameAndRegistrationNumber`

**Use Case:** Corporate asset verification

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getPropertySummaryInformationByCompanyNameAndRegistrationNumber>
         <enq:companyName>ABC PROPERTIES</enq:companyName>
         <enq:companyNumber>2015/123456/07</enq:companyNumber>
         <enq:officeCode>T</enq:officeCode>
         <enq:username>your_username</enq:username>
         <enq:password>your_password</enq:password>
      </enq:getPropertySummaryInformationByCompanyNameAndRegistrationNumber>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response Data:**
- Company details (registration number, type)
- Properties owned by company
- Contract details

---

### 5. Get Property Details by Address (Erf)
**Operation:** `getErfPropertyInformation`

**Use Case:** Verify specific property ownership and details

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getErfPropertyInformation>
         <enq:erf>1234</enq:erf>
         <enq:townshipName>SANDTON</enq:townshipName>
         <enq:officeCode>T</enq:officeCode>
         <enq:portion>0</enq:portion>
         <enq:propertyTypeCode>E</enq:propertyTypeCode>
         <enq:username>your_username</enq:username>
         <enq:password>your_password</enq:password>
      </enq:getErfPropertyInformation>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response Data:**
- Property extent (size)
- Current owner(s) with shares
- Title deed information
- Property history
- Endorsements (bonds, restrictions)

---

## Reference Data Operations

### Get Office Codes
**Operation:** `getOfficeRegistryList`

**Purpose:** Get list of Deeds Office locations and their codes

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getOfficeRegistryList/>
   </soapenv:Body>
</soapenv:Envelope>
```

**Common Office Codes:**
- **T** - Pretoria (Gauteng)
- **J** - Johannesburg (Gauteng)
- **C** - Cape Town (Western Cape)
- **D** - Durban (KwaZulu-Natal)
- **B** - Bloemfontein (Free State)

---

### Get Property Type Codes
**Operation:** `getDeedsPropertyTypeList`

**Purpose:** Get valid property type codes

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Body>
      <enq:getDeedsPropertyTypeList/>
   </soapenv:Body>
</soapenv:Envelope>
```

**Common Property Type Codes:**
- **E** - Erf (urban stand)
- **F** - Farm
- **T** - Township
- **S** - Sectional title scheme
- **A** - Agricultural holding

---

## Error Handling

All responses include an `errorResponse` element:

```xml
<errorResponse>
  <errorCode>ERROR_CODE</errorCode>
  <errorDescription>Human readable description</errorDescription>
  <errorMessage>Detailed error message</errorMessage>
</errorResponse>
```

**Check for errors:**
1. HTTP response code (should be 200)
2. SOAP Fault (indicates protocol error)
3. `errorResponse` element in business response
4. Empty result lists (no records found)

---

## Common Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| `idNumber` | SA ID number (13 digits) | `8001015009087` |
| `officeCode` | Deeds office code | `T` (Pretoria) |
| `propertyTypeCode` | Property type | `E` (Erf) |
| `portion` | Property portion number | `0` (whole), `1`, `2`, etc. |
| `erf` | Erf/stand number | `1234` |
| `townshipName` | Township name | `SANDTON` |
| `farmNumber` | Farm number | `123` |
| `companyNumber` | CIPC registration number | `2015/123456/07` |

---

## Integration Checklist

- [ ] Obtain Deeds Office service credentials
- [ ] Request IP whitelisting for 16.28.8.25
- [ ] Test connectivity with `getOfficeRegistryList()` 
- [ ] Get reference data (office codes, property types)
- [ ] Generate SOAP client from WSDL
- [ ] Implement error handling
- [ ] Add retry logic for transient failures
- [ ] Implement credential management (secrets)
- [ ] Add logging and monitoring
- [ ] Create integration tests

---

## SOAP Client Generation

### Using JAX-WS (Java)
```bash
wsimport -keep -p za.gov.deeds.soap.client \
  /path/to/deeds-registration-soap.xml
```

### Using Apache CXF
```bash
wsdl2java -p za.gov.deeds.soap.client \
  -d src/main/java \
  /path/to/deeds-registration-soap.xml
```

### Using Spring Boot
Add to `pom.xml`:
```xml
<plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <schemaDirectory>src/main/resources/wsdl</schemaDirectory>
        <schemaIncludes>
            <include>deeds-registration-soap.xml</include>
        </schemaIncludes>
    </configuration>
</plugin>
```

---

## Testing Commands

### Test from VPC Lambda (after whitelisting)
```bash
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://deedssoap.deeds.gov.za:80"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

### SOAP Request Test (using curl)
```bash
curl -X POST \
  http://deedssoap.deeds.gov.za:80/deeds-registration-soap/ \
  -H 'Content-Type: text/xml' \
  -H 'SOAPAction: getOfficeRegistryList' \
  -d @soap-request.xml
```

---

## Security Considerations

1. **No HTTPS** - Service uses plain HTTP
   - Relies on IP whitelisting for security
   - Credentials transmitted in clear text
   - Only accessible from whitelisted IPs

2. **Credential Management**
   - Store in AWS Secrets Manager
   - Rotate credentials regularly
   - Use IAM roles for Lambda access

3. **Network Security**
   - VPC with NAT Gateway (static IP)
   - Security groups restricting outbound access
   - CloudWatch logging for audit trail

4. **Data Privacy**
   - PII data in responses (names, ID numbers)
   - Implement data masking in logs
   - Comply with POPIA (SA privacy law)

---

## Support

- **Service Owner:** South African Deeds Office
- **Technical Contact:** [To be obtained]
- **Your Static IP:** 16.28.8.25 (must be whitelisted)
- **WSDL Location:** `/docs/deeds-office/deeds-registration-soap.xml`
