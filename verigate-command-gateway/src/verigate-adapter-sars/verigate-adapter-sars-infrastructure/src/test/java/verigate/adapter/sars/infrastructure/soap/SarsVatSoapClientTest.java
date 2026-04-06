/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpServer;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.sars.domain.models.VatVendorDetails;
import verigate.adapter.sars.infrastructure.config.SarsEfilingCredentials;

class SarsVatSoapClientTest {

    private HttpServer server;
    private String baseUrl;
    private SarsEfilingCredentials credentials;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;

        credentials = mock(SarsEfilingCredentials.class);
        when(credentials.getLoginName()).thenReturn("testuser");
        when(credentials.getPassword()).thenReturn("testpass");
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void search_returnsVendorDetailsOnHttp200() {
        String responseBody = buildSuccessfulSoapResponse(
            "4123456789", "Acme Trading (Pty) Ltd", "Acme Trading",
            "2015/03/01", "Active", "47110",
            "123 Main Rd", "Sandton", "", "", "2196");

        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(200, responseBody.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofSeconds(10));

        VatVendorDetails result = client.search("4123456789", null);

        assertNotNull(result);
        assertEquals("4123456789", result.vatNumber());
        assertEquals("Acme Trading (Pty) Ltd", result.vendorName());
    }

    @Test
    void search_throwsPermanentExceptionOnSoapFault() {
        String faultResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<soap:Body>"
            + "<soap:Fault>"
            + "<faultcode>soap:Server</faultcode>"
            + "<faultstring>Authentication failed</faultstring>"
            + "</soap:Fault>"
            + "</soap:Body>"
            + "</soap:Envelope>";

        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(500, faultResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(faultResponse.getBytes());
            }
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofSeconds(10));

        PermanentException ex = assertThrows(PermanentException.class,
            () -> client.search("4123456789", null));
        assertTrue(ex.getMessage().contains("SOAP fault"));
    }

    @Test
    void search_throwsTransientExceptionOnHttp429() {
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(429, -1);
            exchange.close();
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofSeconds(10));

        TransientException ex = assertThrows(TransientException.class,
            () -> client.search("4123456789", null));
        assertTrue(ex.getMessage().contains("429"));
    }

    @Test
    void search_throwsTransientExceptionOnHttp503() {
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofSeconds(10));

        TransientException ex = assertThrows(TransientException.class,
            () -> client.search("4123456789", null));
        assertTrue(ex.getMessage().contains("503"));
    }

    @Test
    void search_throwsPermanentExceptionOnHttp400() {
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofSeconds(10));

        PermanentException ex = assertThrows(PermanentException.class,
            () -> client.search("4123456789", null));
        assertTrue(ex.getMessage().contains("400"));
    }

    @Test
    void search_throwsTransientExceptionOnTimeout() {
        server.createContext("/", exchange -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        SarsVatSoapClient client = new SarsVatSoapClient(
            baseUrl, credentials, Duration.ofMillis(100));

        assertThrows(TransientException.class,
            () -> client.search("4123456789", null));
    }

    @Test
    void search_throwsTransientExceptionOnConnectionRefused() {
        // Use a port that is not listening
        SarsVatSoapClient client = new SarsVatSoapClient(
            "http://localhost:1", credentials, Duration.ofSeconds(2));

        assertThrows(TransientException.class,
            () -> client.search("4123456789", null));
    }

    private String buildSuccessfulSoapResponse(
        String regNum, String name, String tradeName,
        String regDt, String status, String activityCd,
        String addr1, String addr2, String addr3, String addr4, String postCode) {
      return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
          + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
          + "<soap:Body>"
          + "<SearchResponse xmlns=\"http://www.sars.gov.za\">"
          + "<SearchResult>"
          + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"></xs:schema>"
          + "<diffgr:diffgram xmlns:diffgr=\"urn:schemas-microsoft-com:xml-diffgram-v1\">"
          + "<NewDataSet>"
          + "<Table>"
          + "<Reg_Num>" + regNum + "</Reg_Num>"
          + "<Name>" + name + "</Name>"
          + "<Trade_Name>" + tradeName + "</Trade_Name>"
          + "<Reg_Dt>" + regDt + "</Reg_Dt>"
          + "<Status>" + status + "</Status>"
          + "<Activity_Cd>" + activityCd + "</Activity_Cd>"
          + "<Addr_Line1>" + addr1 + "</Addr_Line1>"
          + "<Addr_Line2>" + addr2 + "</Addr_Line2>"
          + "<Addr_Line3>" + addr3 + "</Addr_Line3>"
          + "<Addr_Line4>" + addr4 + "</Addr_Line4>"
          + "<Post_Code>" + postCode + "</Post_Code>"
          + "</Table>"
          + "</NewDataSet>"
          + "</diffgr:diffgram>"
          + "</SearchResult>"
          + "</SearchResponse>"
          + "</soap:Body>"
          + "</soap:Envelope>";
    }
}
