package ca.bc.gov.educ.api.digitalid.controller;

import ca.bc.gov.educ.api.digitalid.endpoint.DigitalIDEndpoint;
import ca.bc.gov.educ.api.digitalid.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.digitalid.exception.errors.ApiError;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.IdentityTypeCode;
import ca.bc.gov.educ.api.digitalid.validator.DigitalIDPayloadValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
@EnableResourceServer
@Slf4j
@SuppressWarnings("squid:ModifiersOrderCheck")
public class DigitalIDController implements DigitalIDEndpoint {
  private final static DigitalIDMapper mapper = DigitalIDMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDService service;

  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDPayloadValidator payloadValidator;


  @Autowired
  public DigitalIDController(final DigitalIDService digitalIDService, final DigitalIDPayloadValidator payloadValidator) {
    this.payloadValidator = payloadValidator;
    this.service = digitalIDService;
  }

  public DigitalID searchDigitalID(@RequestParam(value = "identitytype", required = false) String typeCode, @RequestParam("identityvalue") String typeValue) {
    return mapper.toStructure(service.searchDigitalId(typeCode, typeValue));
  }

  public DigitalID retrieveDigitalID(@PathVariable String id) {
    return mapper.toStructure(service.retrieveDigitalID(UUID.fromString(id)));
  }

  public List<AccessChannelCode> retrieveAccessChannelCodes() {
    return service.getAccessChannelCodesList().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  public List<IdentityTypeCode> retrieveIdentityTypeCodes() {
    return service.getIdentityTypeCodesList().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  public DigitalID createDigitalID(@Validated @RequestBody DigitalID digitalID) {
    val validationResult = getPayloadValidator().validatePayload(digitalID, true);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
    setAuditColumns(digitalID);
    return mapper.toStructure(service.createDigitalID(mapper.toModel(digitalID)));
  }

  public DigitalID updateDigitalID(@Validated @RequestBody DigitalID digitalID) {
    val validationResult = getPayloadValidator().validatePayload(digitalID, false);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
    setAuditColumns(digitalID);
    return mapper.toStructure(service.updateDigitalID(mapper.toModel(digitalID)));
  }

  @Override
  public String health() {
    return "OK";
  }

  private void setAuditColumns(DigitalID digitalID) {
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
