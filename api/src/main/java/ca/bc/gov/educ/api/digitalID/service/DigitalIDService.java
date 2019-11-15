package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.digitalID.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.digitalID.exception.InvalidParameterException;
import ca.bc.gov.educ.api.digitalID.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalID.props.ApplicationProperties;
import ca.bc.gov.educ.api.digitalID.repository.AccessChannelCodeRepository;
import ca.bc.gov.educ.api.digitalID.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalID.repository.IdentityTypeCodeRepository;
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

    @Autowired
    private AccessChannelCodeRepository accessChannelCodeRepository;

    @Autowired
    private IdentityTypeCodeRepository identityTypeCodeRepository;

    /**
     * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
     *
     * @param typeValue
     * @param typeCode
     * @throws EntityNotFoundException
     * @return
     */
    public DigitalIDEntity searchDigitalId(String typeCode, String typeValue) throws EntityNotFoundException{

        Optional<IdentityTypeCodeEntity> identityTypeCode = identityTypeCodeRepository.findById(typeCode.toUpperCase());
        if(!identityTypeCode.isPresent())
            throw new InvalidParameterException(typeCode);


        Optional<DigitalIDEntity> result =  digitalIDRepository.findByIdentityTypeCodeAndIdentityValue(identityTypeCode.get(), typeValue);
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

        validateParameters(digitalID);

        if(digitalID.getDigitalID()!=null){
            throw new InvalidParameterException("digitalID");
        }
        digitalID.setUpdateUser(ApplicationProperties.CLIENT_ID);
        digitalID.setUpdateDate(new Date());
        digitalID.setCreateUser(ApplicationProperties.CLIENT_ID);
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
    public DigitalIDEntity updateDigitalID(DigitalIDEntity digitalID) throws EntityNotFoundException {

        validateParameters(digitalID);

        Optional<DigitalIDEntity> curDigitalID = digitalIDRepository.findById(digitalID.getDigitalID());

        if(curDigitalID.isPresent())
        {
            DigitalIDEntity newDigitalID = curDigitalID.get();
            newDigitalID.setStudentID(digitalID.getStudentID());
            newDigitalID.setIdentityTypeCode(digitalID.getIdentityTypeCode());
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

    private void validateParameters(DigitalIDEntity digitalIDEntity) throws InvalidParameterException {

        String typeCode = digitalIDEntity.getIdentityTypeCode().getIdentityTypeCode().toUpperCase();
        String accessChannelCode = digitalIDEntity.getLastAccessChannelCode().getAccessChannelCode().toUpperCase();
        Optional<IdentityTypeCodeEntity> identityTypeCodeEntity = identityTypeCodeRepository.findById(typeCode);
        if(identityTypeCodeEntity.isPresent()){
            digitalIDEntity.setIdentityTypeCode(identityTypeCodeEntity.get());
        } else
            throw new EntityNotFoundException(IdentityTypeCodeEntity.class, "identityTypeCode", typeCode);

        Optional<AccessChannelCodeEntity> accessChannelCodeEntity = accessChannelCodeRepository.findById(accessChannelCode.toUpperCase());
        if(accessChannelCodeEntity.isPresent()){
            digitalIDEntity.setLastAccessChannelCode(accessChannelCodeEntity.get());
        } else
            throw new EntityNotFoundException(AccessChannelCodeEntity.class, "accessChannelCode", accessChannelCode);

        if(digitalIDEntity.getCreateDate()!=null)
            throw new InvalidParameterException("createDate");
        if(digitalIDEntity.getCreateUser()!=null)
            throw new InvalidParameterException("createUser");
        if(digitalIDEntity.getUpdateDate()!=null)
            throw new InvalidParameterException("updateDate");
        if(digitalIDEntity.getUpdateUser()!=null)
            throw new InvalidParameterException("updateUser");
    }
}
