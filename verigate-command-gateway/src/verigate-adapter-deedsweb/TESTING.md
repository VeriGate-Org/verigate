# DeedsWeb Integration — Testing Guide

> **Infrastructure state as of 2026-06-23:** The BigIP load balancer in front of DeedsWeb is
> returning HTTP 302 for all SOAP operation-specific POST requests. Tests that make live SOAP
> calls will throw `TransientException` until the Deeds Office reconfigures BigIP.
> TLS handshake and base-URL GET both work correctly.

---

## Prerequisites

| Requirement | Detail |
|---|---|
| Java | JDK 17+ (project targets Java 21) |
| Maven | 3.9.x (download to `C:\tools\maven\apache-maven-3.9.9` if not in PATH) |
| GitHub PAT | Classic token with `read:packages` scope — required to download `verigate-shared-kernel` |
| AWS CLI | Configured with credentials for `af-south-1`, profile targeting the `verigate` stack |
| AVG SSL fix | If AVG Web/Mail Shield is active, use the custom truststore (see Stage 1) |

---

## Stage 1: Unblock the build (GitHub PAT)

### 1.1 Create a Classic PAT

Go to `github.com/settings/tokens/new?scopes=read:packages` and create a token with the
`read:packages` scope. Then update `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_CLASSIC_PAT_WITH_READ_PACKAGES</password>
    </server>
  </servers>
</settings>
```

### 1.2 AVG SSL workaround (Windows only)

If AVG Web/Mail Shield intercepts HTTPS, Maven cannot reach `maven.pkg.github.com` with the
default JDK truststore. Fix by pointing Maven at a custom truststore that includes the AVG CA:

```powershell
$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStore=C:\temp\cacerts-custom -Djavax.net.ssl.trustStorePassword=changeit"
```

### 1.3 Set PATH (if Maven is not installed system-wide)

```powershell
$env:PATH     = "C:\tools\maven\apache-maven-3.9.9\bin;$env:PATH"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
```

### 1.4 Delete any stale resolution marker

```powershell
Remove-Item -Force "$env:USERPROFILE\.m2\repository\verigate\verigate-shared-kernel\1.0.2\verigate-shared-kernel-1.0.2.pom.lastUpdated" -ErrorAction SilentlyContinue
```

### 1.5 Build the adapter (skip tests for speed)

Run from the repository root (`verigate-command-gateway/`):

```powershell
mvn clean install `
  -pl src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain,`
     src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application,`
     src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure `
  -am -DskipTests
```

---

## Stage 2: Local unit + WireMock tests (no network required)

These cover all code changes — redirect classification, DH key fix, JAXB mapping — without
hitting the real DeedsWeb endpoint.

```powershell
mvn test `
  -pl src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-domain,`
     src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-application,`
     src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure `
  -am
```

### Expected results

| Test class | What it validates |
|---|---|
| `SoapErrorClassifierTest` | Redirect (301/302), HTML, auth detection |
| `CxfDeedsRegistryClientWireMockTest` | Full CXF stack — happy path, HTML → `PermanentException`, 302 → `TransientException` |
| `CxfDeedsRegistryClientTest` | Fan-out logic, partial failures, credential forwarding |
| `SoapResponseMapperTest` | JAXB response → domain model mapping |
| `DefaultPropertyOwnershipVerificationServiceTest` | Search dispatch, filtering, confidence scoring |
| `DefaultPropertyVerificationCommandHandlerTest` | Command handler wiring |

All tests should pass without network access.

---

## Stage 3: Live JVM integration test (must run from VPC)

The test class is `DeedsWebLiveIntegrationTest`. It is gated by
`-Dintegration.test.enabled=true` and reads credentials from
`src/test/resources/integration-test-local.properties` (gitignored).

### 3.1 Populate credentials

```properties
# src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/src/test/resources/integration-test-local.properties
deedsweb.username=<username from Secrets Manager verigate/deedsweb/credentials>
deedsweb.password=<password from Secrets Manager verigate/deedsweb/credentials>
integration.test.enabled=true
```

Fetch from AWS:

```powershell
aws secretsmanager get-secret-value `
  --secret-id verigate/deedsweb/credentials `
  --region af-south-1 `
  --query SecretString `
  --output text `
  --no-verify-ssl
```

### 3.2 Run the tests

The test **must execute from inside the VPC** (or from a host whose IP is whitelisted by the
Deeds Office — currently NAT EIP `13.246.247.144`). Run via an EC2 bastion, a Lambda invoke,
or from a GitHub Actions runner in the VPC.

```bash
mvn test \
  -pl src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure \
  -Dtest=DeedsWebLiveIntegrationTest \
  -Dintegration.test.enabled=true
```

### 3.3 Expected results (current state — BigIP broken)

| Test | Expected |
|---|---|
| `httpsEndpoint_tlsHandshakeSucceeds` | **PASS** — TLS layer works with 1024-bit DHE relaxation |
| `getOfficeRegistryList_bigIpRedirect_throwsTransient` | **PASS** — documents current 302 behaviour |
| `getOfficeRegistryList_connectsAndReturnsOffices` | FAIL (`TransientException`) — BigIP redirecting |
| `findPropertiesByIdNumber_returnsResultsForKnownTestId` | FAIL (`TransientException`) — BigIP redirecting |

### 3.4 Expected results (after BigIP fix)

All four tests should pass. Re-run Stage 3 after the Deeds Office confirms routing is fixed.

---

## Stage 4: Deploy the Lambda to AWS

### 4.1 Build the deployable JAR

```bash
mvn clean package \
  -pl src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure \
  -am -DskipTests
