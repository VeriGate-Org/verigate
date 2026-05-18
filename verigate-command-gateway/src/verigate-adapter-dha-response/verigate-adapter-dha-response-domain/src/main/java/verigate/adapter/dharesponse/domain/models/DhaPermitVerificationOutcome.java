package verigate.adapter.dharesponse.domain.models;

public enum DhaPermitVerificationOutcome {
    VERIFIED,
    INVALID,
    EXPIRED,
    NOT_FOUND,
    PARTIAL_MATCH,
    UNABLE_TO_VERIFY,
    INCONCLUSIVE
}
