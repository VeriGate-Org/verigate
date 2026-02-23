#!/bin/bash
#
# Deploy VPC Connectivity Test Lambda Function
# This script deploys a Lambda function to test outbound connectivity from your VPC
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Configuration
STACK_NAME="verigate-connectivity-test"
TEMPLATE_FILE="$SCRIPT_DIR/connectivity-test-template.yaml"
REGION="${AWS_REGION:-af-south-1}"

# VPC Configuration (default VPC)
VPC_ID="${VPC_ID:-vpc-077e3c6d4f171bd8f}"
SUBNET_IDS="${SUBNET_IDS:-subnet-05eae024ee14aa3ae,subnet-0da53b482e4ca18e2,subnet-04fcbfa138b0ad8cc}"
SECURITY_GROUP_ID="${SECURITY_GROUP_ID:-sg-074a6fc8e06f6fe4e}"  # default security group

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================"
echo "VPC Connectivity Test Lambda Deployment"
echo "========================================"
echo ""
echo "Configuration:"
echo "  Stack Name: $STACK_NAME"
echo "  Region: $REGION"
echo "  VPC ID: $VPC_ID"
echo "  Subnets: $SUBNET_IDS"
echo "  Security Group: $SECURITY_GROUP_ID"
echo ""

# Check if AWS CLI is configured
if ! aws sts get-caller-identity >/dev/null 2>&1; then
    echo -e "${RED}Error: AWS CLI is not configured or credentials are invalid${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} AWS credentials verified"

# Check if template exists
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo -e "${RED}Error: Template file not found: $TEMPLATE_FILE${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Template file found"

# Deploy with SAM
echo ""
echo "Deploying Lambda function..."
echo ""

sam deploy \
    --template-file "$TEMPLATE_FILE" \
    --stack-name "$STACK_NAME" \
    --region "$REGION" \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides \
        Environment=dev \
        VpcId="$VPC_ID" \
        SubnetIds="$SUBNET_IDS" \
        SecurityGroupIds="$SECURITY_GROUP_ID" \
    --no-confirm-changeset \
    --no-fail-on-empty-changeset

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Deployment successful!${NC}"
    
    # Get function name
    FUNCTION_NAME=$(aws cloudformation describe-stacks \
        --stack-name "$STACK_NAME" \
        --region "$REGION" \
        --query 'Stacks[0].Outputs[?OutputKey==`FunctionName`].OutputValue' \
        --output text)
    
    echo ""
    echo "Function deployed: $FUNCTION_NAME"
    echo ""
    echo "To test connectivity, run:"
    echo ""
    echo "  # Test default URL (Deeds Office)"
    echo "  aws lambda invoke --function-name $FUNCTION_NAME --region $REGION response.json && cat response.json | jq ."
    echo ""
    echo "  # Test custom URL"
    echo "  aws lambda invoke --function-name $FUNCTION_NAME --region $REGION --payload '{\"url\":\"https://www.drdlr.gov.za\"}' response.json && cat response.json | jq ."
    echo ""
else
    echo -e "${RED}✗ Deployment failed${NC}"
    exit 1
fi
