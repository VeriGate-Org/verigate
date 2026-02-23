# Quick Reference: VPC Connectivity Testing

## Deployed Lambda Function
**Name:** `verigate-connectivity-test-dev`  
**Region:** af-south-1  
**VPC:** vpc-077e3c6d4f171bd8f  

## Test Any URL from Your VPC

```bash
# Basic test (uses default URL from environment)
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    response.json && cat response.json | jq .

# Test specific URL
aws lambda invoke \
    --function-name verigate-connectivity-test-dev \
    --region af-south-1 \
    --cli-binary-format raw-in-base64-out \
    --payload '{"url":"http://your-url-here"}' \
    response.json && cat response.json | jq -r '.body' | jq .
```

## Test Results Summary

| Test | URL | Result |
|------|-----|--------|
| VPC Internet | http://www.google.com | ✅ SUCCESS |
| Deeds Office | http://deedssoap.deeds.gov.za:80 | ❌ TIMEOUT |
| Direct IP | http://164.151.130.226:80 | ❌ TIMEOUT |

## What This Means

✅ **Your VPC is correctly configured** with internet access  
❌ **The Deeds Office service is network-restricted**

## Next Action Required

Contact the Deeds Office API provider to:
1. Confirm the correct service endpoint
2. Request IP whitelisting (if needed)
3. Get VPN access details (if needed)
4. Obtain network access documentation

## Clean Up (Optional)

To remove the test Lambda function:
```bash
aws cloudformation delete-stack \
    --stack-name verigate-connectivity-test \
    --region af-south-1
```
