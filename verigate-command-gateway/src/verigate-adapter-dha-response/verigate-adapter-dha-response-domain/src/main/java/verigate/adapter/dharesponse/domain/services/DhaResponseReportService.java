package verigate.adapter.dharesponse.domain.services;

import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;

/** Service for generating reports from DHA response verification results. */
public interface DhaResponseReportService {
  String generateReport(String commandId, String partnerId,
      DhaResponseVerificationResult result);
}
