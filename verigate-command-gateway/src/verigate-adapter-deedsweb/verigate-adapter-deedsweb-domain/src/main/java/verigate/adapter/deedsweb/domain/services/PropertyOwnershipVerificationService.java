/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.services;

import java.util.List;
import verigate.adapter.deedsweb.domain.models.OwnershipVerificationResult;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertyOwnershipCheck;

/**
 * Service interface for property ownership verification against DeedsWeb records.
 * Provides capabilities to verify property ownership, retrieve property records,
 * and evaluate ownership from pre-fetched property data.
 */
public interface PropertyOwnershipVerificationService {

  /**
   * Performs a full property ownership verification for a subject.
   * Searches DeedsWeb for properties registered to the subject and evaluates ownership.
   *
   * @param subjectIdNumber the ID number of the person being checked
   * @param subjectName the name of the person being checked
   * @param propertyDescription optional property description to narrow the search
   * @return the complete property ownership check aggregate with results
   */
  PropertyOwnershipCheck verifyOwnership(
      String subjectIdNumber, String subjectName, String propertyDescription);

  /**
   * Finds all properties registered to an owner by their ID number.
   *
   * @param ownerIdNumber the ID number of the property owner
   * @return a list of property details for all properties found
   */
  List<PropertyDetails> findPropertiesByOwner(String ownerIdNumber);

  /**
   * Evaluates property ownership from an already-retrieved list of properties.
   * Useful when property data has been fetched separately and only the
   * ownership evaluation logic is needed.
   *
   * @param subjectIdNumber the ID number of the person being checked
   * @param subjectName the name of the person being checked
   * @param properties the pre-fetched list of property details to evaluate
   * @return the ownership verification result
   */
  OwnershipVerificationResult checkOwnership(
      String subjectIdNumber, String subjectName, List<PropertyDetails> properties);
}
