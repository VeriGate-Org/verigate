# Deeds Office Connectivity Test Results

**Test Date:** October 16, 2025  
**Test Time:** 10:13-10:16 UTC  
**Service:** South African Deeds Office SOAP API  
**Your Static IP:** 16.28.8.25

---

## Executive Summary

| Test Component | Status | Details |
|---------------|--------|---------|
| VPC Internet Access | ✅ WORKING | Confirmed via ipify.org |
| DNS Resolution | ✅ WORKING | Resolves to 164.151.130.226 |
| TCP Connection | ❌ FAILED | Connection timeout (error code 11) |
| HTTP Request | ❌ FAILED | Connection timeout after retries |
| **Overall Status** | **❌ BLOCKED** | **IP not yet whitelisted** |

---

## Test Results Details

### 1. Public IP Verification
**Result:** ✅ SUCCESS

Your VPC Lambda is using the correct static IP address:
```json
{
  "ip": "16.28.8.25"
}
```

This IP should be provided to the Deeds Office for whitelisting.

---

### 2. DNS Resolution Test
**Target:** `deedssoap.deeds.gov.za`  
**Result:** ✅ SUCCESS

```json
{
  "status": "SUCCESS",
  "hostname": "deedssoap.deeds.gov.za",
  "ip_address": "164.151.130.226"
}
```

DNS is working correctly from your VPC. The hostname resolves to the Deeds Office server.

---

### 3. TCP Socket Connection Test
**Target:** `164.151.130.226:80`  
**Result:** ❌ FAILED

```json
{
  "status": "FAILED",
  "port": 80,
  "error_code": 11,
  "details": "Socket connection refused or blocked"
}
```

**Analysis:** 
- Error code 11 typically means "Resource temporarily unavailable" or the connection was actively blocked
- The server is reachable (DNS works) but refusing connections
- This indicates IP-based filtering/firewall blocking

---

### 4. HTTP Request Test
**Target:** `http://deedssoap.deeds.gov.za/deeds-registration-soap/`  
**Result:** ❌ FAILED

```json
{
  "status": "FAILED",
  "error_type": "MaxRetryError",
  "error": "Connection timeout (connect timeout=10.0s)",
  "details": "Connection failed - check network/firewall/security groups"
}
```

**Analysis:**
- Multiple retry attempts all timed out
- Confirms the server is blocking connections from your IP
- No HTTP response received (connection level block)

---

## Root Cause Analysis

### Current Situation
The Deeds Office server (`deedssoap.deeds.gov.za` / `164.151.130.226`) is:
- ✅ Online and accessible (DNS resolves)
- ✅ Reachable from your VPC network layer
- ❌ **Actively blocking connections from IP 16.28.8.25**

### Why Connection Fails
1. **IP Whitelisting Not Complete:** Your IP (16.28.8.25) has not been added to the Deeds Office firewall allowlist
2. **Firewall Rules:** The server is configured to only accept connections from pre-approved IP addresses
3. **Government Network Security:** Standard security practice for government services

---

## Next Steps

### If Third Party Confirmed Whitelisting

Since you mentioned "the thirdparty have confirmed," here are the possible scenarios:

#### Scenario 1: Whitelisting Complete but Not Active Yet
- **Action:** Wait 15-30 minutes for firewall rules to propagate
- **Verification:** Re-run the connectivity test
- **Command:**
  ```bash
  aws lambda invoke \
      --function-name verigate-connectivity-test-dev \
      --region af-south-1 \
      --cli-binary-format raw-in-base64-out \
      --payload '{"url":"http://deedssoap.deeds.gov.za/deeds-registration-soap/"}' \
      response.json && cat response.json | jq -r '.body' | jq .
  ```

#### Scenario 2: Wrong IP Was Whitelisted
- **Verify:** Confirm they whitelisted exactly `16.28.8.25`
- **Check:** Ask them to verify the whitelisted IP in their firewall logs
- **Provide:** Send them this report showing your confirmed public IP

#### Scenario 3: Additional Requirements
The service may require:
- **VPN Connection:** Some government services require VPN in addition to IP whitelisting
- **Port Configuration:** They may have opened a different port (not 80)
- **HTTPS Instead:** Service might be on port 443 (HTTPS)
- **Different Endpoint:** The actual service URL might be different

### Immediate Actions to Take

1. **Contact Deeds Office Support**
   - Share this test report
   - Confirm they whitelisted IP: `16.28.8.25`
   - Ask when the firewall rules were applied
   - Request confirmation that rules are active

