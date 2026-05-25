/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.services.PaymentService;
import verigate.billing.infrastructure.functions.lambda.di.factories.PaymentWebhookDependencyFactory;

/**
 * AWS Lambda handler for PayFast ITN (Instant Transaction Notification) webhooks.
 * Always returns HTTP 200 to acknowledge receipt, regardless of processing outcome.
 */
public class PaymentWebhookLambdaHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG =
        LoggerFactory.getLogger(PaymentWebhookLambdaHandler.class);

    private final PaymentService paymentService;

    public PaymentWebhookLambdaHandler() {
        this(new PaymentWebhookDependencyFactory());
    }

    public PaymentWebhookLambdaHandler(PaymentWebhookDependencyFactory factory) {
        this.paymentService = factory.getPaymentService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
        APIGatewayProxyRequestEvent event, Context context) {

        LOG.info("PayFast ITN webhook received");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("OK");

        try {
            String body = event.getBody();
            if (body == null || body.isBlank()) {
                LOG.warn("Empty ITN body received");
                return response;
            }

            Map<String, String> itnData = parseFormEncodedBody(body);

            if (event.getRequestContext() != null
                && event.getRequestContext().getIdentity() != null) {
                itnData.put("source_ip",
                    event.getRequestContext().getIdentity().getSourceIp());
            }

            paymentService.processItnNotification(itnData);
            LOG.info("PayFast ITN processed successfully");

        } catch (Exception e) {
            LOG.error("Failed to process PayFast ITN", e);
        }

        return response;
    }

    private Map<String, String> parseFormEncodedBody(String body) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }
}
