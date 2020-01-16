package ca.bc.gov.educ.api.digitalid.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import ca.bc.gov.educ.api.digitalid.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalid.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;

@SpringBootTest
@Transactional
public class DigitalIDServiceTest {

    @Autowired
    DigitalIDService service;

    @Test
    public void createValidDigitalIdTest(){

        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());

        assertNotNull(service.createDigitalID(digitalID));
    }

    @Test
    public void createInvalidDigitalIdTest1(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setDigitalID(UUID.fromString("00000000-8000-0000-000e-000000000000"));
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());

        assertThrows(InvalidParameterException.class, () -> {
            service.createDigitalID(digitalID);
        });
    }

    @Test
    public void createInvalidDigitalIdTest3(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("FAKECODE");
        digitalID.setIdentityValue("REALVALUE123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setCreateUser("UNIT-TEST");
        digitalID.setUpdateUser("UNIT-TEST");
        digitalID.setLastAccessDate(new Date());

        service.createDigitalID(digitalID);
        assertThrows(DataIntegrityViolationException.class, () -> {
            assertNotNull(service.searchDigitalId("FAKECODE", "REALVALUE123"));
        });
    }

    @Test
    public void searchValidDigitalIdTest(){
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
    public void searchInvalidDigitalIdTest(){
        assertThrows(EntityNotFoundException.class, () -> {
            service.searchDigitalId("bcsc", "fakeValue123");
        });
    }

    @Test
    public void retrieveValidDigitalIdTest(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());
        UUID id = service.createDigitalID(digitalID).getDigitalID();

        assertNotNull(service.retrieveDigitalID(id));
    }

    @Test
    public void retrieveInvalidDigitalIdTest(){
        assertThrows(EntityNotFoundException.class, () -> {
            service.retrieveDigitalID(UUID.fromString("00000000-8000-0000-000e-000000000000"));
        });
    }

    @Test
    public void updateValidDigitalIdTest(){

        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());
        service.createDigitalID(digitalID).getDigitalID();

        DigitalIDEntity newDigitalID = new DigitalIDEntity();
        newDigitalID.setDigitalID(digitalID.getDigitalID());
        newDigitalID.setIdentityTypeCode("BCSC");
        newDigitalID.setIdentityValue("newValue123");
        newDigitalID.setLastAccessChannelCode("OSPR");
        newDigitalID.setLastAccessDate(new Date());



        assertNotNull(service.updateDigitalID(newDigitalID));
    }
}
