package ca.bc.gov.educ.api.digitalid.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.repository.AccessChannelCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.IdentityTypeCodeTableRepository;
import ca.bc.gov.educ.api.digitalid.rest.RestUtils;

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
  @Mock
  RestTemplate template;
  @Mock
  RestUtils restUtils;
  @Mock
  ApplicationProperties applicationProperties;

  @Before
  public void before() {
    service = new DigitalIDService(digitalIDRepository, accessChannelCodeRepository, identityTypeCodeRepository, restUtils, applicationProperties);
  }

  @Test
  public void testCreateDigitalID_WhenGivenValidPayload_ReturnsSavedObject() {

    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(new Date());
    digitalID = service.createDigitalID(digitalID);
    assertNotNull(digitalID);
    assertNotNull(digitalID.getDigitalID());
    assertNotNull(digitalID.getCreateDate());
  }

  @Test
  public void testCreateDigitalID_WhenGivenDigitalIDInPayload_ThrowsInvalidParameterException() {
    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setDigitalID(UUID.fromString("00000000-8000-0000-000e-000000000000"));
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(new Date());

    assertThrows(InvalidParameterException.class, () -> service.createDigitalID(digitalID));
  }


  @Test
  public void testSearchDigitalID_WhenGivenPathParamsMatch_ShouldReturnTheMatchedObject() {
    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("REALVALUE123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setCreateUser("UNIT-TEST");
    digitalID.setUpdateUser("UNIT-TEST");
    digitalID.setLastAccessDate(new Date());
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
    digitalID.setLastAccessDate(new Date());
    UUID id = service.createDigitalID(digitalID).getDigitalID();

    assertNotNull(service.retrieveDigitalID(id));
  }

  @Test
  public void testRetrieveDigitalID_WhenGivenDigitalIDDoesNotExist_ShouldThrowEntityNotFoundException() {
    assertThrows(EntityNotFoundException.class, () -> {
      service.retrieveDigitalID(UUID.fromString("00000000-8000-0000-000e-000000000000"));
    });
  }

  @Test
  public void testUpdateDigitalID_WhenGivenValidPayload_ShouldUpdateTheObject() {

    DigitalIDEntity digitalID = new DigitalIDEntity();
    digitalID.setIdentityTypeCode("BCSC");
    digitalID.setIdentityValue("realValue123");
    digitalID.setLastAccessChannelCode("OSPR");
    digitalID.setLastAccessDate(new Date());
    service.createDigitalID(digitalID);

    DigitalIDEntity newDigitalID = new DigitalIDEntity();
    newDigitalID.setDigitalID(digitalID.getDigitalID());
    newDigitalID.setIdentityTypeCode("BCSC");
    newDigitalID.setIdentityValue("newValue123");
    newDigitalID.setLastAccessChannelCode("OSPR");
    newDigitalID.setLastAccessDate(new Date());
    newDigitalID = service.updateDigitalID(newDigitalID);

    assertNotNull(newDigitalID.getUpdateDate());
    assertTrue("newValue123".equalsIgnoreCase(newDigitalID.getIdentityValue()));
  }
}
