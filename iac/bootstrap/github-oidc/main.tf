terraform {
  required_version = ">= 1.7.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "github_org" {
  description = "GitHub organization name"
  type        = string
  default     = "VeriGate-Org"
}

variable "github_repo" {
  description = "GitHub repository name"
  type        = string
  default     = "verigate"
}

variable "environment_shortname" {
  description = "Short environment name (dev, prod)"
  type        = string
}

# OIDC Provider (create once per account)
resource "aws_iam_openid_connect_provider" "github" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
}

# Deploy role for GitHub Actions
data "aws_iam_policy_document" "github_actions_assume_role" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values = [
        "repo:${var.github_org}/${var.github_repo}:environment:verigate-*",
        "repo:${var.github_org}/${var.github_repo}:ref:refs/heads/main",
        "repo:${var.github_org}/${var.github_repo}:ref:refs/tags/v*",
      ]
    }
  }
}

resource "aws_iam_role" "github_actions_deploy" {
  name               = "github-actions-deploy-${var.environment_shortname}"
  assume_role_policy = data.aws_iam_policy_document.github_actions_assume_role.json
}

resource "aws_iam_role_policy_attachment" "admin" {
  role       = aws_iam_role.github_actions_deploy.name
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
}

output "role_arn" {
  description = "ARN of the GitHub Actions deploy role. Set this as AWS_ROLE_ARN secret in the GitHub environment."
  value       = aws_iam_role.github_actions_deploy.arn
}
