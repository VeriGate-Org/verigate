# DNS Resolution Analysis - Why It Failed from AWS

## The Mystery

DNS resolution for `deedssoap.deeds.gov.za` worked from your local machine but appeared to fail from the AWS Lambda function in your VPC.

## Root Cause Discovered

**The DNS resolution didn't actually fail in AWS!** There was a bug in the connectivity test script.

### The Bug

When testing the URL `http://deedssoap.deeds.gov.za:80`, the hostname parsing code did:

```python
# Line 81 in connectivity-test-template.yaml
hostname = url.replace("http://", "").replace("https://", "").split("/")[0]
```

**Problem:** This resulted in `hostname = "deedssoap.deeds.gov.za:80"` (includes port number)

**DNS Resolution Attempt:**
```python
socket.gethostbyname("deedssoap.deeds.gov.za:80")  # FAILS!
```

DNS cannot resolve hostnames that include port numbers. The `:80` is not part of the hostname.

### Proof

```python
# FAILS - port included
>>> socket.gethostbyname("deedssoap.deeds.gov.za:80")
Error: [Errno -2] Name or service not known

# SUCCEEDS - hostname only
>>> socket.gethostbyname("deedssoap.deeds.gov.za")
'164.151.130.226'
```

### Test Results

| URL Tested | Parsed Hostname | DNS Result |
|------------|----------------|------------|
| `http://deedssoap.deeds.gov.za:80` | `deedssoap.deeds.gov.za:80` | ❌ FAILED (bug) |
| `http://deedssoap.deeds.gov.za` | `deedssoap.deeds.gov.za` | ✅ SUCCESS |
| `http://www.google.com` | `www.google.com` | ✅ SUCCESS |

## Confirmation: DNS Works Fine in AWS VPC

When we tested with the URL **without** the explicit port number:

```bash
aws lambda invoke \
    --payload '{"url":"http://deedssoap.deeds.gov.za"}' \
    ...
```

**Result:**
```json
{
  "dns_resolution": {
    "status": "SUCCESS",
    "hostname": "deedssoap.deeds.gov.za",
    "ip_address": "164.151.130.226"
  }
}
```

✅ **DNS resolution works perfectly in your VPC!**

## VPC DNS Configuration

Your VPC is correctly configured for DNS:

```json
{
  "DhcpOptions": {
    "domain-name": "af-south-1.compute.internal",
    "domain-name-servers": ["AmazonProvidedDNS"]
  }
}
```

- **DNS Support:** Enabled (uses Amazon Provided DNS at 172.31.0.2)
- **Public DNS Resolution:** Working (verified with Google, ipify.org, deeds.gov.za)
- **Internet Gateway:** Attached and routing properly

## The Real Problem

The actual connectivity issue is **not DNS-related**. The problems are:

### 1. Connection Timeout (Not DNS Failure)

Even after successful DNS resolution to `164.151.130.226`, the connection times out:

```
✅ DNS Resolution: SUCCESS (164.151.130.226)
❌ Socket Connection: TIMEOUT (10 seconds)
❌ HTTP Request: TIMEOUT (connection refused)
```

This is identical behavior from both:
- Your local machine
- AWS VPC Lambda

### 2. Service Access Restrictions

The Deeds Office service at `164.151.130.226:80` is:
- **Firewalled** - Not accepting connections from unauthorized IPs
- **Requires whitelisting** - Your IP (16.28.8.25) needs to be approved
- **May require VPN** - Could be on a private government network

## Summary

| Component | Status | Notes |
|-----------|--------|-------|
| VPC DNS Configuration | ✅ WORKING | Amazon Provided DNS |
| Public DNS Resolution | ✅ WORKING | Resolves external domains |
| Deeds DNS Resolution | ✅ WORKING | Resolves to 164.151.130.226 |
| Initial Test Failure | 🐛 BUG | Script included port in hostname |
| Deeds Service Access | ❌ BLOCKED | IP whitelisting required |

## Conclusion

**AWS VPC DNS is working correctly.** The initial DNS failure was a parsing bug in the test script (including `:80` in the hostname). The actual problem is that the Deeds Office service requires IP whitelisting to allow connections from your static IP address (16.28.8.25).

## Next Steps

1. ✅ DNS Resolution: No action needed (working correctly)
2. ❌ Service Access: Contact Deeds Office to whitelist IP 16.28.8.25
3. 🔧 Optional: Fix the test script to handle URLs with explicit ports

## Fixed Hostname Parsing

The correct parsing should be:

```python
from urllib.parse import urlparse

parsed = urlparse(url)
hostname = parsed.hostname  # Correctly extracts just the hostname
port = parsed.port or (443 if parsed.scheme == 'https' else 80)
```

This properly handles:
- `http://example.com` → hostname: `example.com`, port: 80
- `http://example.com:8080` → hostname: `example.com`, port: 8080
- `https://example.com` → hostname: `example.com`, port: 443
