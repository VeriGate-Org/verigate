/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Credentials used to authenticate with the DeedsWeb SOAP service. Resolved per
 * verification request from AWS Secrets Manager.
 *
 * @param username SOAP username
 * @param password SOAP password
 */
public record DeedsWebCredentials(String username, String password) {
}
