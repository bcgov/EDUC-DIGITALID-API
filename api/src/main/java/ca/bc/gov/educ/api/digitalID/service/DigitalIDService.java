package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.digitalID.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalID.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.props.ApplicationProperties;
import ca.bc.gov.educ.api.digitalID.repository.DigitalIDRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * DigitalIDService
 *
 * @author John Cox
 */

@Service
public class DigitalIDService {
    private static final Log logger = LogFactory.getLog(DigitalIDService.class);

    @Autowired
    private DigitalIDRepository digitalIDRepository;

    /**
     * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
     *
     * @param typeValue
     * @param typeCode
     * @throws EntityNotFoundException
     * @return
     */
    public DigitalIDEntity searchDigitalId(String typeCode, String typeValue) throws EntityNotFoundException{

        Optional<DigitalIDEntity> result =  digitalIDRepository.findByIdentityTypeCodeAndIdentityValue(typeCode.toUpperCase(), typeValue);
        if(result.isPresent()) {
            return result.get();
        } else {
            throw new EntityNotFoundException(DigitalIDEntity.class, "identityTypeCode", typeCode, "identityTypeValue", typeValue);
        }
    }

    /**
     * Search for DigitalIDEntity by digital id
     *
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    public DigitalIDEntity retrieveDigitalID(Long id) throws EntityNotFoundException {
        Optional<DigitalIDEntity> result =  digitalIDRepository.findById(id);
        if(result.isPresent()) {
            return result.get();
        } else {
            throw new EntityNotFoundException(DigitalIDEntity.class, "digitalID", id.toString());
        }
    }

    /**
     * Creates a DigitalIDEntity
     *
     * @param digitalID
     * @return
     * @throws EntityNotFoundException
     * @throws InvalidParameterException
     */
    public DigitalIDEntity createDigitalID(DigitalIDEntity digitalID) throws EntityNotFoundException, InvalidParameterException {

        validateCreateParameters(digitalID);

        if(digitalID.getDigitalID()!=null){
            throw new InvalidParameterException("digitalID");
        }
        digitalID.setUpdateDate(new Date());
        digitalID.setCreateDate(new Date());

        return digitalIDRepository.save(digitalID);
    }

    /**
     * Updates a DigitalIDEntity
     *
     * @param digitalID
     * @return
     * @throws Exception
     */
    public DigitalIDEntity updateDigitalID(DigitalIDEntity digitalID) throws EntityNotFoundException, InvalidParameterException {

        validateCreateParameters(digitalID);


        Optional<DigitalIDEntity> curDigitalID = digitalIDRepository.findById(digitalID.getDigitalID());

        if(curDigitalID.isPresent())
        {
            DigitalIDEntity newDigitalID = curDigitalID.get();
            newDigitalID.setStudentID(digitalID.getStudentID());
            newDigitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode());
            newDigitalID.setIdentityValue(digitalID.getIdentityValue());
            newDigitalID.setLastAccessDate(digitalID.getLastAccessDate());
            newDigitalID.setLastAccessChannelCode(digitalID.getLastAccessChannelCode());
            newDigitalID.setUpdateUser(ApplicationProperties.CLIENT_ID);
            newDigitalID.setUpdateDate(new Date());
            newDigitalID = digitalIDRepository.save(newDigitalID);

            return newDigitalID;
        } else {
            throw new EntityNotFoundException(DigitalIDEntity.class, "digitalID", digitalID.getDigitalID().toString());
        }
    }

    private void validateCreateParameters(DigitalIDEntity digitalIDEntity) throws InvalidParameterException {
        if(digitalIDEntity.getCreateDate()!=null)
            throw new InvalidParameterException("createDate");
        if(digitalIDEntity.getUpdateDate()!=null)
            throw new InvalidParameterException("updateDate");
    }
}
