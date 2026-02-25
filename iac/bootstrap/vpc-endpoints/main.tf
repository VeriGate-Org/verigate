data "aws_ssm_parameter" "vpc_id" {
  name = "/platform/vpcid"
}

data "aws_ssm_parameter" "private_subnet_1" {
  name = "/platform/privateSubnet-1"
}

data "aws_ssm_parameter" "private_subnet_2" {
  name = "/platform/privateSubnet-2"
}

data "aws_vpc" "this" {
  id = data.aws_ssm_parameter.vpc_id.value
}

data "aws_route_tables" "private" {
  vpc_id = data.aws_ssm_parameter.vpc_id.value
}

# Security group for interface VPC endpoints
resource "aws_security_group" "vpc_endpoints" {
  name_prefix = "verigate-vpc-endpoints-"
  description = "Allow HTTPS from VPC CIDR for VPC endpoints"
  vpc_id      = data.aws_ssm_parameter.vpc_id.value

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = [data.aws_vpc.this.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ECR API endpoint (interface)
resource "aws_vpc_endpoint" "ecr_api" {
  vpc_id              = data.aws_ssm_parameter.vpc_id.value
  service_name        = "com.amazonaws.${var.aws_region}.ecr.api"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [
    data.aws_ssm_parameter.private_subnet_1.value,
    data.aws_ssm_parameter.private_subnet_2.value,
  ]
  security_group_ids = [aws_security_group.vpc_endpoints.id]
}

# ECR Docker endpoint (interface)
resource "aws_vpc_endpoint" "ecr_dkr" {
  vpc_id              = data.aws_ssm_parameter.vpc_id.value
  service_name        = "com.amazonaws.${var.aws_region}.ecr.dkr"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [
    data.aws_ssm_parameter.private_subnet_1.value,
    data.aws_ssm_parameter.private_subnet_2.value,
  ]
  security_group_ids = [aws_security_group.vpc_endpoints.id]
}

# S3 gateway endpoint (free)
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = data.aws_ssm_parameter.vpc_id.value
  service_name      = "com.amazonaws.${var.aws_region}.s3"
  vpc_endpoint_type = "Gateway"

  route_table_ids = data.aws_route_tables.private.ids
}

# CloudWatch Logs endpoint (interface)
resource "aws_vpc_endpoint" "logs" {
  vpc_id              = data.aws_ssm_parameter.vpc_id.value
  service_name        = "com.amazonaws.${var.aws_region}.logs"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [
    data.aws_ssm_parameter.private_subnet_1.value,
    data.aws_ssm_parameter.private_subnet_2.value,
  ]
  security_group_ids = [aws_security_group.vpc_endpoints.id]
}
