package ca.bc.gov.educ.api.pen.controller;

import ca.bc.gov.educ.api.pen.model.DigitalIdentitityDAO;
import ca.bc.gov.educ.api.pen.props.ApplicationProperties;
import ca.bc.gov.educ.api.pen.service.DigitalIDService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Digital Identity controller
 *
 * @author John Cox
 */

@RestController
@RequestMapping("digitalid")
public class DigitalIDController {

    private final DigitalIDService service;
    private final String IDENTITY_CODE_TABLE_BCSC_VALUE = "1";
    private final String IDENTITY_CODE_TABLE_BCEID_VALUE = "2";

    DigitalIDController(DigitalIDService digitalIdentity){
        this.service = digitalIdentity;
    }

    @GetMapping("/bcsc/{id}")
    @PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIdentitityDAO getDigitalIDByBCSC(@PathVariable String id){
        return service.loadDigitalId(id, IDENTITY_CODE_TABLE_BCSC_VALUE);
    }

    @GetMapping("/bceid/{id}")
    @PreAuthorize("#oauth2.hasAnyScope('READ')")
    public DigitalIdentitityDAO getDigitalIDByBCeID(@PathVariable String id){
        return service.loadDigitalId(id, IDENTITY_CODE_TABLE_BCEID_VALUE);
    }
}
