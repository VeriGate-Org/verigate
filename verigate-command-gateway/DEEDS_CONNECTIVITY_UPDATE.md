# Deeds Office Connectivity - Update Report

**Date:** October 17, 2025  
**Test Times:** 11:12 UTC (Success) → 11:29-11:32 UTC (Failing Again)

---

## Current Status: ⚠️ INTERMITTENT CONNECTIVITY

### Timeline of Test Results

| Time (UTC) | DNS | TCP Connection | HTTP Request | Status |
|------------|-----|----------------|--------------|--------|
| 11:12 | ✅ SUCCESS | ✅ **SUCCESS (51ms)** | ⚠️ Reset | **Working!** |
| 11:29 | ✅ SUCCESS | ❌ FAILED | ❌ FAILED | Blocked |
| 11:32 | ✅ SUCCESS | ❌ FAILED (error 11) | ❌ FAILED | Blocked |

---

## What This Tells Us

### The Good News
At **11:12 UTC**, your IP (16.28.8.25) was successfully connecting:
- TCP connection established in 51ms
- Server was accepting connections
- This proves whitelisting CAN work

### The Current Issue
As of **11:29 UTC** (17 minutes later), connections are timing out again:
- TCP connection fails with error code 11
- Back to the same behavior as before whitelisting
- DNS still works, but TCP connections are blocked

---

## Possible Explanations

### 1. **Time-Based Access Restrictions** (Most Likely)
The Deeds Office may have implemented time-based firewall rules:
- ✅ Access allowed during business hours only
- ❌ Access blocked outside business hours
- South African business hours: roughly 08:00-17:00 SAST (06:00-15:00 UTC)

**Current time:** 11:32 UTC = 13:32 SAST (1:32 PM South African time)
- This IS within business hours, so this may not be the issue

### 2. **Intermittent Service Availability**
- The service may be temporarily down for maintenance
- Server might be restarting
- Load balancer could be failing health checks

### 3. **Conditional Whitelisting**
- IP whitelisting might have specific conditions
- Could require additional authentication at network level
- VPN connection might be needed in addition to IP whitelisting

### 4. **Rate Limiting**
- Too many connection attempts in short time
- Temporary ban after multiple requests
- Anti-DDoS measures triggered

### 5. **Incomplete Whitelisting Configuration**
- Whitelisting might be partially configured
- Could be active on some servers but not others (if load balanced)
- Configuration might not have fully propagated

---

## Evidence Analysis

### Why We Saw Success at 11:12 UTC

The TCP connection success at 11:12 UTC was **not a false positive**:

```json
{
  "status": "SUCCESS",
  "port": 80,
  "connection_time_seconds": 0.051
}
```

This proves:
- The firewall briefly allowed your IP
- Network routing was correct
- The server was reachable and accepting connections
- The infrastructure setup is valid

### Current Failure Pattern

```json
{
  "status": "FAILED",
  "port": 80,
  "error_code": 11,
  "details": "Socket connection refused or blocked"
}
```

Error code 11 (EAGAIN/EWOULDBLOCK) typically means:
- Connection attempt is being actively refused
- Firewall is blocking the connection
- Service is unavailable

---

## Recommended Actions

### 1. Contact Deeds Office Support (**URGENT**)

Inform them of this intermittent connectivity and ask:

**Questions to Ask:**
```
1. Are there time-based restrictions on the firewall?
   - Business hours only?
   - Specific time windows?

2. Is the whitelisting fully active and propagated?
   - We saw success at 11:12 UTC, but failures after 11:29 UTC
   - Is this expected behavior?

3. Does the service require additional authentication?
   - VPN connection needed?
   - Additional network-level auth?

4. Is there rate limiting?
   - How many requests per minute are allowed?
   - Are we triggering anti-DDoS measures?

5. Is the service load-balanced?
   - Could some servers have the whitelist while others don't?
   - How long for configuration to fully propagate?

6. What is the service availability schedule?
   - Is it 24/7 or specific hours?
   - Any planned maintenance windows?
```

### 2. Document the Evidence

Send them this information:
```
Your Static IP: 16.28.8.25
Service: deedssoap.deeds.gov.za (164.151.130.226)
Port: 80

Timeline:
- 2025-10-17 11:12 UTC: ✅ TCP connection SUCCESS (51ms)
- 2025-10-17 11:29 UTC: ❌ TCP connection FAILED (timeout)
- 2025-10-17 11:32 UTC: ❌ TCP connection FAILED (error 11)

Request: Please investigate why connectivity was working at 11:12 but 
failing 17 minutes later. Is this expected behavior?
```

### 3. Schedule Another Test

Try testing at different times:
- **Morning** (08:00-10:00 SAST / 06:00-08:00 UTC)
- **Midday** (12:00-14:00 SAST / 10:00-12:00 UTC)  
- **Afternoon** (15:00-17:00 SAST / 13:00-15:00 UTC)
- **Evening** (19:00-21:00 SAST / 17:00-19:00 UTC)

This will help determine if there's a time-based pattern.

### 4. Monitor for Longer Period

Run periodic tests to identify patterns:
```bash
# Test every 15 minutes for 4 hours
for i in {1..16}; do
  echo "Test $i at $(date)"
  aws lambda invoke \
      --function-name verigate-connectivity-test-dev \
      --region af-south-1 \
      --payload '{"url":"http://deedssoap.deeds.gov.za"}' \
      test_$i.json
  sleep 900  # 15 minutes
done
```

---

## Quick Reference Commands

### Test Connectivity Now
```bash
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://deedssoap.deeds.gov.za"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

### Check for Success Pattern
Look for these indicators of successful whitelisting:
```json
{
  "socket_connection": {
    "status": "SUCCESS",
    "connection_time_seconds": 0.05
  }
}
```

### Check Your Static IP
```bash
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --payload '{"url":"https://api.ipify.org?format=json"}' \
    response.json && cat response.json | jq -r '.body' | jq '.tests.http_request.response_preview'
```

---

## Summary

The connectivity test at 11:12 UTC showed that your IP **can** connect when the whitelisting is active, which is excellent progress. However, the connection started failing again just 17 minutes later, suggesting either time-based restrictions, service intermittency, or incomplete configuration.

The fact that you achieved a successful TCP connection proves your infrastructure is correctly set up and the whitelisting concept is working. Now you need to work with the Deeds Office to understand why it's intermittent and ensure consistent access.

**Next Step:** Contact the Deeds Office support team immediately with the evidence from this report and request clarification on the access restrictions and whitelisting status.

---

## Files Referenced
- `DEEDS_CONNECTIVITY_TEST_RESULTS.md` - Initial test results (October 16)
- `DEEDS_CONNECTIVITY_SUCCESS.md` - Success report (11:12 UTC)
- `DEEDS_CONNECTIVITY_UPDATE.md` - This document
- `scripts/lambda-soap-test.py` - SOAP test script (created but not deployed)
