package verigate.adapter.dharesponse.domain.services;

import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;

/** Service for parsing DHA email responses into verification results. */
public interface DhaResponseParserService {
  DhaResponseVerificationResult analyzeResponse(String emailBody, String permitNumber,
      String permitType, String nationality);
}
