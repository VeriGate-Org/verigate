output "eip_public_ip" {
  description = "Elastic IP public address — provide to DeedsWeb for whitelisting"
  value       = aws_eip.nat.public_ip
}

output "nat_gateway_id" {
  description = "NAT Gateway ID"
  value       = aws_nat_gateway.this.id
}

output "private_subnet_ids" {
  description = "Private subnet IDs (one per AZ)"
  value = [
    aws_subnet.private_1.id,
    aws_subnet.private_2.id,
    aws_subnet.private_3.id,
  ]
}
