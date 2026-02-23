#!/usr/bin/env python3
"""
Lambda function to test outbound connectivity to South African Deeds Office API
URL: http://enquiry.service.registration.deeds.gov.za/
"""

import json
import urllib3
import socket
import time
from datetime import datetime

def lambda_handler(event, context):
    """
    Test connectivity to the Deeds Office URL from within VPC
    """
    url = "http://enquiry.service.registration.deeds.gov.za/"
    results = {
        "timestamp": datetime.utcnow().isoformat(),
        "url": url,
        "tests": {}
    }
    
    # Test 1: DNS Resolution
    print(f"Testing DNS resolution for: {url}")
    hostname = url.replace("http://", "").replace("/", "")
    try:
        ip_address = socket.gethostbyname(hostname)
        results["tests"]["dns_resolution"] = {
            "status": "SUCCESS",
            "hostname": hostname,
            "ip_address": ip_address
        }
        print(f"DNS Resolution SUCCESS: {hostname} -> {ip_address}")
    except socket.gaierror as e:
        results["tests"]["dns_resolution"] = {
            "status": "FAILED",
            "hostname": hostname,
            "error": str(e)
        }
        print(f"DNS Resolution FAILED: {str(e)}")
    
    # Test 2: HTTP GET Request
    print(f"Testing HTTP GET request to: {url}")
    http = urllib3.PoolManager(
        timeout=urllib3.Timeout(connect=10.0, read=30.0),
        retries=urllib3.Retry(total=3, connect=3, read=3)
    )
    
    try:
        start_time = time.time()
        response = http.request('GET', url, 
                               headers={'User-Agent': 'VeriGate-Connectivity-Test/1.0'},
                               redirect=True)
        elapsed_time = time.time() - start_time
        
        results["tests"]["http_request"] = {
            "status": "SUCCESS",
            "http_status": response.status,
            "response_time_seconds": round(elapsed_time, 3),
            "content_length": len(response.data),
            "headers": dict(response.headers)
        }
        
        # Try to get a snippet of the response
        try:
            response_text = response.data.decode('utf-8')[:500]
            results["tests"]["http_request"]["response_preview"] = response_text
        except:
            results["tests"]["http_request"]["response_preview"] = "Unable to decode response"
        
        print(f"HTTP Request SUCCESS: Status {response.status}, Time: {elapsed_time:.3f}s")
        
    except urllib3.exceptions.MaxRetryError as e:
        results["tests"]["http_request"] = {
            "status": "FAILED",
            "error_type": "MaxRetryError",
            "error": str(e),
            "details": "Connection failed after retries - possible network/firewall issue"
        }
        print(f"HTTP Request FAILED (MaxRetryError): {str(e)}")
        
    except urllib3.exceptions.ConnectTimeoutError as e:
        results["tests"]["http_request"] = {
            "status": "FAILED",
            "error_type": "ConnectTimeoutError",
            "error": str(e),
            "details": "Connection timed out - check firewall/security groups"
        }
        print(f"HTTP Request FAILED (Timeout): {str(e)}")
        
    except Exception as e:
        results["tests"]["http_request"] = {
            "status": "FAILED",
            "error_type": type(e).__name__,
            "error": str(e)
        }
        print(f"HTTP Request FAILED: {str(e)}")
    
    # Test 3: Socket Connection Test (low-level)
    print(f"Testing TCP socket connection to: {hostname}:80")
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(10)
        start_time = time.time()
        result = sock.connect_ex((hostname, 80))
        elapsed_time = time.time() - start_time
        sock.close()
        
        if result == 0:
            results["tests"]["socket_connection"] = {
                "status": "SUCCESS",
                "port": 80,
                "connection_time_seconds": round(elapsed_time, 3)
            }
            print(f"Socket Connection SUCCESS: Time: {elapsed_time:.3f}s")
        else:
            results["tests"]["socket_connection"] = {
                "status": "FAILED",
                "port": 80,
                "error_code": result,
                "details": "Socket connection refused or blocked"
            }
            print(f"Socket Connection FAILED: Error code {result}")
            
    except Exception as e:
        results["tests"]["socket_connection"] = {
            "status": "FAILED",
            "error": str(e)
        }
        print(f"Socket Connection FAILED: {str(e)}")
    
    # Overall Assessment
    all_tests_passed = all(
        test.get("status") == "SUCCESS" 
        for test in results["tests"].values()
    )
    
    results["overall_status"] = "SUCCESS" if all_tests_passed else "FAILED"
    results["vpc_environment"] = {
        "lambda_function": context.function_name if context else "local-test",
        "aws_region": context.invoked_function_arn.split(":")[3] if context else "unknown"
    }
    
    # Pretty print results
    print("\n" + "="*80)
    print("CONNECTIVITY TEST RESULTS")
    print("="*80)
    print(json.dumps(results, indent=2))
    print("="*80)
    
    return {
        'statusCode': 200 if all_tests_passed else 500,
        'body': json.dumps(results, indent=2)
    }

# For local testing
if __name__ == "__main__":
    class MockContext:
        function_name = "local-test"
        invoked_function_arn = "arn:aws:lambda:af-south-1:123456789012:function:local-test"
    
    result = lambda_handler({}, MockContext())
    print("\nTest completed. Exit code:", 0 if result['statusCode'] == 200 else 1)
