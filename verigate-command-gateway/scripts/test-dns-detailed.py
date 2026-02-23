#!/usr/bin/env python3
"""
Advanced DNS resolution test for Lambda
Tests multiple DNS methods and servers
"""

import json
import socket
import os
from datetime import datetime

def test_dns_resolution(hostname):
    """Test DNS resolution using multiple methods"""
    results = {
        "hostname": hostname,
        "timestamp": datetime.utcnow().isoformat(),
        "tests": {}
    }
    
    # Method 1: socket.gethostbyname (uses system resolver)
    print(f"[Method 1] socket.gethostbyname('{hostname}')")
    try:
        ip = socket.gethostbyname(hostname)
        results["tests"]["gethostbyname"] = {
            "status": "SUCCESS",
            "ip_address": ip,
            "method": "System resolver (libc)"
        }
        print(f"  ✓ SUCCESS: {ip}")
    except socket.gaierror as e:
        results["tests"]["gethostbyname"] = {
            "status": "FAILED",
            "error": str(e),
            "error_code": e.errno,
            "method": "System resolver (libc)"
        }
        print(f"  ✗ FAILED: {e}")
    
    # Method 2: socket.getaddrinfo (more detailed)
    print(f"[Method 2] socket.getaddrinfo('{hostname}')")
    try:
        addr_info = socket.getaddrinfo(hostname, None)
        ips = list(set([addr[4][0] for addr in addr_info]))
        results["tests"]["getaddrinfo"] = {
            "status": "SUCCESS",
            "ip_addresses": ips,
            "count": len(ips),
            "method": "Extended resolver"
        }
        print(f"  ✓ SUCCESS: {ips}")
    except socket.gaierror as e:
        results["tests"]["getaddrinfo"] = {
            "status": "FAILED",
            "error": str(e),
            "error_code": e.errno,
            "method": "Extended resolver"
        }
        print(f"  ✗ FAILED: {e}")
    
    # Method 3: socket.gethostbyname_ex (extended info)
    print(f"[Method 3] socket.gethostbyname_ex('{hostname}')")
    try:
        hostname_result, aliaslist, ipaddrlist = socket.gethostbyname_ex(hostname)
        results["tests"]["gethostbyname_ex"] = {
            "status": "SUCCESS",
            "canonical_hostname": hostname_result,
            "aliases": aliaslist,
            "ip_addresses": ipaddrlist,
            "method": "Extended hostname lookup"
        }
        print(f"  ✓ SUCCESS: {ipaddrlist}")
    except socket.gaierror as e:
        results["tests"]["gethostbyname_ex"] = {
            "status": "FAILED",
            "error": str(e),
            "error_code": e.errno,
            "method": "Extended hostname lookup"
        }
        print(f"  ✗ FAILED: {e}")
    
    # Check /etc/resolv.conf
    print("[System] Checking DNS configuration")
    try:
        with open('/etc/resolv.conf', 'r') as f:
            resolv_conf = f.read()
            results["system_dns_config"] = resolv_conf
            print(f"  /etc/resolv.conf:\n{resolv_conf}")
    except Exception as e:
        results["system_dns_config"] = f"Unable to read: {str(e)}"
        print(f"  Unable to read /etc/resolv.conf: {e}")
    
    return results

def lambda_handler(event, context):
    """Lambda handler for DNS testing"""
    
    # Get hostname from event or use default
    hostname = event.get('hostname', 'deedssoap.deeds.gov.za')
    
    print("="*70)
    print(f"DNS RESOLUTION TEST: {hostname}")
    print("="*70)
    
    results = test_dns_resolution(hostname)
    
    # Add Lambda environment info
    results["lambda_info"] = {
        "function_name": context.function_name if context else "local-test",
        "aws_region": os.environ.get('AWS_REGION', 'unknown'),
        "vpc_enabled": bool(os.environ.get('AWS_LAMBDA_VPC_SUBNET_IDS'))
    }
    
    # Overall status
    success_count = sum(1 for test in results["tests"].values() if test.get("status") == "SUCCESS")
    total_count = len(results["tests"])
    results["overall_status"] = "SUCCESS" if success_count > 0 else "FAILED"
    results["success_rate"] = f"{success_count}/{total_count}"
    
    print("="*70)
    print(f"OVERALL: {results['overall_status']} ({results['success_rate']})")
    print("="*70)
    
    return {
        'statusCode': 200 if success_count > 0 else 500,
        'body': json.dumps(results, indent=2)
    }

# For local testing
if __name__ == "__main__":
    class MockContext:
        function_name = "local-test"
    
    print("\nTesting: deedssoap.deeds.gov.za")
    print("-" * 70)
    result = lambda_handler({"hostname": "deedssoap.deeds.gov.za"}, MockContext())
    print("\nResult:", json.dumps(json.loads(result['body']), indent=2))
    
    print("\n\nTesting: www.google.com")
    print("-" * 70)
    result = lambda_handler({"hostname": "www.google.com"}, MockContext())
    print("\nResult:", json.dumps(json.loads(result['body']), indent=2))
