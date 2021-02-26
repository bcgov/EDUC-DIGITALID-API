package ca.bc.gov.educ.api.digitalid.endpoint;

import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.IdentityTypeCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Digital ID.", description = "This CRUD API is for Digital ID related to a student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_DIGITALID", "WRITE_DIGITALID"})})
public interface DigitalIDEndpoint {

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID searchDigitalID(@RequestParam("identitytype") String typeCode, @RequestParam("identityvalue") String typeValue);

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID retrieveDigitalID(@PathVariable String id);

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "BAD REQUEST"), @ApiResponse(responseCode = "201", description = "CREATED.")})
  @ResponseStatus(code = CREATED)
  DigitalID createDigitalID(@Validated @RequestBody DigitalID digitalID);

  @PutMapping
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID updateDigitalID(@Validated @RequestBody DigitalID digitalID);

  @GetMapping("/accessChannelCodes")
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID_CODETABLE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<AccessChannelCode> retrieveAccessChannelCodes();
  
  @GetMapping("/identityTypeCodes")
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID_CODETABLE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<IdentityTypeCode> retrieveIdentityTypeCodes();

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_DELETE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "NO CONTENT"),  @ApiResponse(responseCode = "404", description = "NOT FOUND."), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> deleteById(@PathVariable UUID id);

}
