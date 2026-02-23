#!/usr/bin/env python3
"""
Lambda-compatible SOAP test for Deeds Office service
This script can be deployed as a Lambda function or run locally via Lambda invoke
"""

import json
import urllib3
from datetime import datetime

def lambda_handler(event, context):
    """
    Test SOAP request to Deeds Office service
    """
    endpoint = "http://deedssoap.deeds.gov.za:80/deeds-registration-soap/"
    
    # Simple SOAP request for getOfficeRegistryList (no credentials needed)
    soap_request = '''<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:enq="http://enquiry.service.registration.deeds.gov.za/">
   <soapenv:Header/>
   <soapenv:Body>
      <enq:getOfficeRegistryList/>
   </soapenv:Body>
</soapenv:Envelope>'''
    
    results = {
        "timestamp": datetime.utcnow().isoformat(),
        "endpoint": endpoint,
        "method": "POST",
        "operation": "getOfficeRegistryList",
        "test_results": {}
    }
    
    print(f"Testing SOAP endpoint: {endpoint}")
    print(f"Operation: getOfficeRegistryList")
    
    # Create HTTP client
    http = urllib3.PoolManager(
        timeout=urllib3.Timeout(connect=10.0, read=30.0),
        retries=urllib3.Retry(total=2, connect=2, read=2, backoff_factor=1)
    )
    
    try:
        # Send SOAP request
        print("Sending SOAP POST request...")
        response = http.request(
            'POST',
            endpoint,
            body=soap_request.encode('utf-8'),
            headers={
                'Content-Type': 'text/xml; charset=utf-8',
                'SOAPAction': 'getOfficeRegistryList',
                'User-Agent': 'VeriGate-SOAP-Test/1.0'
            }
        )
        
        results["test_results"] = {
            "status": "SUCCESS",
            "http_status": response.status,
            "content_length": len(response.data),
            "headers": dict(response.headers)
        }
        
        # Try to parse response
        response_text = response.data.decode('utf-8', errors='replace')
        results["test_results"]["response_preview"] = response_text[:1000]
        
        # Check if it's a SOAP response
        if '<soap' in response_text.lower() or '<envelope' in response_text.lower():
            results["test_results"]["response_type"] = "SOAP"
            print(f"✅ SUCCESS! Received SOAP response")
        elif response.status == 200:
            results["test_results"]["response_type"] = "HTTP_200"
            print(f"✅ SUCCESS! HTTP 200 received")
        else:
            results["test_results"]["response_type"] = "OTHER"
            print(f"⚠️ HTTP {response.status} received")
        
        # Extract key info
        if response.status == 200:
            if 'officeRegistryList' in response_text:
                results["test_results"]["contains_data"] = True
                print("✅ Response contains office registry data!")
            elif 'fault' in response_text.lower():
                results["test_results"]["soap_fault"] = True
                print("⚠️ SOAP Fault received")
        
        print(f"\nHTTP Status: {response.status}")
        print(f"Response length: {len(response.data)} bytes")
        print(f"\nResponse preview (first 500 chars):")
        print(response_text[:500])
        
    except urllib3.exceptions.MaxRetryError as e:
        results["test_results"] = {
            "status": "FAILED",
            "error_type": "MaxRetryError",
            "error": str(e),
            "details": "Connection failed after retries"
        }
        print(f"❌ FAILED: {str(e)}")
        
    except Exception as e:
        results["test_results"] = {
            "status": "FAILED",
            "error_type": type(e).__name__,
            "error": str(e)
        }
        print(f"❌ FAILED: {str(e)}")
    
    # Overall assessment
    test_status = results["test_results"].get("status", "UNKNOWN")
    http_status = results["test_results"].get("http_status", 0)
    
    if test_status == "SUCCESS" and http_status == 200:
        results["overall_status"] = "SUCCESS"
        results["message"] = "SOAP service is accessible and responding!"
    elif test_status == "SUCCESS" and http_status in [401, 403, 500]:
        results["overall_status"] = "PARTIAL_SUCCESS"
        results["message"] = f"Service accessible but returned HTTP {http_status}"
    else:
        results["overall_status"] = "FAILED"
        results["message"] = "Could not connect to SOAP service"
    
    print("\n" + "="*80)
    print("SOAP TEST RESULTS")
    print("="*80)
    print(json.dumps(results, indent=2))
    print("="*80)
    
    return {
        'statusCode': 200 if test_status == "SUCCESS" else 500,
        'body': json.dumps(results, indent=2)
    }

# For local testing
if __name__ == "__main__":
    class MockContext:
        function_name = "local-soap-test"
        invoked_function_arn = "arn:aws:lambda:af-south-1:123456789012:function:local-soap-test"
    
    result = lambda_handler({}, MockContext())
    print("\nTest completed.")
