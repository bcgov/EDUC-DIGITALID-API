package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.service.v1.DigitalIDService;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DigitalIDServiceTest {

  @Autowired
  private DigitalIDRepository digitalIDRepository;
  @Autowired
  private AccessChannelCodeTableRepository accessChannelCodeRepository;
  @Autowired
  private IdentityTypeCodeTableRepository identityTypeCodeRepository;
  DigitalIDService service;


  @Before
  public void before() {
    service = new DigitalIDService(digitalIDRepository, accessChannelCodeRepository, identityTypeCodeRepository);
  }

  @Test
  public void testCreateDigitalID_WhenGivenValidPayload_ReturnsSavedObject() {
    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(LocalDateTime.now());
    digitalID = service.createDigitalID(digitalID);
    assertNotNull(digitalID);
    assertNotNull(digitalID.getDigitalID());
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatch_ShouldReturnTheMatchedStudentIDObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    var guid = UUID.randomUUID();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setStudentID(guid);
    digitalID.setCreateUser("UNIT-TEST");
    digitalID.setUpdateUser("UNIT-TEST");
    digitalID.setLastAccessDate(LocalDateTime.now());
    service.createDigitalID(digitalID);
    var digitalIDList = service.searchDigitalIds(guid.toString());
    assertEquals(1, digitalIDList.size());
    assertEquals(guid, digitalIDList.get(0).getStudentID());
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatchAndAutoMatchFalse_ShouldReturnTheMatchedStudentIDObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    var guid = UUID.randomUUID();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setStudentID(guid);
    digitalID.setAutoMatched("N");
    digitalID.setCreateUser("UNIT-TEST");
    digitalID.setUpdateUser("UNIT-TEST");
    digitalID.setLastAccessDate(LocalDateTime.now());
    service.createDigitalID(digitalID);
    var digitalIDList = service.searchDigitalIds(guid.toString());
    assertEquals(1, digitalIDList.size());
    assertEquals(guid, digitalIDList.get(0).getStudentID());
    assertEquals("N", digitalIDList.get(0).getAutoMatched());
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatchAndAutoMatchTrue_ShouldReturnTheMatchedStudentIDObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    var guid = UUID.randomUUID();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setStudentID(guid);
    digitalID.setAutoMatched("Y");
    digitalID.setCreateUser("UNIT-TEST");
    digitalID.setUpdateUser("UNIT-TEST");
    digitalID.setLastAccessDate(LocalDateTime.now());
    service.createDigitalID(digitalID);
    var digitalIDList = service.searchDigitalIds(guid.toString());
    assertEquals(1, digitalIDList.size());
    assertEquals(guid, digitalIDList.get(0).getStudentID());
    assertEquals("Y", digitalIDList.get(0).getAutoMatched());
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatch_ShouldReturnEmptyObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    var guid = UUID.randomUUID();
    var digitalIDList = service.searchDigitalIds(guid.toString());
    assertEquals(0, digitalIDList.size() );
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatch_ShouldReturnTheMatchedObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setCreateUser("UNIT-TEST");
    digitalID.setUpdateUser("UNIT-TEST");
    digitalID.setLastAccessDate(LocalDateTime.now());
    service.createDigitalID(digitalID);

    assertNotNull(service.searchDigitalId("BCSC", "realValue123"));
  }

  @Test
  public void testSearchDigitalID_WhenGivenPathParamsDoNotMatch_ShouldThrowEntityNotFoundException() {
    assertThrows(EntityNotFoundException.class, () -> service.searchDigitalId("bcsc", "fakeValue123"));
  }

  @Test
  public void testRetrieveDigitalID_WhenGivenDigitalIDExist_ShouldReturnTheObject() {
    final DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(LocalDateTime.now());
    final UUID id = service.createDigitalID(digitalID).getDigitalID();

    assertNotNull(service.retrieveDigitalID(id));
  }

  @Test
  public void testRetrieveDigitalID_WhenGivenDigitalIDDoesNotExist_ShouldThrowEntityNotFoundException() {
    val guid = UUID.randomUUID();
    assertThrows(EntityNotFoundException.class, () -> service.retrieveDigitalID(guid));
  }

  @Test
  public void testUpdateDigitalID_WhenGivenValidPayload_ShouldUpdateTheObject() {

    final DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(LocalDateTime.now());
    service.createDigitalID(digitalID);

    DigitalIDEntity newDigitalID = new DigitalIDEntity();
    newDigitalID.setDigitalID(digitalID.getDigitalID());
    newDigitalID.setIdentityTypeCode("BCSC");
    newDigitalID.setIdentityValue("newValue123");
    newDigitalID.setLastAccessChannelCode("OSPR");
    newDigitalID.setLastAccessDate(LocalDateTime.now());
    newDigitalID = service.updateDigitalID(newDigitalID, digitalID.getDigitalID());

    assertTrue("newValue123".equalsIgnoreCase(newDigitalID.getIdentityValue()));
  }
}
