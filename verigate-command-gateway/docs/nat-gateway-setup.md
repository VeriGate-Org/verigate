# NAT Gateway Setup Playbook

This document provides a repeatable process for creating a single-NAT design in VeriGate environments (`dev`, `prod`).

## Automation Script

We use `single-nat-setup.sh` to provision or reuse a single NAT gateway per VPC. The script:

1. Discovers a public subnet (or uses `PREFERRED_AZ` if provided).
2. Allocates or reuses a tagged Elastic IP (`vg-<env>-nat-eip`).
3. Creates or reuses a NAT gateway in that subnet (`vg-<env>-nat`).
4. Connects private route tables to the NAT.

The script currently lives in the `aws-setup-docs` repository at `single-nat-setup.sh`. Copy it alongside your infrastructure tooling or reference it directly.

```bash
#!/usr/bin/env bash
set -euo pipefail
: "${REGION:=af-south-1}"
: "${VPC_ID:=vpc-077e3c6d4f171bd8f}"
: "${ENVIRONMENT:=dev}"
: "${PREFERRED_AZ:=}"
# ... (rest of script unchanged)
```

> **Heads-up:** Override the default `REGION`, `VPC_ID`, and `ENVIRONMENT` via environment variables so you do not edit the script directly.

## Rollout Procedure (per environment)

1. **Prerequisites**
   - AWS CLI v2 configured for the target account.
   - `jq`, `python3`, and permissions to manage EC2 networking resources.
   - The VPC already contains at least one public subnet with an Internet Gateway route.

2. **Set environment variables**

   ```bash
   export REGION=<region>
   export VPC_ID=<vpc-id>
   export ENVIRONMENT=<env-code>   # dev|prod
   export PREFERRED_AZ=<az-optional>
   ```

3. **Run the script**

   ```bash
   ./single-nat-setup.sh
   ```

   The script outputs the NAT ID, Elastic IP, and the public subnet used. Capture the Elastic IP—this is the egress IP to share with partners.

4. **Create a private route table (one per VPC)**

   ```bash
   aws ec2 create-route-table \
     --vpc-id $VPC_ID \
     --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=vg-$ENVIRONMENT-private-rtb},{Key=Application,Value=VeriGate},{Key=Environment,Value=$ENVIRONMENT}]"

   aws ec2 create-route \
     --route-table-id <new-rtb-id> \
     --destination-cidr-block 0.0.0.0/0 \
     --nat-gateway-id <nat-id-from-script>
   ```

5. **Associate private subnets**

   ```bash
   aws ec2 associate-route-table --route-table-id <new-rtb-id> --subnet-id <private-subnet-1>
   aws ec2 associate-route-table --route-table-id <new-rtb-id> --subnet-id <private-subnet-2>
   # repeat for all private subnets
   ```

6. **Verify routing**
   - `aws ec2 describe-route-tables --route-table-id <new-rtb-id>` should show `0.0.0.0/0` pointing to the NAT.
   - `aws ec2 describe-route-tables --route-table-id <main-public-rtb>` should still point to the Internet Gateway for public subnets.

7. **Optional egress test**
   - Deploy a short-lived Lambda (or EC2) into one of the private subnets and call `https://checkip.amazonaws.com`. The response must match the Elastic IP returned by the script. (See historical commands in the repo history for a ready-to-use Lambda example.)

8. **Document the results**
   - Record the NAT ID, Elastic IP, route table ID, and subnet associations for the environment. Maintaining a table helps operations and audits.

## Environment Matrix

| Environment | Example variables | Notes |
| --- | --- | --- |
| dev | `REGION=eu-west-1`, `VPC_ID=<dev-vpc-id>`, `ENVIRONMENT=dev` | Reuse script and follow rollout steps. |
| prod | `REGION=eu-west-1`, `VPC_ID=<prod-vpc-id>`, `ENVIRONMENT=prod` | Consider multi-AZ resilience (one NAT per AZ) if production traffic requires it. |

## Operational Notes

- Elastic IPs remain billable until released. Only deallocate them if you are done with the corresponding NAT.
- NAT gateway pricing includes hourly and data-processing charges; monitor CloudWatch metrics (`ActiveConnectionCount`, `BytesOutToDestination`).
- Keep IAM tags (`Application`, `Environment`, `Name`) aligned across Elastic IPs, NAT gateways, and route tables for automation.
- If a NAT already exists in the VPC, the script reuses it and simply wires up routing—review the output before provisioning new gateways.
- For production, evaluate whether a single NAT matches availability requirements; AWS recommends one NAT per AZ for high availability.
