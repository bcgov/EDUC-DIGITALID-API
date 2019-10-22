package ca.bc.gov.educ.api.pen.controller.v1.api;

import ca.bc.gov.educ.api.pen.model.DIGITAL_IDENTITY;
import ca.bc.gov.educ.api.pen.props.ApplicationProperties;
import ca.bc.gov.educ.api.pen.service.DigitalIDService;
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

    DigitalIDController(DigitalIDService digitalIdentity){
        this.service = digitalIdentity;
    }

    @GetMapping("/bcsc/{id}")
    public DIGITAL_IDENTITY getDigitalIDByBCSC(@PathVariable String id){
        return service.loadDigitalId(id, ApplicationProperties.IDENTITY_CODE_TABLE_BCSC_VALUE);
    }

    @GetMapping("/bceid/{id}")
    public DIGITAL_IDENTITY getDigitalIDByBCeID(@PathVariable String id){
        return service.loadDigitalId(id, ApplicationProperties.IDENTITY_CODE_TABLE_BCEID_VALUE);
    }
}
