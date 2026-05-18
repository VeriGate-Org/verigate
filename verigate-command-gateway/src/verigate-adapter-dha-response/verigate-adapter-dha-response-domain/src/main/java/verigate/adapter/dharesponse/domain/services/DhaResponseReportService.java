package verigate.adapter.dharesponse.domain.services;

import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;

public interface DhaResponseReportService {
    String generateReport(String commandId, String partnerId,
                          DhaResponseVerificationResult result);
}
