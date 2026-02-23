#!/bin/bash
#
# Test Deeds Office SOAP Service with a simple SOAP request
# This tests if the service is accessible and responding to SOAP requests
#

set -e

echo "=========================================="
echo "Deeds Office SOAP Service Test"
echo "=========================================="
echo ""

# Service endpoint
ENDPOINT="http://deedssoap.deeds.gov.za:80/deeds-registration-soap/"

# Create a simple SOAP request to getOfficeRegistryList (no params needed)
SOAP_REQUEST='<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Header/>
   <soapenv:Body>
      <enq:getOfficeRegistryList/>
   </soapenv:Body>
</soapenv:Envelope>'

echo "Testing SOAP endpoint: $ENDPOINT"
echo "Method: getOfficeRegistryList (reference data - no credentials needed)"
echo ""

# Make the SOAP request
echo "Sending SOAP request..."
echo ""

RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\nTIME_TOTAL:%{time_total}\n" \
  -X POST \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: getOfficeRegistryList" \
  --connect-timeout 30 \
  --max-time 60 \
  -d "$SOAP_REQUEST" \
  "$ENDPOINT" 2>&1)

# Extract HTTP code and time
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
TIME_TOTAL=$(echo "$RESPONSE" | grep "TIME_TOTAL:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d' | sed '/TIME_TOTAL:/d')

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "RESULT:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "HTTP Status Code: $HTTP_CODE"
echo "Response Time: ${TIME_TOTAL}s"
echo ""

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ SUCCESS! Service is accessible and responding!"
    echo ""
    echo "Response Preview:"
    echo "$RESPONSE_BODY" | head -20
    echo ""
    echo "Full response saved to: deeds-soap-response.xml"
    echo "$RESPONSE_BODY" > deeds-soap-response.xml
elif [ "$HTTP_CODE" = "500" ]; then
    echo "⚠️  HTTP 500 - SOAP Fault (service is working but request may have errors)"
    echo ""
    echo "Response:"
    echo "$RESPONSE_BODY" | head -30
elif [ -z "$HTTP_CODE" ]; then
    echo "❌ FAILED - No response received"
    echo ""
    echo "Error output:"
    echo "$RESPONSE_BODY"
else
    echo "⚠️  HTTP $HTTP_CODE - Unexpected response"
    echo ""
    echo "Response:"
    echo "$RESPONSE_BODY" | head -30
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
