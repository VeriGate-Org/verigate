#----------------------------------------------------------------------------------------------------------------
# Platform Data Resources
#----------------------------------------------------------------------------------------------------------------
data "aws_caller_identity" "current" {}

data "aws_ssm_parameter" "vpc_id" {
  name = "/platform/vpcid"
}

data "aws_ssm_parameter" "vpc_cidr" {
  name = "/platform/vpcCidr"
}

data "aws_ssm_parameter" "private_subnet_az1" {
  name = "/platform/privateSubnet-1"
}
data "aws_ssm_parameter" "private_subnet_az2" {
  name = "/platform/privateSubnet-2"
}
data "aws_ssm_parameter" "private_subnet_az3" {
  name = "/platform/privateSubnet-3"
}
data "aws_ssm_parameter" "public_subnet_az1" {
  name = "/platform/publicSubnet-1"
}
data "aws_ssm_parameter" "public_subnet_az2" {
  name = "/platform/publicSubnet-2"
}
data "aws_ssm_parameter" "public_subnet_az3" {
  name = "/platform/publicSubnet-3"
} 