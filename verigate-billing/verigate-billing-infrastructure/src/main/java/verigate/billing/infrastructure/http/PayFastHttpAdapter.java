/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.http;

import com.google.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.application.services.DefaultPaymentService;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.Payment;
import verigate.billing.infrastructure.config.PayFastConfiguration;

/**
 * HTTP adapter for PayFast payment gateway integration.
 * Handles signature generation/validation and payment URL construction.
 */
public class PayFastHttpAdapter implements DefaultPaymentService.PayFastAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(PayFastHttpAdapter.class);

    // PayFast ITN source IP ranges
    private static final String[] PAYFAST_IP_PREFIXES = {
        "197.97.145.", "41.74.179."
    };

    private final PayFastConfiguration config;

    @Inject
    public PayFastHttpAdapter(PayFastConfiguration config) {
        this.config = config;
    }

    @Override
    public String getMerchantId() {
        return config.getMerchantId();
    }

    @Override
    public boolean validateSignature(Map<String, String> itnData) {
        String receivedSignature = itnData.get("signature");
        if (receivedSignature == null) {
            LOG.warn("No signature in ITN data");
            return false;
        }

        Map<String, String> sortedData = new TreeMap<>(itnData);
        sortedData.remove("signature");

        String paramString = sortedData.entrySet().stream()
            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue().trim(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        if (config.getPassphrase() != null && !config.getPassphrase().isEmpty()) {
            paramString += "&passphrase=" + URLEncoder.encode(config.getPassphrase(), StandardCharsets.UTF_8);
        }

        String calculatedSignature = md5(paramString);
        boolean valid = receivedSignature.equals(calculatedSignature);

        if (!valid) {
            LOG.warn("PayFast signature mismatch");
        }

        return valid;
    }

    @Override
    public boolean validateSourceIp(String sourceIp) {
        if (sourceIp == null) {
            return false;
        }
        for (String prefix : PAYFAST_IP_PREFIXES) {
            if (sourceIp.startsWith(prefix)) {
                return true;
            }
        }
        LOG.warn("PayFast ITN from unexpected IP: {}", sourceIp);
        return false;
    }

    @Override
    public String generatePaymentUrl(Payment payment, Invoice invoice) {
        String baseUrl = config.getBaseUrl() + "/eng/process";

        Map<String, String> params = new TreeMap<>();
        params.put("merchant_id", config.getMerchantId());
        params.put("merchant_key", config.getMerchantKey());
        params.put("amount", invoice.total().toPlainString());
        params.put("item_name", "VeriGate Invoice " + invoice.invoiceNumber());
        params.put("m_payment_id", payment.paymentId());
        params.put("item_description", "Billing period: " + invoice.billingPeriod());

        String paramString = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        if (config.getPassphrase() != null && !config.getPassphrase().isEmpty()) {
            String sigString = paramString + "&passphrase="
                + URLEncoder.encode(config.getPassphrase(), StandardCharsets.UTF_8);
            params.put("signature", md5(sigString));
        }

        String finalParams = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        return baseUrl + "?" + finalParams;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 computation failed", e);
        }
    }
}
