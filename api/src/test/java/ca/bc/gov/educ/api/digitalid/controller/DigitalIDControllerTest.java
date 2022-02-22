package ca.bc.gov.educ.api.digitalid.controller;

import ca.bc.gov.educ.api.digitalid.controller.v1.DigitalIDController;
import ca.bc.gov.educ.api.digitalid.model.v1.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.service.v1.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.digitalid.constants.v1.URL.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DigitalIDControllerTest {
  @Autowired
  AccessChannelCodeTableRepository accessChannelCodeTableRepository;

  @Autowired
  IdentityTypeCodeTableRepository identityTypeCodeTableRepository;

  @Autowired
  DigitalIDRepository repository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  DigitalIDService service;

  @Autowired
  DigitalIDController controller;

  @Before
  public void setUp() {
    this.identityTypeCodeTableRepository.save(this.createIdentityTypeCodeData());
    this.accessChannelCodeTableRepository.save(this.createAccessChannelCodeData());
  }

  /**
   * need to delete the records to make it working in unit tests assertion, else the records will keep growing and assertions will fail.
   */
  @After
  public void after() {
    this.identityTypeCodeTableRepository.deleteAll();
    this.accessChannelCodeTableRepository.deleteAll();
    this.repository.deleteAll();
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
  public void testRetrieveDigitalId_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get(BASE_URL + UUID.randomUUID()).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID")))).andDo(print()).andExpect(status().isNotFound());
  }


  @Test
  public void testRetrieveDigitalId_GivenValidID_ShouldReturnOK() throws Exception {
    final DigitalIDEntity entity = this.service.createDigitalID(this.createDigitalIDMockData());
    this.mockMvc.perform(get(BASE_URL +"/"+ entity.getDigitalID()).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID")))).andDo(print()).andExpect(status().isOk());
    this.repository.deleteById(entity.getDigitalID());
  }

  @Test
  public void testRetrieveIdentityTypeCodes_GivenARecordInDB_ShouldReturnOK() throws Exception {
    this.mockMvc.perform(get(BASE_URL+IDENTITY_TYPE_CODES).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID_CODETABLE")))).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void testRetrieveAccessChannelCodes_GivenARecordInDB_ShouldReturnOK() throws Exception {
    this.mockMvc.perform(get(BASE_URL+ACCESS_CHANNEL_CODES).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID_CODETABLE")))).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void testCreateDigitalId_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreateDigitalIdWithAutoMatch_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    var date = LocalDateTime.now().toString();
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC").autoMatchedDate(date)
        .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.autoMatchedDate").isNotEmpty());
  }

  @Test
  public void testCreateDigitalIdWithAutoMatchFalse_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC").autoMatchedDate(null)
        .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.autoMatchedDate").isEmpty());
  }

  @Test
  public void testCreateDigitalIdWithNoAutoMatch_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
        .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.autoMatchedDate").isEmpty());
  }

  @Test
  public void testCreateDigitalId_GivenDigitalIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(UUID.randomUUID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateDigitalId_GivenInvalidLACInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC1")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateDigitalId_GivenInvalidLITCInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC1").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateDigitalId_GivenITCIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode(null).lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateDigitalId_GivenITVIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALIDE"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue(null).lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  public void testCreateDigitalId_GivenLADFormatWrong_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate("2020-01-0119:40:09").build()))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  public void testCreateDigitalId_GivenLACIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode(null)
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateDigitalId_GivenLADIsNull_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(null).build()))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateDigitalId_GivenRandomDigitalIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put(BASE_URL+"/"+UUID.randomUUID()).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(UUID.randomUUID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateDigitalId_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    final DigitalIDEntity entity = this.service.createDigitalID(this.createDigitalIDMockData());
    this.mockMvc.perform(put(BASE_URL+"/"+entity.getDigitalID()).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(entity.getDigitalID().toString()).identityTypeCode("BCSC").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.identityValue").value("TEST"));
  }

  @Test
  public void testUpdateDigitalId_GivenInvalidLITCInPayload_ShouldReturnStatusBadRequest() throws Exception {
    final DigitalIDEntity entity = this.service.createDigitalID(this.createDigitalIDMockData());
    this.mockMvc.perform(put(BASE_URL+"/"+entity.getDigitalID()).with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(asJsonString(DigitalID.builder().digitalID(entity.getDigitalID().toString()).identityTypeCode("BCSC1").lastAccessChannelCode("AC")
                    .createUser("TEST").updateUser("TEST").identityValue("Test1").lastAccessDate(LocalDateTime.now().toString()).build()))).andDo(print()).andExpect(status().isBadRequest());
  }


  @Test
  public void testSearchDigitalId_GivenIdentityTypeAndIdentityValueExistInDB_ShouldReturnStatusOK() throws Exception {
    final DigitalIDEntity entity = this.service.createDigitalID(this.createDigitalIDMockData());
    this.mockMvc.perform(get(BASE_URL).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID"))).param("identitytype", entity.getIdentityTypeCode()).param("identityvalue", entity.getIdentityValue()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.identityValue").value("123"));
  }

  @Test
  public void testSearchDigitalId_GivenStudentIDValueExistInDB_ShouldReturnStatusOK() throws Exception {
    var digitalID = this.createDigitalIDMockData();
    var randGUID = UUID.randomUUID();
    digitalID.setStudentID(randGUID);
    final DigitalIDEntity entity = this.service.createDigitalID(digitalID);
    this.mockMvc.perform(get(BASE_URL + LIST).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID"))).param("studentID", entity.getStudentID().toString()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
  }

  @Test
  public void testSearchDigitalId_GivenStudentIDValueErrorInDB_ShouldReturnStatus400() throws Exception {
    var randGUID = UUID.randomUUID();
    this.mockMvc.perform(get(BASE_URL + LIST).with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DIGITALID"))).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isBadRequest());
  }

  private DigitalIDEntity createDigitalIDMockData() {
    return DigitalIDEntity.builder().identityTypeCode("BCSC").identityValue("123").lastAccessChannelCode("ABC").createUser("TEST").lastAccessDate(LocalDateTime.now()).updateUser("TEST").build();
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
