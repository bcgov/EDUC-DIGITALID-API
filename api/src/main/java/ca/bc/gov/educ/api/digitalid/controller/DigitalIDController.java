package ca.bc.gov.educ.api.digitalid.controller;

import ca.bc.gov.educ.api.digitalid.endpoint.DigitalIDEndpoint;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDEntityMapper;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Digital Identity controller
 *
 * @author John Cox
 */

@RestController
@EnableResourceServer
@Slf4j
public class DigitalIDController implements DigitalIDEndpoint {

  @Getter(AccessLevel.PRIVATE)
  private final DigitalIDService service;
  final DigitalIDEntityMapper mapper = DigitalIDEntityMapper.mapper;

  DigitalIDController(@Autowired final DigitalIDService digitalIDService) {
    this.service = digitalIDService;
  }

  public DigitalID searchDigitalID(@RequestParam(value = "identitytype", required = false) String typeCode, @RequestParam("identityvalue") String typeValue) {
    return mapper.toStructure(service.searchDigitalId(typeCode, typeValue));
  }

  public DigitalID retrieveDigitalID(@PathVariable String id) {
    return mapper.toStructure(service.retrieveDigitalID(UUID.fromString(id)));
  }

  public DigitalID createDigitalID(@Validated @RequestBody DigitalID digitalID) {
    return mapper.toStructure(service.createDigitalID(mapper.toModel(digitalID)));
  }

  public DigitalID updateDigitalID(@Validated @RequestBody DigitalID digitalID) {
    return mapper.toStructure(service.updateDigitalID(mapper.toModel(digitalID)));
  }

  @Override
  public String health() {
    log.info("Health Check OK, returning OK");
    return "OK";
  }
}
