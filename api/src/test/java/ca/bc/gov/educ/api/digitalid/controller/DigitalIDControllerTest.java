package ca.bc.gov.educ.api.digitalid.controller;

import ca.bc.gov.educ.api.digitalid.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.support.WithMockOAuth2Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DigitalIDControllerTest {
  @Autowired
  AccessChannelCodeTableRepository accessChannelCodeTableRepository;

  @Autowired
  IdentityTypeCodeTableRepository identityTypeCodeTableRepository;

  @Autowired
  DigitalIDRepository repository;

  private MockMvc mockMvc;

  @Autowired
  DigitalIDService service;

  @Autowired
  DigitalIDController controller;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
    identityTypeCodeTableRepository.save(createIdentityTypeCodeData());
    accessChannelCodeTableRepository.save(createAccessChannelCodeData());
  }

  /**
   * need to delete the records to make it working in unit tests assertion, else the records will keep growing and assertions will fail.
   */
  @After
  public void after() {
    identityTypeCodeTableRepository.deleteAll();
    accessChannelCodeTableRepository.deleteAll();
    repository.deleteAll();
  }

  private AccessChannelCodeEntity createAccessChannelCodeData() {
    return AccessChannelCodeEntity.builder().accessChannelCode("AC").description("Access Code")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }

  private IdentityTypeCodeEntity createIdentityTypeCodeData() {
    return IdentityTypeCodeEntity.builder().identityTypeCode("BCSC").description("BC Services Card")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }


  @Test
  @WithMockOAuth2Scope(scope = "READ_DIGITALID")
  public void testRetrieveDigitalId_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID())).andDo(print()).andExpect(status().isNotFound());
  }


  @Test
  @WithMockOAuth2Scope(scope = "READ_DIGITALID")
  public void testRetrieveDigitalId_GivenValidID_ShouldReturnOK() throws Exception {
    DigitalIDEntity entity = service.createDigitalID(createDigitalIDMockData());
    this.mockMvc.perform(get("/" + entity.getDigitalID())).andDo(print()).andExpect(status().isOk());
    repository.deleteById(entity.getDigitalID());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DIGITALID_CODETABLE")
  public void testRetrieveIdentityTypeCodes_GivenARecordInDB_ShouldReturnOK() throws Exception {
    this.mockMvc.perform(get("/identityTypeCodes")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DIGITALID_CODETABLE")
  public void testRetrieveAccessChannelCodes_GivenARecordInDB_ShouldReturnOK() throws Exception {
    this.mockMvc.perform(get("/accessChannelCodes")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenDigitalIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(UUID.randomUUID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenInvalidLACInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC1")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenInvalidLITCInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC1").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenITCIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode(null).lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenITVIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue(null).lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenLADFormatWrong_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate("2020-01-0119:40:09").build()))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenLADInFuture_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate("2199-01-01T19:40:09").build()))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenLACIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode(null)
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testCreateDigitalId_GivenLADIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(null).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testUpdateDigitalId_GivenRandomDigitalIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(UUID.randomUUID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testUpdateDigitalId_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    DigitalIDEntity entity = service.createDigitalID(createDigitalIDMockData());
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(entity.getDigitalID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.identityValue").value("TEST"));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DIGITALID")
  public void testUpdateDigitalId_GivenInvalidLITCInPayload_ShouldReturnStatusBadRequest() throws Exception {
    DigitalIDEntity entity = service.createDigitalID(createDigitalIDMockData());
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(entity.getDigitalID().toString()).identityTypeCode("BCSC1").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }


  @Test
  @WithMockOAuth2Scope(scope = "READ_DIGITALID")
  public void testSearchDigitalId_GivenIdentityTypeAndIdentityValueExistInDB_ShouldReturnStatusOK() throws Exception {
    DigitalIDEntity entity = service.createDigitalID(createDigitalIDMockData());
    this.mockMvc.perform(get("/").param("identitytype", entity.getIdentityTypeCode()).param("identityvalue", entity.getIdentityValue()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.identityValue").value("123"));
  }


  private DigitalIDEntity createDigitalIDMockData() {
    return DigitalIDEntity.builder().identityTypeCode("BCSC").identityValue("123").lastAccessChannelCode("ABC").createUser("TEST").lastAccessDate(LocalDateTime.now()).updateUser("TEST").build();
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
