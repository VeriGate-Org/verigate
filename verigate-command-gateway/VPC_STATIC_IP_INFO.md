# VPC Static IP Address Information

## Your Static Public IP Address

**Public IP:** `16.28.8.25`

This is the IP address that external services (like the Deeds Office SOAP service) will see when your VPC Lambda functions make outbound connections.

---

## Infrastructure Details

### NAT Gateway
- **NAT Gateway ID:** nat-0d50fbc822aae3169
- **State:** available
- **Public IP:** 16.28.8.25
- **Private IP:** 172.31.3.134
- **Subnet:** subnet-04fcbfa138b0ad8cc (af-south-1a)
- **VPC:** vpc-077e3c6d4f171bd8f

### Elastic IP (EIP)
- **Allocation ID:** eipalloc-0eb2bcd0a74dee9a8
- **Association ID:** eipassoc-0a46d3ec4859502c1
- **Network Interface:** eni-0ac0fbc873453b11e

### Verified By
- **Test Service:** ipify.org
- **Lambda Function:** verigate-connectivity-test-dev
- **Test Date:** October 15, 2025
- **Test Result:** Confirmed IP is 16.28.8.25

---

## For IP Whitelisting Requests

When requesting access to the Deeds Office SOAP service or other external APIs, provide the following information:

**Static IP Address to Whitelist:** `16.28.8.25`

**Region:** Africa (Cape Town) - af-south-1

**IP Type:** AWS Elastic IP attached to NAT Gateway

**Direction:** Outbound connections from VPC resources

---

## Network Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  VPC: vpc-077e3c6d4f171bd8f (172.31.0.0/16)                │
│                                                             │
│  ┌──────────────────────────────────────────────┐          │
│  │ Lambda Function: verigate-connectivity-test-dev         │
│  │ Private Subnet Resources                     │          │
│  └────────────────┬─────────────────────────────┘          │
│                   │                                         │
│                   ↓                                         │
│  ┌────────────────────────────────────────────────┐        │
│  │ NAT Gateway: nat-0d50fbc822aae3169            │        │
│  │ Private IP: 172.31.3.134                      │        │
│  │ Public IP: 16.28.8.25 (Elastic IP)            │        │
│  └────────────────┬───────────────────────────────┘        │
│                   │                                         │
└───────────────────┼─────────────────────────────────────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │  Internet Gateway    │
         │  igw-02bd019cbd5bd2f8a│
         └──────────┬───────────┘
                    │
                    ↓
              [ Internet ]
                    │
                    ↓
    ┌───────────────────────────────┐
    │  External Service             │
    │  (Sees source IP: 16.28.8.25) │
    │                               │
    │  e.g., Deeds Office SOAP API  │
    │  deedssoap.deeds.gov.za       │
    │  164.151.130.226:80           │
    └───────────────────────────────┘
```

---

## Important Notes

1. **This IP is static** - It's an Elastic IP that won't change unless you explicitly release it
2. **All VPC Lambda functions** using the NAT Gateway will use this IP for outbound connections
3. **Billing** - NAT Gateway incurs AWS charges per hour and per GB of data processed
4. **High Availability** - Currently you have one NAT Gateway in one subnet. For production, consider deploying NAT Gateways in multiple availability zones

---

## Template Email for IP Whitelisting Request

```
Subject: IP Whitelisting Request for Deeds Office SOAP Service Access

Dear Deeds Office Support Team,

We are requesting access to the Deeds Office SOAP service 
(http://deedssoap.deeds.gov.za:80) for our verification system.

Please whitelist the following IP address:

IP Address: 16.28.8.25
Organization: [Your Company Name]
Purpose: Property deeds verification integration
Region: South Africa (Cape Town)

This is a static AWS Elastic IP address that will be used for all 
outbound connections from our verification system.

Please confirm once the IP has been whitelisted so we can test 
connectivity.

Thank you for your assistance.

Best regards,
[Your Name]
```

---

## Testing After Whitelisting

Once your IP is whitelisted, test connectivity with:

```bash
# Test from VPC Lambda
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://deedssoap.deeds.gov.za:80"}' \
    response.json && cat response.json | jq -r '.body' | jq .

# Expected result after whitelisting:
# - dns_resolution: SUCCESS
# - socket_connection: SUCCESS  
# - http_request: SUCCESS (or appropriate SOAP response)
```

---

## Additional IPs (If Needed)

Currently, you have one NAT Gateway with one static IP. If you need additional IPs or want redundancy:

### Option 1: Deploy Additional NAT Gateways
- Deploy NAT Gateway in subnet-05eae024ee14aa3ae (af-south-1b)
- Deploy NAT Gateway in subnet-0da53b482e4ca18e2 (af-south-1c)
- Each will get its own Elastic IP

### Option 2: Use Multiple Elastic IPs
- You can associate multiple Elastic IPs with different Lambda functions
- Requires routing configuration

---

## Cost Information

**Current NAT Gateway Cost (af-south-1):**
- Hourly charge: ~$0.045/hour = ~$32.40/month
- Data processing: ~$0.045/GB

**Elastic IP Cost:**
- Free when associated with a running resource
- $0.005/hour if not associated

---

## Quick Reference

| Item | Value |
|------|-------|
| **Your Static IP** | **16.28.8.25** |
| NAT Gateway ID | nat-0d50fbc822aae3169 |
| EIP Allocation ID | eipalloc-0eb2bcd0a74dee9a8 |
| VPC ID | vpc-077e3c6d4f171bd8f |
| Region | af-south-1 (Cape Town) |
| Status | Active & Verified |

---

**Provide this IP (16.28.8.25) to the Deeds Office for whitelisting.**
