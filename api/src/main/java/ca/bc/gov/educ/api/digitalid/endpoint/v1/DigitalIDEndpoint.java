package ca.bc.gov.educ.api.digitalid.endpoint.v1;

import ca.bc.gov.educ.api.digitalid.constants.v1.URL;
import ca.bc.gov.educ.api.digitalid.struct.v1.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.v1.IdentityTypeCode;
import ca.bc.gov.educ.api.digitalid.struct.v1.TenantAccess;
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

@RequestMapping(URL.BASE_URL)
@OpenAPIDefinition(info = @Info(title = "API for Digital ID.", description = "This CRUD API is for Digital ID related to a student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_DIGITALID", "WRITE_DIGITALID"})})
public interface DigitalIDEndpoint {

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID searchDigitalID(@RequestParam("identitytype") String typeCode, @RequestParam("identityvalue") String typeValue);

  @GetMapping(URL.LIST)
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  List<DigitalID> searchDigitalIDs(@RequestParam(name = "studentID", required = false) String studentID);

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID retrieveDigitalID(@PathVariable String id);

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_DELETE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "404", description = "NOT FOUND."), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> deleteById(@PathVariable UUID id);

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  DigitalID updateDigitalID(@Validated @RequestBody DigitalID digitalID, @PathVariable UUID id);

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DIGITALID')")
  @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "BAD REQUEST"), @ApiResponse(responseCode = "201", description = "CREATED.")})
  @ResponseStatus(code = CREATED)
  DigitalID createDigitalID(@Validated @RequestBody DigitalID digitalID);


  @GetMapping(URL.ACCESS_CHANNEL_CODES)
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID_CODETABLE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<AccessChannelCode> retrieveAccessChannelCodes();

  @GetMapping(URL.IDENTITY_TYPE_CODES)
  @PreAuthorize("hasAuthority('SCOPE_READ_DIGITALID_CODETABLE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<IdentityTypeCode> retrieveIdentityTypeCodes();

  @GetMapping(URL.TENANT_ACCESS)
  @PreAuthorize("hasAuthority('SCOPE_READ_TENANT_ACCESS')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND.")})
  TenantAccess determineTenantAccess(@RequestParam("clientID") String clientID, @RequestParam("tenantID") String tenantID);

}
