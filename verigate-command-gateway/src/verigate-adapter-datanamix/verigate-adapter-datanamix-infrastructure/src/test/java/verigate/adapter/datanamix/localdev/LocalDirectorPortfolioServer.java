/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.localdev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import crosscutting.environment.EnvironmentConfig;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import verigate.adapter.datanamix.domain.models.DirectorPortfolio;
import verigate.adapter.datanamix.domain.models.DirectorshipEntry;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;
import verigate.adapter.datanamix.infrastructure.http.DatanamixDirectorSearchClient;
import verigate.adapter.datanamix.infrastructure.http.DatanamixHttpAdapter;
import verigate.adapter.datanamix.infrastructure.http.DatanamixOAuthTokenProvider;

/**
 * ============================================================================================
 * TEMPORARY LOCAL DEV BRIDGE -- NOT PRODUCTION ARCHITECTURE. DO NOT DEPLOY. DO NOT WIRE INTO
 * ANY LAMBDA / SAM / TERRAFORM CONFIG.
 * ============================================================================================
 *
 * <p>Story 2.3's director search backend ({@link DatanamixDirectorSearchClient}) is real,
 * tested, and proven against live Datanamix sandbox -- but the codebase has no proven
 * mechanism yet for a command-gateway adapter's result data to reach a caller (verified: the
 * SQS command handler's return value is discarded, and verification events carry no payload
 * -- see story 2.3 command-gateway wiring scoping notes). The correct fix is either a real
 * synchronous Lambda invoked by the BFF, or publishing this module as an installable artifact
 * the BFF can depend on directly. Neither is a same-day task.
 *
 * <p>This class exists so the portal can show real Datanamix data <em>today</em>, without
 * deploying anything, by running this as a plain local process and having the portal call it
 * directly. It is intentionally kept in {@code src/test/java} so it is never packaged into any
 * production artifact.
 *
 * <p><b>Run it:</b> {@code mvn -pl src/verigate-adapter-datanamix/verigate-adapter-datanamix-infrastructure
 * test-compile exec:java -Dexec.mainClass=verigate.adapter.datanamix.localdev.LocalDirectorPortfolioServer
 * -Dexec.classpathScope=test} (or run {@code main()} directly from an IDE).
 *
 * <p><b>Call it:</b> {@code GET http://localhost:8090/director-portfolio/{idNumber}}
 *
 * <p><b>When picking this back up once all stories are done</b>: delete this class and replace
 * it with the real wiring (Lambda + BFF synchronous invoke was the recommended real approach).
 */
public final class LocalDirectorPortfolioServer {

  private static final int PORT = 8090;
  private static final String ROUTE_PREFIX = "/director-portfolio/";

  private LocalDirectorPortfolioServer() {
  }

  /**
   * Starts the local bridge server. Blocks until the process is killed (Ctrl+C).
   */
  public static void main(String[] args) throws IOException, PermanentException {
    ObjectMapper objectMapper = new ObjectMapper();
    DatanamixApiConfiguration configuration = new DatanamixApiConfiguration(new EnvironmentConfig());
    DatanamixOAuthTokenProvider tokenProvider =
        new DatanamixOAuthTokenProvider(configuration, objectMapper);
    DatanamixHttpAdapter httpAdapter =
        new DatanamixHttpAdapter(configuration, tokenProvider, objectMapper);
    DatanamixDirectorSearchClient client =
        new DatanamixDirectorSearchClient(httpAdapter, configuration);

    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
    server.createContext(ROUTE_PREFIX, exchange -> handleRequest(exchange, client, objectMapper));
    server.setExecutor(null);
    server.start();

    printBanner();
  }

  private static void handleRequest(
      HttpExchange exchange, DatanamixDirectorSearchClient client, ObjectMapper objectMapper)
      throws IOException {

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Content-Type", "application/json");

    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
      exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
      exchange.sendResponseHeaders(204, -1);
      return;
    }

    String path = exchange.getRequestURI().getPath();
    String idNumber = path.startsWith(ROUTE_PREFIX) ? path.substring(ROUTE_PREFIX.length()) : "";

    try {
      DirectorPortfolio portfolio = client.searchByIdNumber(idNumber);
      writeJson(exchange, 200, toResponseBody(portfolio));
    } catch (TransientException e) {
      writeJson(exchange, 503, errorBody("Datanamix temporarily unavailable: " + e.getMessage()));
    } catch (PermanentException e) {
      writeJson(exchange, 400, errorBody(e.getMessage()));
    } catch (Exception e) {
      writeJson(exchange, 500, errorBody("Unexpected error: " + e.getMessage()));
    }
  }

  private static Object toResponseBody(DirectorPortfolio portfolio) {
    List<Object> directorships = portfolio.directorships().stream()
        .map(LocalDirectorPortfolioServer::toDirectorshipJson)
        .toList();
    return java.util.Map.of(
        "found", portfolio.found(),
        "idNumber", portfolio.idNumber() != null ? portfolio.idNumber() : "",
        "firstName", portfolio.firstName() != null ? portfolio.firstName() : "",
        "surname", portfolio.surname() != null ? portfolio.surname() : "",
        "directorships", directorships);
  }

  private static Object toDirectorshipJson(DirectorshipEntry entry) {
    java.util.Map<String, String> json = new java.util.LinkedHashMap<>();
    json.put("companyName", entry.companyName());
    json.put("registrationNumber", entry.registrationNumber());
    json.put("companyStatus", entry.companyStatus());
    json.put("directorStatus", entry.directorStatus());
    json.put("designation", entry.designation());
    json.put("appointmentDate", entry.appointmentDate() != null ? entry.appointmentDate().toString() : null);
    return json;
  }

  private static Object errorBody(String message) {
    return java.util.Map.of("error", message);
  }

  private static void writeJson(HttpExchange exchange, int statusCode, Object body)
      throws IOException {
    byte[] bytes = new ObjectMapper().writeValueAsBytes(body);
    exchange.sendResponseHeaders(statusCode, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes, 0, bytes.length);
    }
  }

  private static void printBanner() {
    String banner = """

        ================================================================================
         TEMPORARY LOCAL DEV BRIDGE for story 2.3 director search -- NOT PRODUCTION.
         Do not deploy. Delete once the real Lambda+BFF wiring is built.

         Listening on: http://localhost:%d%s{idNumber}
         Example:      curl http://localhost:%d%s9001015001083

         Calls Datanamix's real sandbox API live -- no credentials needed for SANDBOX.
         Press Ctrl+C to stop.
        ================================================================================
        """.formatted(PORT, ROUTE_PREFIX, PORT, ROUTE_PREFIX);
    System.out.println(banner);
  }
}
