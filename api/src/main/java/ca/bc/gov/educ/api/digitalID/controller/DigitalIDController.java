package ca.bc.gov.educ.api.digitalID.controller;

import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.service.DigitalIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    //@PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIDEntity searchDigitalIDByBCSC(@PathVariable String id){
        return service.searchDigitalId(id, IDENTITY_CODE_TABLE_BCSC_VALUE);
    }

    @GetMapping("/bceid/{id}")
    //@PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIDEntity searchDigitalIDByBCeID(@PathVariable String id){
        return service.searchDigitalId(id, IDENTITY_CODE_TABLE_BCEID_VALUE);
    }

    @GetMapping("/{id}")
    //@PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIDEntity retreiveDigitalID(@PathVariable String id){
        return service.retrieveDigitalID(id);
    }

    @PostMapping()
    //@PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIDEntity createDigitalID(@RequestBody DigitalIDEntity digitalID){
        return service.createDigitalID(digitalID);
    }
}
