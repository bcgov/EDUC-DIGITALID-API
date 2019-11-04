package ca.bc.gov.educ.api.digitalID.controller;

import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.service.DigitalIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Digital Identity controller
 *
 * @author John Cox
 */

@RestController
@RequestMapping("digitalid")
public class DigitalIDController {

    @Autowired
    private final DigitalIDService service;
    private final String IDENTITY_CODE_TABLE_BCSC_VALUE = "1";
    private final String IDENTITY_CODE_TABLE_BCEID_VALUE = "2";

    DigitalIDController(DigitalIDService digitalIdentity){
        this.service = digitalIdentity;
    }

    @GetMapping("/bcsc/{id}")
    //@PreAuthorize("#oauth2.hasScope('READ_DIGITALID')")
    public DigitalIDEntity searchDigitalIDByBCSC(@PathVariable String id) throws Exception {
        return service.searchDigitalId(id, IDENTITY_CODE_TABLE_BCSC_VALUE);
    }

    @GetMapping("/bceid/{id}")
   // @PreAuthorize("#oauth2.hasAnyScope('READ_DIGITALID')")
    public DigitalIDEntity searchDigitalIDByBCeID(@PathVariable String id) throws Exception {
        return service.searchDigitalId(id, IDENTITY_CODE_TABLE_BCEID_VALUE);
    }

    @GetMapping("/{id}")
    //@PreAuthorize("#oauth2.hasAnyScope('READ_DIGITALID')")
    public DigitalIDEntity retreiveDigitalID(@PathVariable String id) throws Exception {
        return service.retrieveDigitalID(id);
    }

    @PostMapping()
    //@PreAuthorize("#oauth2.hasAnyScope('WRITE_DIGITALID')")
    public DigitalIDEntity createDigitalID(@Validated @RequestBody DigitalIDEntity digitalID) throws Exception {
        return service.createDigitalID(digitalID);
    }

    @PutMapping()
    //@PreAuthorize("#oauth2.hasAnyScope('WRITE_DIGITALID')")
    public DigitalIDEntity updateDigitalID(@RequestBody DigitalIDEntity digitalID) throws Exception {
        return service.updateDigitalID(digitalID);
    }
}
