/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

import com.adobe.pdfservices.operation.PDFServices;
import com.adobe.pdfservices.operation.PDFServicesMediaType;
import com.adobe.pdfservices.operation.PDFServicesResponse;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.auth.ServicePrincipalCredentials;
import com.adobe.pdfservices.operation.exception.SDKException;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.Asset;
import com.adobe.pdfservices.operation.pdfjobs.jobs.DocumentMergeJob;
import com.adobe.pdfservices.operation.pdfjobs.jobs.ProtectPDFJob;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.DocumentMergeParams;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.OutputFormat;
import com.adobe.pdfservices.operation.pdfjobs.params.protectpdf.EncryptionAlgorithm;
import com.adobe.pdfservices.operation.pdfjobs.params.protectpdf.ProtectPDFParams;
import com.adobe.pdfservices.operation.pdfjobs.result.DocumentMergeResult;
import com.adobe.pdfservices.operation.pdfjobs.result.ProtectPDFResult;
import com.google.inject.Inject;
import crosscutting.environment.Environment;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.secrets.SecretManager;
import java.io.InputStream;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class demonstrates the creation of a PDF file using Adobe PDF services. It uses the
 * DocumentMerge operation to merge data from a JSON file into a PDF template.
 */
public class AdobePdfGenerator implements PdfGenerator {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdobePdfGenerator.class);
  private static Credentials credentials;
  private static PDFServices pdfServices;

  /**
   * Constructor for the AdobePdfGenerator class.
   */
  @Inject
  public AdobePdfGenerator(
      SecretManager secretManager,
      Environment environment,
      InternalTransportJsonSerializer serializer) {
    String adobeCredentialsStringified =
        secretManager.getSecret(
            "aws-verigate-" + environment.get("ENVIRONMENT") + "-verigate-adobe-pdf-client");

    AdobePdfGeneratorCredentials adobeCredentials =
        serializer.deserialize(adobeCredentialsStringified, AdobePdfGeneratorCredentials.class);

    credentials =
        new ServicePrincipalCredentials(
            adobeCredentials.clientId(), adobeCredentials.clientSecret());

    pdfServices = new PDFServices(credentials);
  }

  private Asset encryptAsset(Asset pdfToEncrypt, String password) throws ServiceApiException {
    // Create parameters for the job
    ProtectPDFParams protectPdfParams =
        ProtectPDFParams.passwordProtectOptionsBuilder()
            .setUserPassword(password)
            .setEncryptionAlgorithm(EncryptionAlgorithm.AES_256)
            .build();

    // Creates a new job instance
    ProtectPDFJob protectPdfJob = new ProtectPDFJob(pdfToEncrypt, protectPdfParams);

    // Submit the job and gets the job result
    String encryptionJobLocation = pdfServices.submit(protectPdfJob);

    PDFServicesResponse<ProtectPDFResult> encryptionJobServiceResponse =
        pdfServices.getJobResult(encryptionJobLocation, ProtectPDFResult.class);

    // Get content from the resulting asset(s)
    Asset encryptionResultAsset = encryptionJobServiceResponse.getResult().getAsset();

    return encryptionResultAsset;
  }

  private Asset genDoc(InputStream documentTemplate, String documentContentJson)
      throws ServiceApiException {
    // Creates an asset(s) from source file(s) and upload
    Asset asset = pdfServices.upload(documentTemplate, PDFServicesMediaType.DOCX.getMediaType());

    JSONObject jsonDataForMerge = new JSONObject(documentContentJson);

    // Create parameters for the job
    DocumentMergeParams documentMergeParams =
        DocumentMergeParams.documentMergeParamsBuilder()
            .withJsonDataForMerge(jsonDataForMerge)
            .withOutputFormat(OutputFormat.PDF)
            .build();

    // Creates a new job instance
    DocumentMergeJob documentMergeJob = new DocumentMergeJob(asset, documentMergeParams);

    // Submit the job and gets the job result
    String location = pdfServices.submit(documentMergeJob);

    PDFServicesResponse<DocumentMergeResult> pdfServicesResponse =
        pdfServices.getJobResult(location, DocumentMergeResult.class);

    // Get content from the resulting asset(s)
    Asset resultAsset = pdfServicesResponse.getResult().getAsset();

    return resultAsset;
  }

  @Override
  public InputStream generateDocument(
      InputStream documentTemplate, String documentContentJson, String password) {
    try {

      var generatedAsset = genDoc(documentTemplate, documentContentJson);

      var enryptedAsset = encryptAsset(generatedAsset, password);

      return pdfServices.getContent(enryptedAsset).getInputStream();

    } catch (ServiceApiException | SDKException | ServiceUsageException e) {
      LOGGER.error("Exception encountered while executing operation", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream generateDocument(InputStream documentTemplate, String documentContentJson) {
    try {

      var generatedAsset = genDoc(documentTemplate, documentContentJson);

      return pdfServices.getContent(generatedAsset).getInputStream();

    } catch (ServiceApiException | SDKException | ServiceUsageException e) {
      LOGGER.error("Exception encountered while executing operation", e);
      throw new RuntimeException(e);
    }
  }
}
