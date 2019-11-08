package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.digitalID.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalID.props.ApplicationProperties;
import ca.bc.gov.educ.ords.exception.NoOrdsResultsFoundException;
import ca.bc.gov.educ.ords.exception.ORDSQueryException;
import ca.bc.gov.educ.ords.model.ORDSTargetCredential;
import ca.bc.gov.educ.ords.service.ORDSService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * @author John Cox
 */

@Service
public class DigitalIDService {
    private static final Log logger = LogFactory.getLog(DigitalIDService.class);

    private final String UPDATE_FIELDS = "student_id, identity_type_code, identity_value, last_access_time, last_access_channel_code, update_user, update_date";
    private final String CREATE_FIELDS = UPDATE_FIELDS + ", create_user, create_date";
    private final String SEARCH_DIGITALID_QUERY = "select * from digital_identity where lower(identity_value) = lower(?) and lower(identity_type_code) = lower(?);";
    private final String RETRIEVE_DIGITALID_QUERY = "select * from digital_identity where lower(digital_identity_id) = lower(?);";
    private final String INSERT_DIGITALID_QUERY = "insert into digital_identity (" + CREATE_FIELDS + ") values (?,?,?,?,?,?,?,?,?)";
    private final String UPDATE_DIGITALID_QUERY = "update digital_identity " + "set " + UPDATE_FIELDS.replaceAll(", ", "=?, ") + "=? where digital_identity_id = ?";

    @Autowired
    private ObjectMapper objectMapper;

    ORDSTargetCredential targetCredential;

