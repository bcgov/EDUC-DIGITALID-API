package ca.bc.gov.educ.api.pen.controller.v1.api;

import ca.bc.gov.educ.api.pen.model.DigitalIdentity;
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
    public DigitalIdentity getDigitalIDByBCSC(@PathVariable String id){
        return service.loadDigitalId(id, "bcsc");
    }

    @GetMapping("/bceid/{id}")
    public String getDigitalIDByBCeID(@PathVariable String id){

        return "Hello there BCeID " + id;
    }
}
