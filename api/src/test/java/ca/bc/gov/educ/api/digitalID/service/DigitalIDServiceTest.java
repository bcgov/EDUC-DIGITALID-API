package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.digitalID.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalID.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalID.repository.AccessChannelCodeRepository;
import ca.bc.gov.educ.api.digitalID.repository.IdentityTypeCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Order;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class DigitalIDServiceTest {

    @Autowired
    DigitalIDService service;

    @Autowired
    AccessChannelCodeRepository accessChannelCodeRepository;

    @Autowired
    IdentityTypeCodeRepository identityTypeCodeRepository;

    @Order(1)
    @Test
    public void createDigitalIdTest(){

        Optional<IdentityTypeCodeEntity> identityTypeCodeEntity =  identityTypeCodeRepository.findById("BCSC");
        if(!identityTypeCodeEntity.isPresent())
            throw new EntityNotFoundException(IdentityTypeCodeEntity.class, "identityTypeCodeEntity", "BCSC");

        Optional<AccessChannelCodeEntity> accessChannelCodeEntity =  accessChannelCodeRepository.findById("OSPR");
        if(!accessChannelCodeEntity.isPresent())
            throw new EntityNotFoundException(AccessChannelCodeEntity.class, "accessChannelCode", "OSPR");

        DigitalIDEntity digitalID = new DigitalIDEntity();
        digitalID.setIdentityTypeCode(identityTypeCodeEntity.get());
        digitalID.setIdentityValue("realValue123");
        digitalID.setLastAccessChannelCode(accessChannelCodeEntity.get());
        digitalID.setLastAccessDate(new Date());

        assertNotNull(service.createDigitalID(digitalID));
    }

    @Order(2)
    @Test
    public void searchDigitalIdTest(){
        assertThrows(EntityNotFoundException.class, () -> {
            service.searchDigitalId("bcsc", "fakeValue123");
        });
    }
}