```

### 4.2 Option A — Full SAM deploy

```bash
cd iac/sam
sam build
sam deploy \
  --stack-name verigate \
  --region af-south-1 \
  --no-confirm-changeset \
  --no-fail-on-empty-changeset
```

### 4.3 Option B — Quick function-only update (faster, skips CloudFormation)

```bash
aws lambda update-function-code \
  --function-name verigate-verify-property-ownership \
  --zip-file fileb://src/verigate-adapter-deedsweb/verigate-adapter-deedsweb-infrastructure/target/verigate-adapter-deedsweb-infrastructure-1.0-SNAPSHOT.jar \
  --region af-south-1 \
  --no-verify-ssl
```

---

## Stage 5: End-to-end test via SQS → Lambda → CloudWatch

### 5.1 Send a test command directly to the DeedsWeb adapter queue

This bypasses the command gateway router and targets the adapter queue directly, which is
faster for isolated DeedsWeb testing.

**Queue ARN:** `arn:aws:sqs:af-south-1:379992419891:verigate-adapter-deedsweb`

```powershell
$msg = @'
{
  "id": "00000000-0000-0000-0000-000000000099",
  "createdAt": "2026-06-23T00:00:00Z",
  "createdBy": "integration-test",
  "partnerId": "test-partner",
  "verificationType": "PROPERTY_OWNERSHIP_VERIFICATION",
  "origination": { "source": "test", "reference": "test-ref-001" },
  "metadata": {
    "searchType": "ownerId",
    "query": "8001015009087",
    "officeCode": "T"
  }
}
'@

aws sqs send-message `
  --queue-url "https://sqs.af-south-1.amazonaws.com/379992419891/verigate-adapter-deedsweb" `
  --message-body $msg `
  --region af-south-1 `
  --no-verify-ssl
```

### 5.2 Supported metadata fields

| Field | Values | Notes |
|---|---|---|
| `searchType` | `ownerId`, `company`, `ownerName` | Determines which SOAP operation is called |
| `query` | ID number / company name | The search term |
| `officeCode` | `T` (Pretoria), `J` (Johannesburg), `C` (Cape Town), ... or `all` | `all` / omit = fan-out across all offices |
| `province` | Province name string | Optional post-fetch filter |

### 5.3 Watch CloudWatch Logs in real time

```powershell
aws logs tail "/aws/lambda/verigate-verify-property-ownership" `
  --follow `
  --region af-south-1 `
  --no-verify-ssl
```

### 5.4 What to look for in logs

**Right now (BigIP broken):**
```
DeedsWeb BigIP returned HTTP redirect for SOAP operation URL: HTTP response '302: Found' ...
```
This confirms TLS works, credentials resolved, and SOAP call dispatched. Only BigIP routing
is blocking the response.

**After BigIP is fixed:**
```json
{ "level": "INFO", "message": "Property ownership verification completed", "deedNumber": "T.../..." }
```

### 5.5 Check the Dead Letter Queue

If the Lambda exhausts retries, the message lands in the DLQ:

```powershell
aws sqs receive-message `
  --queue-url "https://sqs.af-south-1.amazonaws.com/379992419891/verigate-adapter-deedsweb-dlq" `
  --region af-south-1 `
  --no-verify-ssl
```

---

## Summary: what each stage proves

| Stage | Proves |
|---|---|
| Unit + WireMock tests | Redirect/TLS code changes correct; error classification wired right |
| Live TLS test | DHE 1024-bit relaxation works against the real server |
| Live redirect test | BigIP is returning 302 (documents current state) |
| Live happy-path tests | Full SOAP round-trip works (run after BigIP fix) |
| SQS → Lambda | Cold-start, Secrets Manager fetch, and SOAP call work end-to-end |

---

## Known infrastructure issues (as of 2026-06-23)

### BigIP HTTP 302 redirect

All SOAP operation-specific POST requests (e.g. `/deeds-registration-soap/getOfficeRegistryList`)
are redirected by BigIP to the base path (`/deeds-registration-soap/`). The CXF server at the
base path returns its HTML service-listing page instead of processing the SOAP envelope.

**Our handling:** `CxfPortFactory` sets `autoRedirect=false` so CXF surfaces the 302 as a
`WebServiceException`. `CxfDeedsRegistryClient.classifyTransport()` detects the redirect via
`SoapErrorClassifier.isRedirectError()` and wraps it in `TransientException`, allowing the
gateway to retry once BigIP routing is corrected.

**Action required:** The Deeds Office must reconfigure BigIP to pass operation-specific POST
requests through to the CXF backend without redirecting.

### TLS DHE key size

The DeedsWeb server negotiates TLS using a 1024-bit DHE key. JDK 17+ rejects DHE keys smaller
than 2048 bits by default (`jdk.tls.disabledAlgorithms`).

**Our handling:** `CxfPortFactory` contains a static initializer that lowers the DHE minimum
from 2048 → 1024 bits at JVM startup. This only affects the DHE key-size threshold; certificate
chain validation is unchanged.
