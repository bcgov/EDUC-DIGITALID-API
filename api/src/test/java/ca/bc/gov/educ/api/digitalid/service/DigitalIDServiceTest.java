package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

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
  public void testSearchDigitalID_WhenGivenPathParamsMatch_ShouldReturnTheMatchedObject() {
    DigitalIDEntity digitalID = new DigitalIDEntity();
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
    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(LocalDateTime.now());
    UUID id = service.createDigitalID(digitalID).getDigitalID();

    assertNotNull(service.retrieveDigitalID(id));
  }

  @Test
  public void testRetrieveDigitalID_WhenGivenDigitalIDDoesNotExist_ShouldThrowEntityNotFoundException() {
    assertThrows(EntityNotFoundException.class, () -> service.retrieveDigitalID(UUID.fromString("00000000-8000-0000-000e-000000000000")));
  }

  @Test
  public void testUpdateDigitalID_WhenGivenValidPayload_ShouldUpdateTheObject() {

    DigitalIDEntity digitalID = new DigitalIDEntity();
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
    newDigitalID = service.updateDigitalID(newDigitalID);

    assertTrue("newValue123".equalsIgnoreCase(newDigitalID.getIdentityValue()));
  }
}
