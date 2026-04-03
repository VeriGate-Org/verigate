# GitHub Environments Setup

This document describes all GitHub Environments, Secrets, and Variables required for VeriGate CI/CD.

## GitHub Environments

Create the following environments in the GitHub repository settings:

| Environment | AWS Account | Account ID |
|-------------|-------------|------------|
| `verigate-dev` | VeriGate | 379992419891 |
| `verigate-prod` | VeriGate | 379992419891 |

## Environment Secrets (per environment)

Each environment must have the following secrets configured:

### AWS

| Secret | Description |
|--------|-------------|
| `AWS_ROLE_ARN` | ARN of the GitHub Actions OIDC deploy role (created by `iac/bootstrap/github-oidc/`) |

### QLink (Bank Account Verification)

| Secret | Description |
|--------|-------------|
| `QLINK_USERNAME` | QLink API username |
| `QLINK_PASSWORD` | QLink API password |
| `QLINK_CLIENT_ID` | QLink client ID |

### Worldcheck (Astute/Refinitiv - Personal Details Verification)

| Secret | Description |
|--------|-------------|
| `ASTUTE_CLIENT_ID` | Astute/Worldcheck client ID |
| `ASTUTE_CLIENT_SECRET` | Astute/Worldcheck client secret |
| `ASTUTE_SCOPE` | Astute/Worldcheck OAuth scope |
| `ASTUTE_SUBSCRIPTION_KEY` | Astute/Worldcheck subscription key |
| `ASTUTE_BASIC_AUTHORIZATION` | Astute/Worldcheck basic authorization header |

### World Check One (Sanctions Screening)

| Secret | Description |
|--------|-------------|
| `WORLDCHECK_API_KEY` | World Check One API key |
| `WORLDCHECK_API_SECRET` | World Check One API secret |
| `WORLDCHECK_USER_ID` | World Check One user ID |
| `WORLDCHECK_DEFAULT_GROUP_ID` | World Check One default group ID |

## Repository-Level Secrets

These secrets are set at the repository level (not per-environment):

| Secret | Description |
|--------|-------------|
| `VERIGATE_GH_PACKAGES_PAT` | GitHub Personal Access Token with `read:packages` scope for accessing verigate-shared-kernel |
| `OPENAI_API_KEY` | OpenAI API key for ChatGPT code review workflow |

## Environment Variables (per environment)

Each environment must have the following variables:

| Variable | DEV | PROD |
|----------|-----|------|
| `AWS_REGION` | `eu-west-1` | `eu-west-1` |
| `STACK_NAME` | `verigate-verification-cg-dev` | `verigate-verification-cg` |
| `ENVIRONMENT_SHORTNAME` | `dev` | `prod` |

## Secrets to Remove

The following secrets from the old repository are no longer needed and should NOT be configured:

| Secret | Reason |
|--------|--------|
| `SFT_INSOURCE_APPID` | Old private repo access - replaced by monorepo local modules |
| `SFT_INSOURCE_PK` | Old private repo access - replaced by monorepo local modules |
| `ORMS_USERNAME` | ORMS integration removed - replaced by opensanctions |
| `ORMS_PASSWORD` | ORMS integration removed |
| `ORMS_CLIENT_ID` | ORMS integration removed |

## Setup Order

1. Run `iac/bootstrap/github-oidc/` in the AWS account (379992419891) to create OIDC provider and deploy roles
2. Create GitHub environments with secrets and variables as documented above
3. Push to `main` branch to trigger dev deployment
4. Create a `v*` tag to trigger prod deployment
