#!/bin/bash
set -euo pipefail

REGION="eu-west-1"
AWS="awslocal"
PARTNER_ID="partner-acme"

echo "=== Seeding test data ==="

# ─── API Key ──────────────────────────────────────────────────────────
# The raw key the portal will use. We compute hashes using the same
# algorithm as ApiKeyService.java:
#   lookupHash   = SHA256(rawKey)                        (hex)
#   salt         = 16 random bytes, Base64-URL no-pad
#   verifyHash   = SHA256(Base64UrlDecode(salt) + rawKey) (hex)

RAW_KEY="vg_live_localdev0000000000000000000000000000000000000000000000000000"

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
  "createdAt":        {"S": "2025-01-15T10:00:00"},
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
  "name":          {"S": "Acme Financial Services"},
  "contactEmail":  {"S": "admin@acme-financial.co.za"},
  "billingPlan":   {"S": "PROFESSIONAL"},
  "partnerStatus": {"S": "ACTIVE"},
  "createdAt":     {"S": "2025-01-10T08:00:00"}
}
EOF
)"
echo "  Partner metadata seeded."

# ─── Partner Profile (partner-hub) ──────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":  {"S": "$PARTNER_ID"},
  "entityType": {"S": "PROFILE"},
  "data":       {"S": "{\"companyName\":\"Acme Financial Services\",\"contactEmail\":\"admin@acme-financial.co.za\",\"website\":\"https://acme-financial.co.za\",\"webhookUrl\":\"https://hooks.acme-financial.co.za/verigate\",\"industry\":\"Financial Services\",\"country\":\"ZA\"}"},
  "slug":       {"S": "acme-financial"},
  "updatedAt":  {"S": "2025-01-15T10:30:00Z"}
}
EOF
)"
echo "  Partner profile seeded."

# ─── Notifications (partner-hub) ────────────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":  {"S": "$PARTNER_ID"},
  "entityType": {"S": "NOTIFICATIONS"},
  "data":       {"S": "{\"emailEnabled\":true,\"webhookEnabled\":true,\"emailRecipients\":[\"admin@acme-financial.co.za\",\"compliance@acme-financial.co.za\"],\"notifyOn\":[\"VERIFICATION_COMPLETED\",\"VERIFICATION_FAILED\",\"CASE_CREATED\",\"ALERT_HIGH\"]}"},
  "updatedAt":  {"S": "2025-01-15T10:35:00Z"}
}
EOF
)"
echo "  Notifications seeded."

# ─── Policies (partner-hub) ─────────────────────────────────────────

# Policy 1: PUBLISHED — ID + AVS + SANCTIONS
$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "POLICY#pol-001"},
  "partnerPolicyId":   {"S": "pol-001"},
  "name":              {"S": "Standard KYC"},
  "description":       {"S": "Standard identity verification with address and sanctions screening"},
  "stepsJson":         {"S": "[{\"type\":\"ID\",\"provider\":\"default\",\"required\":true},{\"type\":\"AVS\",\"provider\":\"default\",\"required\":true},{\"type\":\"SANCTIONS\",\"provider\":\"default\",\"required\":true}]"},
  "scoringConfigJson": {"S": "{\"weights\":{\"ID\":40,\"AVS\":30,\"SANCTIONS\":30},\"passThreshold\":70}"},
  "status":            {"S": "PUBLISHED"},
  "version":           {"N": "1"},
  "createdAt":         {"S": "2025-02-01T09:00:00"},
  "updatedAt":         {"S": "2025-02-01T09:00:00"}
}
EOF
)"

# Policy 2: DRAFT — ID + CREDIT
$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "POLICY#pol-002"},
  "partnerPolicyId":   {"S": "pol-002"},
  "name":              {"S": "Credit Check Flow"},
  "description":       {"S": "Identity verification with credit bureau check"},
  "stepsJson":         {"S": "[{\"type\":\"ID\",\"provider\":\"default\",\"required\":true},{\"type\":\"CREDIT\",\"provider\":\"default\",\"required\":true}]"},
  "scoringConfigJson": {"S": "{\"weights\":{\"ID\":50,\"CREDIT\":50},\"passThreshold\":65}"},
  "status":            {"S": "DRAFT"},
  "version":           {"N": "1"},
  "createdAt":         {"S": "2025-03-01T14:00:00"},
  "updatedAt":         {"S": "2025-03-01T14:00:00"}
}
EOF
)"

