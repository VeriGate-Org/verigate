package verigate.webbff.partner.model;

public record NotificationPreferences(
    boolean verificationComplete,
    boolean verificationFailure,
    boolean weeklySummary,
    boolean securityAlerts) {}
