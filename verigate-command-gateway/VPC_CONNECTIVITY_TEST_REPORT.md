# VPC Outbound Connectivity Test Report
**Date:** October 15, 2025  
**Test Target:** South African Deeds Office SOAP Service  
**VPC:** vpc-077e3c6d4f171bd8f (af-south-1)

---

## Executive Summary

✅ **VPC Internet Connectivity:** WORKING  
❌ **Deeds Office Service Connectivity:** FAILED  
⚠️ **Root Cause:** The Deeds Office service appears to be **network-restricted or unavailable**

---

## Test Results

### 1. VPC Internet Connectivity Test
**Target:** http://www.google.com  
**Result:** ✅ SUCCESS

```json
{
  "dns_resolution": "SUCCESS - 142.251.47.100",
  "socket_connection": "SUCCESS - 0.02s",
  "http_request": "SUCCESS - HTTP 200 (0.585s)"
}
```

**Conclusion:** Your VPC Lambda has full internet connectivity through the Internet Gateway.

---

### 2. Deeds Office URL Test #1
**Target:** http://enquiry.service.registration.deeds.gov.za/  
**Result:** ❌ DNS RESOLUTION FAILED

```
Error: [Errno -2] Name or service not known
```

**Conclusion:** This domain does not exist in DNS. Invalid URL.

---

### 3. Deeds Office URL Test #2
**Target:** http://deedssoap.deeds.gov.za:80  
**Result:** ❌ CONNECTION TIMEOUT

#### From VPC Lambda:
- DNS Resolution: ❌ FAILED
- Socket Connection: ❌ FAILED (timeout after retries)
- HTTP Request: ❌ FAILED (connect timeout 10s)

#### From Local Machine:
- DNS Resolution: ✅ SUCCESS → 164.151.130.226
- Socket Connection: ❌ FAILED (timeout after 10s)
- HTTP Request: ❌ FAILED (timeout)

#### Direct IP Test (164.151.130.226:80):
- From VPC Lambda: ❌ CONNECTION TIMEOUT
- From Local Machine: ❌ CONNECTION TIMEOUT

**Conclusion:** The service at `deedssoap.deeds.gov.za` (164.151.130.226:80) is **not accessible** from either your VPC or your local network.

---

## Infrastructure Analysis

### VPC Configuration
- **VPC ID:** vpc-077e3c6d4f171bd8f
- **Region:** af-south-1 (Africa - Cape Town)
- **Subnets:** 3 public subnets (af-south-1a, af-south-1b, af-south-1c)
- **Internet Gateway:** igw-02bd019cbd5bd2f8a ✅
- **Security Group:** sg-074a6fc8e06f6fe4e
  - Egress: All traffic to 0.0.0.0/0 ✅

### Lambda Configuration
- **Function:** verigate-connectivity-test-dev
- **Runtime:** Python 3.11
- **VPC Enabled:** Yes ✅
- **Timeout:** 60 seconds
- **Internet Access:** Confirmed working ✅

---

## Root Cause Analysis

The Deeds Office service is **unreachable** for one of these reasons:

### 1. **Network Access Restrictions (Most Likely)**
   - The service may require **VPN access** to South African government networks
   - The service may be **IP whitelisted** - only specific IP ranges allowed
   - The service may be **geographically restricted** to South Africa only

### 2. **Service Configuration Issues**
   - Port 80 may not be the correct port
   - The service might use HTTPS (port 443) instead
   - The service might be temporarily down

### 3. **Firewall/Security**
   - Government firewall blocking external access
   - AWS IP ranges might be blocked
   - The service requires authentication before accepting connections

---

## Recommendations

### Immediate Actions:

1. **Verify the Correct Service URL and Port**
   ```bash
   # Try HTTPS
   curl -v https://deedssoap.deeds.gov.za
   
   # Try common SOAP ports
   curl -v http://deedssoap.deeds.gov.za:8080
   curl -v http://deedssoap.deeds.gov.za:443
   ```

2. **Check with Deeds Office Provider**
   - Confirm the service endpoint is correct
   - Ask about IP whitelisting requirements
   - Ask about VPN or network access requirements
   - Request firewall rules or access documentation

3. **Test from South African Network**
   - If possible, test from a server located in South Africa
   - The service may be geographically restricted

4. **Request Network Access**
   - Provide your AWS NAT Gateway/Lambda public IP addresses for whitelisting
   - Request VPN credentials if required
   - Get documentation on network access requirements

### Alternative Solutions:

1. **VPN Integration**
   - Set up AWS Client VPN or Site-to-Site VPN to South African government network
   - Route Deeds Office traffic through VPN tunnel

2. **IP Whitelisting**
   - Deploy NAT Gateway in your VPC to get static public IP
   - Request IP whitelisting for that static IP
   - Route Lambda traffic through NAT Gateway

3. **Proxy Server**
   - Deploy proxy server in whitelisted network
   - Configure Lambda to connect through proxy

---

## Test Commands Used

The following Lambda function is deployed and ready for testing:

```bash
# Test default URL
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    response.json && cat response.json | jq .

# Test custom URL
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://your-url-here"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

---

## Next Steps

1. ✅ **VPC Internet connectivity is confirmed working**
2. ❌ **Deeds Office service requires investigation:**
   - Contact Deeds Office API support
   - Verify correct endpoint and port
   - Determine network access requirements
   - Arrange IP whitelisting or VPN access if needed

---

## Files Created

- `/scripts/test-deeds-connectivity.py` - Standalone connectivity test script
- `/scripts/connectivity-test-template.yaml` - CloudFormation template
- `/scripts/deploy-connectivity-test.sh` - Deployment script
- Lambda Function: `verigate-connectivity-test-dev` (deployed in VPC)

The Lambda function can test any URL you provide and will remain available for future connectivity testing.