# Policy 3: ARCHIVED
$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "POLICY#pol-003"},
  "partnerPolicyId":   {"S": "pol-003"},
  "name":              {"S": "Legacy Basic Check"},
  "description":       {"S": "Deprecated: basic ID-only verification"},
  "stepsJson":         {"S": "[{\"type\":\"ID\",\"provider\":\"default\",\"required\":true}]"},
  "scoringConfigJson": {"S": "{\"weights\":{\"ID\":100},\"passThreshold\":60}"},
  "status":            {"S": "ARCHIVED"},
  "version":           {"N": "2"},
  "createdAt":         {"S": "2024-06-15T08:00:00"},
  "updatedAt":         {"S": "2025-01-20T16:00:00"}
}
EOF
)"
echo "  3 policies seeded."

# ─── Verifications ────────────────────────────────────────────────────

# Verification 1: COMPLETED
$AWS dynamodb put-item --table-name verigate-command-store --region "$REGION" --item "$(cat <<EOF
{
  "commandId":       {"S": "cmd-v001"},
  "commandName":     {"S": "VerifyParty"},
  "status":          {"S": "COMPLETED"},
  "partnerId":       {"S": "$PARTNER_ID"},
  "createdAt":       {"S": "2025-03-10T08:15:00"},
  "statusCreatedAt": {"S": "COMPLETED#2025-03-10T08:15:30"},
  "auxiliaryData":   {"M": {"policyId": {"S": "pol-001"}, "subjectName": {"S": "Thabo Molefe"}, "idNumber": {"S": "9001015800083"}}}
}
EOF
)"

# Verification 2: COMPLETED
$AWS dynamodb put-item --table-name verigate-command-store --region "$REGION" --item "$(cat <<EOF
{
  "commandId":       {"S": "cmd-v002"},
  "commandName":     {"S": "VerifyParty"},
  "status":          {"S": "COMPLETED"},
  "partnerId":       {"S": "$PARTNER_ID"},
  "createdAt":       {"S": "2025-03-09T14:20:00"},
  "statusCreatedAt": {"S": "COMPLETED#2025-03-09T14:20:45"},
  "auxiliaryData":   {"M": {"policyId": {"S": "pol-001"}, "subjectName": {"S": "Naledi Dlamini"}, "idNumber": {"S": "8512240156089"}}}
}
EOF
)"

# Verification 3: PENDING (in progress)
$AWS dynamodb put-item --table-name verigate-command-store --region "$REGION" --item "$(cat <<EOF
{
  "commandId":       {"S": "cmd-v003"},
  "commandName":     {"S": "VerifyParty"},
  "status":          {"S": "PENDING"},
  "partnerId":       {"S": "$PARTNER_ID"},
  "createdAt":       {"S": "2025-03-11T09:00:00"},
  "statusCreatedAt": {"S": "PENDING#2025-03-11T09:00:00"},
  "auxiliaryData":   {"M": {"policyId": {"S": "pol-001"}, "subjectName": {"S": "Sipho Ndlovu"}, "idNumber": {"S": "7806115012081"}}}
}
EOF
)"

# Verification 4: PERMANENT_FAILURE (failed)
$AWS dynamodb put-item --table-name verigate-command-store --region "$REGION" --item "$(cat <<EOF
{
  "commandId":       {"S": "cmd-v004"},
  "commandName":     {"S": "VerifyParty"},
  "status":          {"S": "PERMANENT_FAILURE"},
  "partnerId":       {"S": "$PARTNER_ID"},
  "createdAt":       {"S": "2025-03-08T11:45:00"},
  "statusCreatedAt": {"S": "PERMANENT_FAILURE#2025-03-08T11:46:00"},
  "errorDetails":    {"L": [{"S": "ID verification provider timeout"}, {"S": "Max retries exceeded"}]},
  "auxiliaryData":   {"M": {"policyId": {"S": "pol-001"}, "subjectName": {"S": "Lerato Mokoena"}, "idNumber": {"S": "9205280234087"}}}
}
EOF
)"

