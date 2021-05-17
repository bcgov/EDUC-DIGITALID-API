package ca.bc.gov.educ.api.digitalid.controller.v1;

import ca.bc.gov.educ.api.digitalid.endpoint.v1.DigitalIDEndpoint;
import ca.bc.gov.educ.api.digitalid.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.digitalid.exception.errors.ApiError;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.service.v1.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.v1.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.v1.IdentityTypeCode;
import ca.bc.gov.educ.api.digitalid.validator.DigitalIDPayloadValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Digital Identity controller
 *
 * @author John Cox
 */

@RestController
@Slf4j
public class DigitalIDController implements DigitalIDEndpoint {
  private static final DigitalIDMapper mapper = DigitalIDMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDService service;

  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDPayloadValidator payloadValidator;


  @Autowired
  public DigitalIDController(final DigitalIDService digitalIDService, final DigitalIDPayloadValidator payloadValidator) {
    this.payloadValidator = payloadValidator;
    this.service = digitalIDService;
  }

  @Override
  public DigitalID searchDigitalID(final String typeCode, final String typeValue) {
    return mapper.toStructure(this.service.searchDigitalId(typeCode, typeValue));
  }

  @Override
  public DigitalID retrieveDigitalID(final String id) {
    return mapper.toStructure(this.service.retrieveDigitalID(UUID.fromString(id)));
  }

  @Override
  public List<AccessChannelCode> retrieveAccessChannelCodes() {
    return this.service.getAccessChannelCodesList().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public List<IdentityTypeCode> retrieveIdentityTypeCodes() {
    return this.service.getIdentityTypeCodesList().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public DigitalID createDigitalID(final DigitalID digitalID) {
    val validationResult = this.getPayloadValidator().validatePayload(digitalID, true);
    if (!validationResult.isEmpty()) {
      final ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
    this.setAuditColumns(digitalID);
    return mapper.toStructure(this.service.createDigitalID(mapper.toModel(digitalID)));
  }

  @Override
  public DigitalID updateDigitalID(final DigitalID digitalID, final UUID id) {
    val validationResult = this.getPayloadValidator().validatePayload(digitalID, false);
    if (!validationResult.isEmpty()) {
      final ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
    this.setAuditColumns(digitalID);
    return mapper.toStructure(this.service.updateDigitalID(mapper.toModel(digitalID), id));
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteById(final UUID id) {
    this.getService().deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private void setAuditColumns(final DigitalID digitalID) {
    if (StringUtils.isBlank(digitalID.getCreateUser())) {
      digitalID.setCreateUser(ApplicationProperties.API_NAME);
    }
    if (StringUtils.isBlank(digitalID.getUpdateUser())) {
      digitalID.setUpdateUser(ApplicationProperties.API_NAME);
    }
    digitalID.setCreateDate(LocalDateTime.now().toString());
    digitalID.setUpdateDate(LocalDateTime.now().toString());
  }


}
