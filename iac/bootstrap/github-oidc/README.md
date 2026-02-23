# GitHub OIDC Bootstrap

This Terraform configuration creates the GitHub Actions OIDC provider and IAM deploy role in each AWS account. Run once per AWS account.

## Usage

```bash
# Sandbox (420747712978)
aws sso login --profile verigate-sbx
terraform init
terraform apply -var="environment_shortname=sbx"

# Development (601301273631)
aws sso login --profile verigate-dev
terraform init
terraform apply -var="environment_shortname=dev"

# Preproduction (021914681791)
aws sso login --profile verigate-ppe
terraform init
terraform apply -var="environment_shortname=ppe"

# Production (726412862551)
aws sso login --profile verigate-prd
terraform init
terraform apply -var="environment_shortname=prd"
```

## Output

After applying, copy the `role_arn` output and set it as the `AWS_ROLE_ARN` secret in the corresponding GitHub Environment:
- `verigate-sandbox`
- `verigate-development`
- `verigate-preproduction`
- `verigate-production`