# Verification 5: COMPLETED
$AWS dynamodb put-item --table-name verigate-command-store --region "$REGION" --item "$(cat <<EOF
{
  "commandId":       {"S": "cmd-v005"},
  "commandName":     {"S": "VerifyParty"},
  "status":          {"S": "COMPLETED"},
  "partnerId":       {"S": "$PARTNER_ID"},
  "createdAt":       {"S": "2025-03-07T16:30:00"},
  "statusCreatedAt": {"S": "COMPLETED#2025-03-07T16:30:20"},
  "auxiliaryData":   {"M": {"policyId": {"S": "pol-002"}, "subjectName": {"S": "Andile Nkosi"}, "idNumber": {"S": "8801035678082"}}}
}
EOF
)"
echo "  5 verifications seeded."

# ─── Risk Assessments ─────────────────────────────────────────────────

# Assessment 1: APPROVE (score 85)
$AWS dynamodb put-item --table-name verigate-risk-assessments --region "$REGION" --item "$(cat <<EOF
{
  "verificationId":       {"S": "cmd-v001"},
  "assessmentId":         {"S": "ra-001"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "compositeScore":       {"N": "85"},
  "riskTier":             {"S": "LOW"},
  "decision":             {"S": "APPROVE"},
  "decisionReason":       {"S": "All checks passed with high confidence"},
  "individualScoresJson": {"S": "[{\"type\":\"ID\",\"score\":90,\"status\":\"PASS\"},{\"type\":\"AVS\",\"score\":82,\"status\":\"PASS\"},{\"type\":\"SANCTIONS\",\"score\":100,\"status\":\"CLEAR\"}]"},
  "overrideApplied":      {"BOOL": false},
  "assessedAt":           {"S": "2025-03-10T08:15:30"}
}
EOF
)"

# Assessment 2: REJECT (score 42)
$AWS dynamodb put-item --table-name verigate-risk-assessments --region "$REGION" --item "$(cat <<EOF
{
  "verificationId":       {"S": "cmd-v002"},
  "assessmentId":         {"S": "ra-002"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "compositeScore":       {"N": "42"},
  "riskTier":             {"S": "HIGH"},
  "decision":             {"S": "REJECT"},
  "decisionReason":       {"S": "AVS mismatch and low ID confidence score"},
  "individualScoresJson": {"S": "[{\"type\":\"ID\",\"score\":55,\"status\":\"PARTIAL\"},{\"type\":\"AVS\",\"score\":20,\"status\":\"FAIL\"},{\"type\":\"SANCTIONS\",\"score\":100,\"status\":\"CLEAR\"}]"},
  "overrideApplied":      {"BOOL": false},
  "assessedAt":           {"S": "2025-03-09T14:20:45"}
}
EOF
)"

# Assessment 3: MANUAL_REVIEW (score 63)
$AWS dynamodb put-item --table-name verigate-risk-assessments --region "$REGION" --item "$(cat <<EOF
{
  "verificationId":       {"S": "cmd-v005"},
  "assessmentId":         {"S": "ra-003"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "compositeScore":       {"N": "63"},
  "riskTier":             {"S": "MEDIUM"},
  "decision":             {"S": "MANUAL_REVIEW"},
  "decisionReason":       {"S": "Credit score below threshold, manual review required"},
  "individualScoresJson": {"S": "[{\"type\":\"ID\",\"score\":88,\"status\":\"PASS\"},{\"type\":\"CREDIT\",\"score\":45,\"status\":\"BELOW_THRESHOLD\"}]"},
  "overrideApplied":      {"BOOL": false},
  "assessedAt":           {"S": "2025-03-07T16:30:20"}
}
EOF
)"
echo "  3 risk assessments seeded."

# ─── Risk Scoring Config (partner-hub) ──────────────────────────────

