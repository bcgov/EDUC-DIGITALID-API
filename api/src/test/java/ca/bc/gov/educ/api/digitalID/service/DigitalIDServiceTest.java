package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.digitalID.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalID.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        digitalID.setDigitalID(new Long(111));
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());

        assertThrows(InvalidParameterException.class, () -> {
            service.createDigitalID(digitalID);
        });
    }

    @Test
    public void createInvalidDigitalIdTest2(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());
        digitalID.setCreateUser("USER");

        assertThrows(InvalidParameterException.class, () -> {
            service.createDigitalID(digitalID);
        });
    }

    @Test
    public void createInvalidDigitalIdTest3(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("FAKECODE");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
        digitalID.setLastAccessDate(new Date());

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.createDigitalID(digitalID);
        });
    }

    @Test
    public void searchValidDigitalIdTest(){
        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode("BCSC");
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode("OSPR");
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
        Long id = service.createDigitalID(digitalID).getDigitalID();

        assertNotNull(service.retrieveDigitalID(id));
    }

    @Test
    public void retrieveInvalidDigitalIdTest(){
        assertThrows(EntityNotFoundException.class, () -> {
            service.retrieveDigitalID(new Long(111));
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
