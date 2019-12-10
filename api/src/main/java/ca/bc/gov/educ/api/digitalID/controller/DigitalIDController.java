package ca.bc.gov.educ.api.digitalID.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.service.DigitalIDService;

import java.util.UUID;

/**
 * Digital Identity controller
 *
 * @author John Cox
 */

@RestController
@RequestMapping("/")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class DigitalIDController {

    @Autowired
    private final DigitalIDService service;

    DigitalIDController(DigitalIDService digitalIdentity){
        this.service = digitalIdentity;
    }

    @GetMapping("/{typeCode}/{typeValue}")
    @PreAuthorize("#oauth2.hasScope('READ_DIGITALID')")
    public DigitalIDEntity searchDigitalID(@PathVariable String typeCode, @PathVariable String typeValue) throws Exception {
        return service.searchDigitalId(typeCode, typeValue);
    }

    @GetMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('READ_DIGITALID')")
    public DigitalIDEntity retreiveDigitalID(@PathVariable UUID id) throws Exception {
        return service.retrieveDigitalID(id);
    }

    @PostMapping()
    @PreAuthorize("#oauth2.hasScope('WRITE_DIGITALID')")
    public DigitalIDEntity createDigitalID(@Validated @RequestBody DigitalIDEntity digitalID) throws Exception {
        return service.createDigitalID(digitalID);
    }

    @PutMapping()
    @PreAuthorize("#oauth2.hasScope('WRITE_DIGITALID')")
    public DigitalIDEntity updateDigitalID(@Validated @RequestBody DigitalIDEntity digitalID) throws Exception {
        return service.updateDigitalID(digitalID);
    }
}
