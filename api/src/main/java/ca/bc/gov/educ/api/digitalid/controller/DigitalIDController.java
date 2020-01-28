package ca.bc.gov.educ.api.digitalid.controller;

import ca.bc.gov.educ.api.digitalid.endpoint.DigitalIDEndpoint;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDEntityMapper;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DigitalIDController implements DigitalIDEndpoint {
    private final DigitalIDService service;
    final DigitalIDEntityMapper mapper = DigitalIDEntityMapper.mapper;

    DigitalIDController(@Autowired final DigitalIDService digitalIDService) {
        this.service = digitalIDService;
    }

    public DigitalID searchDigitalID(@RequestParam("identitytype") String typeCode, @RequestParam("identityvalue") String typeValue) {
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
}