    @Autowired
    public DigitalIDService (@Value("${ords.username}") String ordsUsername, @Value("${ords.password}") String ordsPassword,  @Value("${ords.url}") String ordsUrl) {
        targetCredential = new ORDSTargetCredential(ordsUsername,
                ordsPassword, ordsUrl);
    }
    /**
     * Search for DigitalIDEntity by identity value and identity type code (BCeID or BCSC)
     *
     * @param id
     * @param type
     * @return
     */
    public DigitalIDEntity searchDigitalId(String id, String type) throws ORDSQueryException, Exception{
        String [] parameters = {id, type};
        try {
            //could not get properties to load when initialized in constructor

            JsonNode result = ORDSService.runSelectQuery(targetCredential, SEARCH_DIGITALID_QUERY, parameters);

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("DigitalIDEntity was not found for parameters {identity_type_code=" + parameters[1] + ", identity_type_value=" + parameters[0] + "}");
                throw new NoOrdsResultsFoundException("DigitalIDEntity was not found for parameters {identity_type_code=" + parameters[1] + ", identity_type_value=" + parameters[0] + "}");
            }
        } catch (ORDSQueryException e) {
            logger.error("Error while searching DigitalID: " + e);
            throw new ORDSQueryException("Error while searching DigitalID", e);
        } catch (Exception e) {
            logger.error("Error while searching DigitalID: " + e);
            throw new Exception("Error while searching DigitalID", e);
        }
    }

    /**
     * Search for DigitalIDEntity by digital id
     *
     * @param id
     * @return
     * @throws ORDSQueryException
     * @throws Exception
     */
    public DigitalIDEntity retrieveDigitalID(String id) throws ORDSQueryException, Exception {
        try {

            JsonNode result = ORDSService.runSelectQuery(targetCredential, RETRIEVE_DIGITALID_QUERY, id);

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("DigitalIDEntity was not found for parameters {digitalID=" + id + "}");
                throw new NoOrdsResultsFoundException("DigitalIDEntity was not found for parameters {digitalID=" + id + "}");
            }
        } catch (ORDSQueryException e) {
            logger.error("Error while retrieving DigitalID: " + e);
            throw new ORDSQueryException("Error while retrieving DigitalID", e);
        } catch (Exception e) {
            logger.error("Error while retrieving DigitalID: " + e);
            throw new Exception("Error while retrieving DigitalID", e);
        }
    }

    /**
     * Creates a DigitalIDEntity
     *
     * @param digitalID
     * @return
     * @throws ORDSQueryException
     * @throws Exception
     */
    public DigitalIDEntity createDigitalID(DigitalIDEntity digitalID) throws ORDSQueryException, Exception {
        String [] parameters = {digitalID.getIdentityValue(), digitalID.getIdentityTypeCode()};
        try {
            ORDSService.runInsertQuery(targetCredential, INSERT_DIGITALID_QUERY, getCreateFields(digitalID));
            JsonNode result = ORDSService.runSelectQuery(targetCredential, SEARCH_DIGITALID_QUERY, parameters);

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("DigitalIDEntity was not found for parameters {identity_type_code=" + parameters[1] + ", identity_type_value=" + parameters[0] + "}");
                throw new NoOrdsResultsFoundException("DigitalIDEntity was not found for parameters {identity_type_code=" + parameters[1] + ", identity_type_value=" + parameters[0] + "}");
            }
        } catch (ORDSQueryException e) {
            logger.error("Error while creating DigitalID: " + e);
            throw new ORDSQueryException("Error while creating DigitalID", e);
        } catch (Exception e) {
            logger.error("Error while creating DigitalID: " + e);
            throw new Exception("Error while creating DigitalID", e);
        }
    }

    /**
     * Updates a DigitalIDEntity
     *
     * @param digitalID
     * @return
     * @throws ORDSQueryException
     * @throws Exception
     */
    public DigitalIDEntity updateDigitalID(DigitalIDEntity digitalID) throws ORDSQueryException, Exception {
        try {
            ORDSService.runUpdateQuery(targetCredential, UPDATE_DIGITALID_QUERY, getFieldsForUpdate(digitalID));
            JsonNode result = ORDSService.runSelectQuery(targetCredential, RETRIEVE_DIGITALID_QUERY, digitalID.getDigitalID().toString());

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("DigitalIDEntity was not found for parameters {identity_type_code=" + digitalID.getIdentityTypeCode() + ", identity_value=" + digitalID.getIdentityValue() + "}");
                throw new NoOrdsResultsFoundException("DigitalIDEntity was not found for parameters {identity_type_code=" + digitalID.getIdentityTypeCode() + ", identity_value=" + digitalID.getIdentityValue() + "}");
            }
        } catch (ORDSQueryException e) {
            logger.error("Error while updating DigitalID: " + e);
            throw new ORDSQueryException("Error while updating DigitalID", e);
        } catch (Exception e) {
            logger.error("Error while updating DigitalID: " + e);
            throw new Exception("Error while updating DigitalID", e);
        }
    }

    private Object [] getCreateFields(DigitalIDEntity digitalID) {

        digitalID.setCreateDate(new Date());
        digitalID.setCreateUser(ApplicationProperties.CLIENT_ID);

        Object[] updateFields = getFieldsForUpdate(digitalID);
        Object[] fields = new Object[updateFields.length + 1];
        System.arraycopy(updateFields, 0, fields, 0, fields.length-1);
        fields[fields.length-2] = digitalID.getCreateUser(); //we ditch the null digitalID_ID here
        fields[fields.length-1] = digitalID.getCreateDate();

        return fields;
    }

    private Object [] getFieldsForUpdate(DigitalIDEntity digitalID) {

        digitalID.setUpdateDate(new Date());
        digitalID.setUpdateUser(ApplicationProperties.CLIENT_ID);

        return new Object[] {
                digitalID.getStudentID(),
                digitalID.getIdentityTypeCode(),
                digitalID.getIdentityValue(),
                digitalID.getLastAccessDate(),
                digitalID.getLastAccessChannelCode(),
                digitalID.getUpdateUser(),
                digitalID.getUpdateDate(),
                digitalID.getDigitalID()
        };
    }
}
