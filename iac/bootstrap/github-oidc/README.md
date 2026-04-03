# GitHub OIDC Bootstrap

This Terraform configuration creates the GitHub Actions OIDC provider and IAM deploy role. Run once per environment in the single AWS account (379992419891).

## Usage

```bash
# Dev
aws sso login
terraform init
terraform apply -var="environment_shortname=dev"

# Prod
terraform apply -var="environment_shortname=prod"
```

## Output

After applying, copy the `role_arn` output and set it as the `AWS_ROLE_ARN` secret in the corresponding GitHub Environment:
- `verigate-dev`
- `verigate-prod`