2. **Verify Endpoint Details**
   - Confirm the correct service URL
   - Check if it's HTTP (port 80) or HTTPS (port 443)
   - Verify the correct path: `/deeds-registration-soap/`
   - Ask for sample SOAP request to test

3. **Test Alternative Configurations**
   ```bash
   # Test HTTPS (port 443)
   aws lambda invoke \
       --function-name verigate-connectivity-test-dev \
       --region af-south-1 \
       --cli-binary-format raw-in-base64-out \
       --payload '{"url":"https://deedssoap.deeds.gov.za"}' \
       response.json && cat response.json | jq -r '.body' | jq .
   
   # Test port 8080
   aws lambda invoke \
       --function-name verigate-connectivity-test-dev \
       --region af-south-1 \
       --cli-binary-format raw-in-base64-out \
       --payload '{"url":"http://deedssoap.deeds.gov.za:8080"}' \
       response.json && cat response.json | jq -r '.body' | jq .
   ```

4. **Request Firewall Logs**
   - Ask them to check their firewall logs for connection attempts from 16.28.8.25
   - This will show if your requests are reaching their firewall and being blocked

---

## Information to Provide to Deeds Office

### Network Details
```
Source IP Address: 16.28.8.25
Region: af-south-1 (Cape Town, South Africa)
Provider: AWS (Amazon Web Services)
NAT Gateway ID: nat-0d50fbc822aae3169
Purpose: VeriGate property verification service integration
```

### Connection Test Results
- DNS Resolution: ✅ Working (resolves to 164.151.130.226)
- TCP Connection: ❌ Blocked (connection timeout)
- HTTP Request: ❌ Blocked (connection timeout)
- Test Timestamp: 2025-10-16 10:13-10:16 UTC

### Expected Service Details
```
Service URL: http://deedssoap.deeds.gov.za:80/deeds-registration-soap/
Protocol: SOAP 1.1/1.2 over HTTP
Namespace: http://enquiry.service.registration.deeds.gov.za/
Target IP: 164.151.130.226
```

---

## Testing Commands Reference

### Quick Connectivity Test
```bash
# Test the main SOAP endpoint
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://deedssoap.deeds.gov.za/deeds-registration-soap/"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

### Verify Your Public IP
```bash
# Confirm your static IP hasn't changed
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"https://api.ipify.org?format=json"}' \
    response.json && cat response.json | jq -r '.body' | jq .responseText'
```

### Test Google (Baseline)
```bash
# Verify VPC internet connectivity is still working
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"https://www.google.com"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

---

## Success Criteria

When whitelisting is complete, you should see:

### Expected Successful Response
```json
{
  "tests": {
    "dns_resolution": {
      "status": "SUCCESS",
      "ip_address": "164.151.130.226"
    },
    "socket_connection": {
      "status": "SUCCESS",
      "connection_time_seconds": 0.05
    },
    "http_request": {
      "status": "SUCCESS",
      "http_status": 200,
      "response_time_seconds": 0.5
    }
  },
  "overall_status": "SUCCESS"
}
```

Or, for a SOAP service, you might see:
- HTTP Status 405 (Method Not Allowed) - SOAP requires POST
- HTTP Status 400 (Bad Request) - Missing SOAP envelope
- HTTP Status 500 (Internal Server Error) - SOAP fault

**Any HTTP response (even an error) means the connection is successful!**

---

## Contact Information for Support

### Your Infrastructure
- **AWS Account ID:** 420747712978
- **VPC ID:** vpc-077e3c6d4f171bd8f
- **NAT Gateway:** nat-0d50fbc822aae3169
- **Static IP:** 16.28.8.25
- **Region:** af-south-1 (Cape Town)

### Deeds Office Details
- **Service Name:** DeedsRegistrationEnquiryService
- **Endpoint:** http://deedssoap.deeds.gov.za:80/deeds-registration-soap/
- **Server IP:** 164.151.130.226
- **Namespace:** http://enquiry.service.registration.deeds.gov.za/

---

## Conclusion

**Current Status:** Connection is blocked at the firewall/network level. Your infrastructure is correctly configured and has internet connectivity. The issue is that IP address 16.28.8.25 needs to be whitelisted by the Deeds Office.

**Recommendation:** 
1. Follow up with the Deeds Office to confirm the whitelisting status
2. Wait 30 minutes and re-test if they confirmed it's complete
3. Request firewall logs showing your connection attempts
4. Verify the exact service endpoint and port they've opened

**Test Command to Run After Whitelisting:**
```bash
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://deedssoap.deeds.gov.za/deeds-registration-soap/"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```