$AWS dynamodb put-item --table-name verigate-partner-hub --region "$REGION" --item "$(cat <<EOF
{
  "partnerId":         {"S": "$PARTNER_ID"},
  "entityType":        {"S": "RISK_SCORING"},
  "strategy":          {"S": "WEIGHTED_AVERAGE"},
  "weightsJson":       {"S": "{\"ID\":35,\"AVS\":25,\"SANCTIONS\":20,\"CREDIT\":20}"},
  "tiersJson":         {"S": "[{\"tier\":\"LOW\",\"minScore\":75,\"maxScore\":100,\"decision\":\"APPROVE\"},{\"tier\":\"MEDIUM\",\"minScore\":50,\"maxScore\":74,\"decision\":\"MANUAL_REVIEW\"},{\"tier\":\"HIGH\",\"minScore\":0,\"maxScore\":49,\"decision\":\"REJECT\"}]"},
  "overrideRulesJson": {"S": "[{\"ruleId\":\"sanctions-hit\",\"condition\":\"SANCTIONS.status == MATCH\",\"action\":\"REJECT\",\"priority\":1}]"},
  "updatedAt":         {"S": "2025-02-20T12:00:00"}
}
EOF
)"
echo "  Risk scoring config seeded."

# ─── Cases ────────────────────────────────────────────────────────────

# Case 1: OPEN
$AWS dynamodb put-item --table-name verigate-cases --region "$REGION" --item "$(cat <<EOF
{
  "caseId":              {"S": "case-001"},
  "verificationId":      {"S": "cmd-v002"},
  "workflowId":          {"S": "pol-001"},
  "partnerId":           {"S": "$PARTNER_ID"},
  "status":              {"S": "OPEN"},
  "priority":            {"S": "HIGH"},
  "compositeRiskScore":  {"N": "42"},
  "riskTier":            {"S": "HIGH"},
  "subjectName":         {"S": "Naledi Dlamini"},
  "subjectId":           {"S": "8512240156089"},
  "commentsJson":        {"S": "[]"},
  "timelineJson":        {"S": "[{\"event\":\"CASE_CREATED\",\"timestamp\":\"2025-03-09T14:21:00\",\"actor\":\"system\",\"details\":\"Auto-created from rejected verification\"}]"},
  "statusCreatedAt":     {"S": "OPEN#2025-03-09T14:21:00"},
  "createdAt":           {"S": "2025-03-09T14:21:00"},
  "updatedAt":           {"S": "2025-03-09T14:21:00"}
}
EOF
)"

# Case 2: IN_REVIEW (assigned)
$AWS dynamodb put-item --table-name verigate-cases --region "$REGION" --item "$(cat <<EOF
{
  "caseId":              {"S": "case-002"},
  "verificationId":      {"S": "cmd-v005"},
  "workflowId":          {"S": "pol-002"},
  "partnerId":           {"S": "$PARTNER_ID"},
  "status":              {"S": "IN_REVIEW"},
  "assignee":            {"S": "analyst@acme-financial.co.za"},
  "priority":            {"S": "MEDIUM"},
  "compositeRiskScore":  {"N": "63"},
  "riskTier":            {"S": "MEDIUM"},
  "subjectName":         {"S": "Andile Nkosi"},
  "subjectId":           {"S": "8801035678082"},
  "commentsJson":        {"S": "[{\"author\":\"analyst@acme-financial.co.za\",\"text\":\"Reviewing credit history details\",\"timestamp\":\"2025-03-08T10:00:00\"}]"},
  "timelineJson":        {"S": "[{\"event\":\"CASE_CREATED\",\"timestamp\":\"2025-03-07T16:31:00\",\"actor\":\"system\",\"details\":\"Auto-created from manual review decision\"},{\"event\":\"CASE_ASSIGNED\",\"timestamp\":\"2025-03-08T09:00:00\",\"actor\":\"admin@acme-financial.co.za\",\"details\":\"Assigned to analyst\"}]"},
  "statusCreatedAt":     {"S": "IN_REVIEW#2025-03-08T09:00:00"},
  "createdAt":           {"S": "2025-03-07T16:31:00"},
  "updatedAt":           {"S": "2025-03-08T10:00:00"}
}
EOF
)"

