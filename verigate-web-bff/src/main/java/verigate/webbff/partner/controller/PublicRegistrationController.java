package verigate.webbff.partner.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.admin.model.PartnerRegistrationRequest;
import verigate.webbff.admin.model.PartnerRegistrationResponse;
import verigate.webbff.admin.service.PartnerRegistrationService;

@RestController
@RequestMapping("/api/public")
public class PublicRegistrationController {

  private static final Logger logger = LoggerFactory.getLogger(PublicRegistrationController.class);

  private final PartnerRegistrationService registrationService;

  public PublicRegistrationController(PartnerRegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping("/register")
  public ResponseEntity<PartnerRegistrationResponse> register(
      @Valid @RequestBody PartnerRegistrationRequest request) {
    logger.info("Partner registration request: email={}, company={}", request.email(), request.companyName());
    PartnerRegistrationResponse response = registrationService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
