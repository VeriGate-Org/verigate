# NAT Gateway Setup Playbook

This document captures the current sandbox (`sbx`) network egress configuration and provides a repeatable process for creating the same single-NAT design in other VeriGate environments (`dev`, `ppe`, `prod`).

## Sandbox Reference State (`sbx`)

| Item | Value |
| --- | --- |
| Region | `af-south-1` |
| VPC | `vpc-077e3c6d4f171bd8f` |
| NAT gateway | `nat-0d50fbc822aae3169` (tagged `Name=vg-sbx-nat`, `Application=VeriGate`, `Environment=sbx`) |
| Elastic IP | `16.28.8.25` (`eipalloc-0eb2bcd0a74dee9a8`, tagged `Name=vg-sbx-nat-eip`) |
| Public subnet hosting NAT | `subnet-04fcbfa138b0ad8cc` |
| Private route table | `rtb-04b9634e66608f24c` (`Name=vg-sbx-private-rtb`) |
| Private subnets routed via NAT | `subnet-05eae024ee14aa3ae` (`af-south-1b`), `subnet-0da53b482e4ca18e2` (`af-south-1c`) |
| Main/public route table | `rtb-0cbc9df1e2ea510bc` (default route → `igw-02bd019cbd5bd2f8a`) |

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
: "${ENVIRONMENT:=sbx}"
: "${PREFERRED_AZ:=}"
# ... (rest of script unchanged)
```

> **Heads-up:** For non-sandbox environments override the default `REGION`, `VPC_ID`, and `ENVIRONMENT` via environment variables so you do not edit the script directly.

## Rollout Procedure (per environment)

1. **Prerequisites**
   - AWS CLI v2 configured for the target account.
   - `jq`, `python3`, and permissions to manage EC2 networking resources.
   - The VPC already contains at least one public subnet with an Internet Gateway route.

2. **Set environment variables**

   ```bash
   export REGION=<region>
   export VPC_ID=<vpc-id>
   export ENVIRONMENT=<env-code>   # dev|ppe|prod
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
   - Record the NAT ID, Elastic IP, route table ID, and subnet associations for the environment. Maintaining a table similar to the sandbox reference helps operations and audits.

## Environment Matrix

| Environment | Example variables | Notes |
| --- | --- | --- |
| dev | `REGION=af-south-1`, `VPC_ID=<dev-vpc-id>`, `ENVIRONMENT=dev` | Reuse script and follow rollout steps. |
| ppe | `REGION=af-south-1`, `VPC_ID=<ppe-vpc-id>`, `ENVIRONMENT=ppe` | Ensure partner access rules are updated with the new Elastic IP. |
| prod | `REGION=af-south-1`, `VPC_ID=<prod-vpc-id>`, `ENVIRONMENT=prod` | Consider multi-AZ resilience (one NAT per AZ) if production traffic requires it. |

## Operational Notes

- Elastic IPs remain billable until released. Only deallocate them if you are done with the corresponding NAT.
- NAT gateway pricing includes hourly and data-processing charges; monitor CloudWatch metrics (`ActiveConnectionCount`, `BytesOutToDestination`).
- Keep IAM tags (`Application`, `Environment`, `Name`) aligned across Elastic IPs, NAT gateways, and route tables for automation.
- If a NAT already exists in the VPC, the script reuses it and simply wires up routing—review the output before provisioning new gateways.
- For production, evaluate whether a single NAT matches availability requirements; AWS recommends one NAT per AZ for high availability.
