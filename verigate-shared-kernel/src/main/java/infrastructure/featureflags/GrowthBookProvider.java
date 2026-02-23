/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.featureflags;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import domain.exceptions.PermanentException;
import growthbook.sdk.java.GBContext;
import growthbook.sdk.java.GrowthBook;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import org.joda.time.Instant;
import org.json.JSONObject;

/** Provides feature flags using GrowthBook. */
public class GrowthBookProvider implements FeatureFlags {

  private GrowthBook growthBook;
  private final Environment environment;
  private final Integer refreshIntervalMilliseconds = 30000;
  private Instant lastRefresh = null;

  // Temporary logging
  private Logger logger = Logger.getLogger(GrowthBookProvider.class.getName());

  /**
   * Initializes a new instance of the GrowthBookProvider class.
   *
   * @param env The environment to use.
   * @throws URISyntaxException If the URI is invalid.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the operation is interrupted.
   */
  @Inject
  public GrowthBookProvider(Environment env) {
    environment = env;
    try {
      fetchFeatures();
    } catch (PermanentException | URISyntaxException | IOException | InterruptedException e) {
      logger.severe("Failed to fetch features from GrowthBook");
    }
  }

  private void fetchFeatures()
      throws PermanentException, URISyntaxException, IOException, InterruptedException {
    if (lastRefresh == null || lastRefresh.plus(refreshIntervalMilliseconds).isBeforeNow()) {
      try {
        logger.info("Growthbook: Cache invalid. Fetching features");
        URI featuresEndpoint = new URI(environment.get("GROWTHBOOK_URI"));
        HttpRequest request = HttpRequest.newBuilder().uri(featuresEndpoint).GET().build();
        HttpResponse<String> response =
            HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        String featuresJson = new JSONObject(response.body()).get("features").toString();

        GBContext context = GBContext.builder().featuresJson(featuresJson).build();

        growthBook = new GrowthBook(context);

        lastRefresh = Instant.now();

        logger.info(
            "Growthbook: Features fetched. Expires at: "
                + lastRefresh.plus(refreshIntervalMilliseconds).toString());

      } catch (PermanentException | URISyntaxException | IOException | InterruptedException e) {
        throw new RuntimeException("Failed to fetch features from GrowthBook", e);
      }
    }
  }

  @Override
  public boolean isFeatureEnabled(String featureName, boolean defaultValue) {
    try {
      fetchFeatures();
    } catch (PermanentException | URISyntaxException | IOException | InterruptedException e) {
      logger.severe("Failed to fetch features from GrowthBook");
      return defaultValue;
    }
    return growthBook.isOn(featureName);
  }
}
