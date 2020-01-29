package ca.bc.gov.educ.api.digitalid.endpoint;

import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Digital ID.", description = "This CRUD API is for Digital ID related to a student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_DIGITALID", "WRITE_DIGITALID"})})
public interface DigitalIDEndpoint {

  @GetMapping("/")
  @PreAuthorize("#oauth2.hasScope('READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID searchDigitalID(@RequestParam("identitytype") String typeCode, @RequestParam("identityvalue") String typeValue);

  @GetMapping("/{id}")
  @PreAuthorize("#oauth2.hasScope('READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID retrieveDigitalID(@PathVariable String id);

  @PostMapping()
  @PreAuthorize("#oauth2.hasScope('WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "201", description = "CREATED.")})
  DigitalID createDigitalID(@Validated @RequestBody DigitalID digitalID);

  @PutMapping()
  @PreAuthorize("#oauth2.hasScope('WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID updateDigitalID(@Validated @RequestBody DigitalID digitalID);
}