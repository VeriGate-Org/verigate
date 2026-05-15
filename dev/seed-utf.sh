#!/bin/bash
set -euo pipefail

REGION="eu-west-1"
AWS="awslocal"
PARTNER_ID="partner-utf"

echo "=== Seeding Urban Task Force tenant ==="

# ─── API Key ──────────────────────────────────────────────────────────
# The raw key the portal will use. We compute hashes using the same
# algorithm as ApiKeyService.java:
#   lookupHash   = SHA256(rawKey)                        (hex)
#   salt         = 16 random bytes, Base64-URL no-pad
#   verifyHash   = SHA256(Base64UrlDecode(salt) + rawKey) (hex)

RAW_KEY="vg_live_utfdev000000000000000000000000000000000000000000000000000000"

echo "Computing API key hashes..."
HASHES=$(python3 -c "
import hashlib, base64, os
raw_key = '$RAW_KEY'
lookup_hash = hashlib.sha256(raw_key.encode('utf-8')).hexdigest()
salt_bytes = os.urandom(16)
salt_b64 = base64.urlsafe_b64encode(salt_bytes).rstrip(b'=').decode()
verify_hash = hashlib.sha256(salt_bytes + raw_key.encode('utf-8')).hexdigest()
print(f'{lookup_hash}|{salt_b64}|{verify_hash}')
")

LOOKUP_HASH=$(echo "$HASHES" | cut -d'|' -f1)
SALT=$(echo "$HASHES" | cut -d'|' -f2)
VERIFY_HASH=$(echo "$HASHES" | cut -d'|' -f3)

echo "  lookupHash: ${LOOKUP_HASH:0:16}..."
echo "  salt:       ${SALT:0:8}..."

$AWS dynamodb put-item --table-name verigate-api-keys --region "$REGION" --item "$(cat <<EOF
{
  "lookupHash":       {"S": "$LOOKUP_HASH"},
  "verificationHash": {"S": "$VERIFY_HASH"},
  "salt":             {"S": "$SALT"},
  "partnerId":        {"S": "$PARTNER_ID"},
  "status":           {"S": "ACTIVE"},
  "keyPrefix":        {"S": "vg_live_"},
  "createdAt":        {"S": "2025-06-01T10:00:00"},
  "createdBy":        {"S": "seed-script"}
}
EOF
)"
echo "  API key seeded."

# ─── Partner Metadata (partner-hub) ─────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":     {"S": "$PARTNER_ID"},
  "entityType":    {"S": "METADATA"},
  "name":          {"S": "Urban Task Force"},
  "contactEmail":  {"S": "hr@utf.co.za"},
  "billingPlan":   {"S": "FREE"},
  "partnerStatus": {"S": "ACTIVE"},
  "createdAt":     {"S": "2025-06-01T08:00:00"}
}
EOF
)"
echo "  Partner metadata seeded."

# ─── Partner Profile (partner-hub) ──────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":  {"S": "$PARTNER_ID"},
  "entityType": {"S": "PROFILE"},
  "data":       {"S": "{\"companyName\":\"Urban Task Force\",\"contactEmail\":\"hr@utf.co.za\",\"website\":\"https://urbantaskforce.co.za\",\"webhookUrl\":\"https://hooks.urbantaskforce.co.za/verigate\",\"industry\":\"Real Estate\",\"country\":\"ZA\"}"},
  "slug":       {"S": "urbantaskforce"},
  "updatedAt":  {"S": "2025-06-01T10:30:00Z"}
}
EOF
)"
echo "  Partner profile seeded."

# ─── Notifications (partner-hub) ────────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":  {"S": "$PARTNER_ID"},
  "entityType": {"S": "NOTIFICATIONS"},
  "data":       {"S": "{\"emailEnabled\":true,\"webhookEnabled\":false,\"emailRecipients\":[\"hr@utf.co.za\"],\"notifyOn\":[\"VERIFICATION_COMPLETED\",\"VERIFICATION_FAILED\"]}"},
  "updatedAt":  {"S": "2025-06-01T10:35:00Z"}
}
EOF
)"
echo "  Notifications seeded."

# ─── Policy (partner-hub) ───────────────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "POLICY#utf-pol-001"},
  "partnerPolicyId":   {"S": "utf-pol-001"},
  "name":              {"S": "Basic KYC"},
  "description":       {"S": "Basic identity verification for FREE tier"},
  "stepsJson":         {"S": "[{\"type\":\"ID\",\"provider\":\"default\",\"required\":true}]"},
  "scoringConfigJson": {"S": "{\"weights\":{\"ID\":100},\"passThreshold\":60}"},
  "status":            {"S": "PUBLISHED"},
  "version":           {"N": "1"},
  "createdAt":         {"S": "2025-06-01T11:00:00"},
  "updatedAt":         {"S": "2025-06-01T11:00:00"}
}
EOF
)"
echo "  1 policy seeded."

# ─── Risk Scoring Config (partner-hub) ──────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "RISK_SCORING"},
  "strategy":          {"S": "WEIGHTED_AVERAGE"},
  "weightsJson":       {"S": "{\"ID\":100}"},
  "tiersJson":         {"S": "[{\"tier\":\"LOW\",\"minScore\":75,\"maxScore\":100,\"decision\":\"APPROVE\"},{\"tier\":\"MEDIUM\",\"minScore\":50,\"maxScore\":74,\"decision\":\"MANUAL_REVIEW\"},{\"tier\":\"HIGH\",\"minScore\":0,\"maxScore\":49,\"decision\":\"REJECT\"}]"},
  "overrideRulesJson": {"S": "[]"},
  "updatedAt":         {"S": "2025-06-01T11:05:00"}
}
EOF
)"
echo "  Risk scoring config seeded."

echo ""
echo "=== Urban Task Force seed data complete ==="
echo "Partner ID:  $PARTNER_ID"
echo "API Key:     $RAW_KEY"
echo ""
echo "Use this key in the portal or curl:"
echo "  curl -H 'X-API-Key: $RAW_KEY' http://localhost:8080/api/partner/policies"
