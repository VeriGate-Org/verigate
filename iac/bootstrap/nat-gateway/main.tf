# -----------------------------------------------------------------------------
# Data Sources — read existing VPC and public subnet from SSM
# -----------------------------------------------------------------------------

data "aws_ssm_parameter" "vpc_id" {
  name = "/platform/vpcid"
}

data "aws_ssm_parameter" "public_subnet_1" {
  name = "/platform/publicSubnet-1"
}

# -----------------------------------------------------------------------------
# Elastic IP — static outbound IP for DeedsWeb whitelisting
# -----------------------------------------------------------------------------

resource "aws_eip" "nat" {
  domain = "vpc"

  tags = {
    Name = "verigate-nat-gateway-eip"
  }
}

# -----------------------------------------------------------------------------
# NAT Gateway — in public subnet af-south-1a, attached to EIP
# -----------------------------------------------------------------------------

resource "aws_nat_gateway" "this" {
  allocation_id = aws_eip.nat.id
  subnet_id     = data.aws_ssm_parameter.public_subnet_1.value

  tags = {
    Name = "verigate-nat-gateway"
  }

  depends_on = [aws_eip.nat]
}

# -----------------------------------------------------------------------------
# Private Subnets — one per AZ for Lambda ENIs
# -----------------------------------------------------------------------------

resource "aws_subnet" "private_1" {
  vpc_id            = data.aws_ssm_parameter.vpc_id.value
  cidr_block        = "172.31.48.0/24"
  availability_zone = "${var.aws_region}a"

  tags = {
    Name = "verigate-private-1"
  }
}

resource "aws_subnet" "private_2" {
  vpc_id            = data.aws_ssm_parameter.vpc_id.value
  cidr_block        = "172.31.49.0/24"
  availability_zone = "${var.aws_region}b"

  tags = {
    Name = "verigate-private-2"
  }
}

resource "aws_subnet" "private_3" {
  vpc_id            = data.aws_ssm_parameter.vpc_id.value
  cidr_block        = "172.31.50.0/24"
  availability_zone = "${var.aws_region}c"

  tags = {
    Name = "verigate-private-3"
  }
}

# -----------------------------------------------------------------------------
# Route Table — single table for all private subnets, default route via NAT GW
# -----------------------------------------------------------------------------

resource "aws_route_table" "private" {
  vpc_id = data.aws_ssm_parameter.vpc_id.value

  tags = {
    Name = "verigate-private-rt"
  }
}

resource "aws_route" "nat" {
  route_table_id         = aws_route_table.private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.this.id
}

resource "aws_route_table_association" "private_1" {
  subnet_id      = aws_subnet.private_1.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_2" {
  subnet_id      = aws_subnet.private_2.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_3" {
  subnet_id      = aws_subnet.private_3.id
  route_table_id = aws_route_table.private.id
}

# -----------------------------------------------------------------------------
# SSM Parameters — update private subnet refs + publish EIP for whitelisting
# -----------------------------------------------------------------------------

resource "aws_ssm_parameter" "private_subnet_1" {
  name      = "/platform/privateSubnet-1"
  type      = "String"
  value     = aws_subnet.private_1.id
  overwrite = true

  tags = {
    Name = "verigate-private-subnet-1"
  }
}

resource "aws_ssm_parameter" "private_subnet_2" {
  name      = "/platform/privateSubnet-2"
  type      = "String"
  value     = aws_subnet.private_2.id
  overwrite = true

  tags = {
    Name = "verigate-private-subnet-2"
  }
}

resource "aws_ssm_parameter" "private_subnet_3" {
  name      = "/platform/privateSubnet-3"
  type      = "String"
  value     = aws_subnet.private_3.id
  overwrite = true

  tags = {
    Name = "verigate-private-subnet-3"
  }
}

resource "aws_ssm_parameter" "nat_eip" {
  name      = "/platform/nat-gateway/eip"
  type      = "String"
  value     = aws_eip.nat.public_ip
  overwrite = true

  tags = {
    Name = "verigate-nat-gateway-eip"
  }
}
