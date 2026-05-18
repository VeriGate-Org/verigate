package verigate.adapter.dharesponse.domain.services;

import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;

public interface DhaResponseParserService {
    DhaResponseVerificationResult analyzeResponse(String emailBody, String permitNumber,
                                                   String permitType, String nationality);
}
