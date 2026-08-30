/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.datanamix.infrastructure.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import crosscutting.environment.Environment;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import verigate.adapter.datanamix.domain.models.CompanyProfile;
import verigate.adapter.datanamix.domain.models.CompanySearchMatch;
import verigate.adapter.datanamix.domain.models.DirectorPortfolio;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;

/**
 * Live integration test against the real Datanamix sandbox API. Skipped by default
 * unless {@code -Dintegration.test.enabled=true} is supplied (same convention as
 * {@code DeedsWebLiveIntegrationTest}).
 *
 * <p>Unlike DeedsWeb, this test needs no credentials at all: {@code EnvironmentType:
 * SANDBOX} requests neither an OAuth token nor an {@code Authorization} header, per
 * Datanamix's own auth documentation. It calls the real, live sandbox endpoints
 * end-to-end (director-search/director-result for story 2.3, company-search/
 * company-result for story 2.2), proving the whole client works against Datanamix's
 * actual API shape, not just against fixtures.
 *
 * <p>Assertions are deliberately structural (non-null, well-formed), not pinned to the
 * illustrative example values from the docs -- sandbox "fake data" behavior may not
 * exactly match the docs' example response.
 */
class DatanamixLiveIntegrationTest {

  // Sandbox matrix: a name search for "XYZ Shoes" is documented to return the "XYZ
  // Shoes" results with a primary EnquiryID/EnquiryResultID pair of 234567/876543,
  // usable against company-result.
  private static final String SANDBOX_TEST_COMPANY_NAME = "XYZ Shoes";

  // NOTE: Datanamix's own docs example ID ("0101010000081") fails their live sandbox
  // validation ("Invalid RSA ID Number") -- a real doc-vs-reality discrepancy found by
  // running this test, not a typo here. This codebase's own existing test fixture ID
  // ("8501015009087", used throughout DefaultVerifyDocumentCommandHandlerTest etc.)
  // ALSO fails Luhn validation -- verified independently. Using a properly Luhn-valid
  // SA ID computed and confirmed for this test instead.
  private static final String SANDBOX_TEST_ID_NUMBER = "9001015001083";

  @Test
  @EnabledIfSystemProperty(named = "integration.test.enabled", matches = "true")
  void shouldSearchDirectorPortfolioAgainstRealSandbox() throws Exception {
    // No env vars set -> DatanamixApiConfiguration defaults to SANDBOX, no auth needed.
    Environment environment = new Environment() {
      @Override
      public String get(String key) {
        return null;
      }

      @Override
      public String get(String key, String defaultValue) {
        return defaultValue;
      }
    };
    DatanamixApiConfiguration configuration = new DatanamixApiConfiguration(environment);
    ObjectMapper objectMapper = new ObjectMapper();
    DatanamixOAuthTokenProvider tokenProvider =
        new DatanamixOAuthTokenProvider(configuration, objectMapper);
    DatanamixHttpAdapter httpAdapter =
        new DatanamixHttpAdapter(configuration, tokenProvider, objectMapper);
    DatanamixDirectorSearchClient client =
        new DatanamixDirectorSearchClient(httpAdapter, configuration);

    DirectorPortfolio portfolio = client.searchByIdNumber(SANDBOX_TEST_ID_NUMBER);

    assertNotNull(portfolio);
    // Sandbox is documented to return deterministic fake data for a well-formed request,
    // so a real HTTP round trip completing without throwing -- and returning a
    // structurally valid portfolio either way -- is the actual proof this test exists
    // for. If found, directorships should be a valid (possibly empty) list, never null.
    assertNotNull(portfolio.directorships());
  }

  @Test
  @EnabledIfSystemProperty(named = "integration.test.enabled", matches = "true")
  void shouldSearchCompanyByNameAgainstRealSandbox() throws Exception {
    DatanamixCompanySearchClient client = newCompanySearchClient();

    List<CompanySearchMatch> matches = client.searchByName(SANDBOX_TEST_COMPANY_NAME);

    assertNotNull(matches);
    assertTrue(!matches.isEmpty(), "Sandbox is documented to return XYZ Shoes results for this search");
  }

  @Test
  @EnabledIfSystemProperty(named = "integration.test.enabled", matches = "true")
  void shouldResolveCompanyProfileEndToEndAgainstRealSandbox() throws Exception {
    DatanamixCompanySearchClient client = newCompanySearchClient();

    // Chains company-search -> company-result exactly as a real "search by name" flow
    // would: search first, then resolve the first match's full profile.
    List<CompanySearchMatch> matches = client.searchByName(SANDBOX_TEST_COMPANY_NAME);
    assertTrue(!matches.isEmpty());

    CompanySearchMatch firstMatch = matches.get(0);
    CompanyProfile profile = client.getProfile(firstMatch.enquiryId(), firstMatch.enquiryResultId());

    assertNotNull(profile);
    assertTrue(profile.found(), "Resolving a match returned by search should yield a found profile");
    assertNotNull(profile.businessName());
    assertNotNull(profile.directors());
  }

  private DatanamixCompanySearchClient newCompanySearchClient() {
    // No env vars set -> DatanamixApiConfiguration defaults to SANDBOX, no auth needed.
    Environment environment = new Environment() {
      @Override
      public String get(String key) {
        return null;
      }

      @Override
      public String get(String key, String defaultValue) {
        return defaultValue;
      }
    };
    DatanamixApiConfiguration configuration = new DatanamixApiConfiguration(environment);
    ObjectMapper objectMapper = new ObjectMapper();
    DatanamixOAuthTokenProvider tokenProvider =
        new DatanamixOAuthTokenProvider(configuration, objectMapper);
    DatanamixHttpAdapter httpAdapter =
        new DatanamixHttpAdapter(configuration, tokenProvider, objectMapper);
    return new DatanamixCompanySearchClient(httpAdapter, configuration);
  }
}