# Case 3: RESOLVED
$AWS dynamodb put-item --table-name verigate-cases --region "$REGION" --item "$(cat <<EOF
{
  "caseId":              {"S": "case-003"},
  "verificationId":      {"S": "cmd-v001"},
  "workflowId":          {"S": "pol-001"},
  "partnerId":           {"S": "$PARTNER_ID"},
  "status":              {"S": "RESOLVED"},
  "assignee":            {"S": "analyst@acme-financial.co.za"},
  "priority":            {"S": "LOW"},
  "decision":            {"S": "APPROVE"},
  "decisionReason":      {"S": "Manual verification confirmed identity after initial flag"},
  "compositeRiskScore":  {"N": "85"},
  "riskTier":            {"S": "LOW"},
  "subjectName":         {"S": "Thabo Molefe"},
  "subjectId":           {"S": "9001015800083"},
  "commentsJson":        {"S": "[{\"author\":\"analyst@acme-financial.co.za\",\"text\":\"Identity confirmed via additional documentation\",\"timestamp\":\"2025-03-10T10:30:00\"}]"},
  "timelineJson":        {"S": "[{\"event\":\"CASE_CREATED\",\"timestamp\":\"2025-03-10T08:16:00\",\"actor\":\"system\",\"details\":\"Created for review\"},{\"event\":\"CASE_RESOLVED\",\"timestamp\":\"2025-03-10T10:30:00\",\"actor\":\"analyst@acme-financial.co.za\",\"details\":\"Approved after manual review\"}]"},
  "statusCreatedAt":     {"S": "RESOLVED#2025-03-10T10:30:00"},
  "createdAt":           {"S": "2025-03-10T08:16:00"},
  "updatedAt":           {"S": "2025-03-10T10:30:00"},
  "resolvedAt":          {"S": "2025-03-10T10:30:00"}
}
EOF
)"
echo "  3 cases seeded."

# ─── Monitored Subjects ──────────────────────────────────────────────

# Subject 1: ACTIVE (weekly)
$AWS dynamodb put-item --table-name verigate-monitored-subjects --region "$REGION" --item "$(cat <<EOF
{
  "subjectId":            {"S": "subj-001"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "policyId":             {"S": "pol-001"},
  "subjectType":          {"S": "INDIVIDUAL"},
  "subjectName":          {"S": "Thabo Molefe"},
  "subjectIdentifier":    {"S": "9001015800083"},
  "metadataJson":         {"S": "{\"country\":\"ZA\",\"checksEnabled\":[\"SANCTIONS\",\"PEP\"]}"},
  "monitoringFrequency":  {"S": "WEEKLY"},
  "status":               {"S": "ACTIVE"},
  "lastCheckedAt":        {"S": "2025-03-08T06:00:00"},
  "nextCheckAt":          {"S": "2025-03-15T06:00:00"},
  "lastRiskScore":        {"N": "85"},
  "lastRiskDecision":     {"S": "APPROVE"},
  "statusNextCheck":      {"S": "ACTIVE#2025-03-15T06:00:00"},
  "createdAt":            {"S": "2025-02-01T10:00:00"},
  "updatedAt":            {"S": "2025-03-08T06:00:00"}
}
EOF
)"

# Subject 2: PAUSED
$AWS dynamodb put-item --table-name verigate-monitored-subjects --region "$REGION" --item "$(cat <<EOF
{
  "subjectId":            {"S": "subj-002"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "policyId":             {"S": "pol-001"},
  "subjectType":          {"S": "INDIVIDUAL"},
  "subjectName":          {"S": "Naledi Dlamini"},
  "subjectIdentifier":    {"S": "8512240156089"},
  "metadataJson":         {"S": "{\"country\":\"ZA\",\"checksEnabled\":[\"SANCTIONS\"]}"},
  "monitoringFrequency":  {"S": "MONTHLY"},
  "status":               {"S": "PAUSED"},
  "lastCheckedAt":        {"S": "2025-02-15T06:00:00"},
  "nextCheckAt":          {"S": "2025-03-15T06:00:00"},
  "lastRiskScore":        {"N": "42"},
  "lastRiskDecision":     {"S": "REJECT"},
  "statusNextCheck":      {"S": "PAUSED#2025-03-15T06:00:00"},
  "createdAt":            {"S": "2025-01-20T10:00:00"},
  "updatedAt":            {"S": "2025-02-20T12:00:00"}
}
EOF
)"
echo "  2 monitored subjects seeded."

# ─── Monitoring Alerts ────────────────────────────────────────────────

# Alert 1: HIGH severity — score drop
$AWS dynamodb put-item --table-name verigate-monitoring-alerts --region "$REGION" --item "$(cat <<EOF
{
  "alertId":              {"S": "alert-001"},
  "subjectId":            {"S": "subj-002"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "severity":             {"S": "HIGH"},
  "alertType":            {"S": "RISK_SCORE_CHANGE"},
  "title":                {"S": "Significant risk score drop detected"},
  "description":          {"S": "Risk score for Naledi Dlamini dropped from 72 to 42 after new adverse media match"},
  "previousRiskScore":    {"N": "72"},
  "currentRiskScore":     {"N": "42"},
  "previousDecision":     {"S": "MANUAL_REVIEW"},
  "currentDecision":      {"S": "REJECT"},
  "acknowledged":         {"BOOL": false},
  "subjectIdCreatedAt":   {"S": "subj-002#2025-03-09T14:00:00"},
  "createdAt":            {"S": "2025-03-09T14:00:00"}
}
EOF
)"

# Alert 2: MEDIUM severity — new sanctions match
$AWS dynamodb put-item --table-name verigate-monitoring-alerts --region "$REGION" --item "$(cat <<EOF
{
  "alertId":              {"S": "alert-002"},
  "subjectId":            {"S": "subj-001"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "severity":             {"S": "MEDIUM"},
  "alertType":            {"S": "SANCTIONS_MATCH"},
  "title":                {"S": "Potential sanctions list match found"},
  "description":          {"S": "Partial name match found on updated OFAC SDN list for Thabo Molefe — likely false positive based on DOB mismatch"},
  "previousRiskScore":    {"N": "85"},
  "currentRiskScore":     {"N": "70"},
  "previousDecision":     {"S": "APPROVE"},
  "currentDecision":      {"S": "MANUAL_REVIEW"},
  "acknowledged":         {"BOOL": true},
  "acknowledgedBy":       {"S": "analyst@acme-financial.co.za"},
  "acknowledgedAt":       {"S": "2025-03-08T10:15:00"},
  "subjectIdCreatedAt":   {"S": "subj-001#2025-03-08T06:05:00"},
  "createdAt":            {"S": "2025-03-08T06:05:00"}
}
EOF
)"

# Alert 3: LOW severity — routine check
$AWS dynamodb put-item --table-name verigate-monitoring-alerts --region "$REGION" --item "$(cat <<EOF
{
  "alertId":              {"S": "alert-003"},
  "subjectId":            {"S": "subj-001"},
  "partnerId":            {"S": "$PARTNER_ID"},
  "severity":             {"S": "LOW"},
  "alertType":            {"S": "ROUTINE_CHECK"},
  "title":                {"S": "Routine monitoring check completed"},
  "description":          {"S": "Weekly sanctions and PEP screening completed with no new findings"},
  "previousRiskScore":    {"N": "85"},
  "currentRiskScore":     {"N": "85"},
  "previousDecision":     {"S": "APPROVE"},
  "currentDecision":      {"S": "APPROVE"},
  "acknowledged":         {"BOOL": false},
  "subjectIdCreatedAt":   {"S": "subj-001#2025-03-01T06:00:00"},
  "createdAt":            {"S": "2025-03-01T06:00:00"}
}
EOF
)"
echo "  3 monitoring alerts seeded."

echo ""
echo "=== Seed data complete ==="
echo "Partner ID:  $PARTNER_ID"
echo "API Key:     $RAW_KEY"
echo ""
echo "Use this key in the portal or curl:"
echo "  curl -H 'X-API-Key: $RAW_KEY' http://localhost:8080/api/partner/policies"
