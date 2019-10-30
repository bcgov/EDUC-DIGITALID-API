package ca.bc.gov.educ.api.digitalID.service;

import ca.bc.gov.educ.api.pen.model.DigitalIDEntity;
import ca.bc.gov.educ.api.pen.props.ApplicationProperties;
import ca.bc.gov.educ.ords.exception.NoOrdsResultsFoundException;
import ca.bc.gov.educ.ords.exception.ORDSQueryException;
import ca.bc.gov.educ.ords.model.ORDSTargetCredential;
import ca.bc.gov.educ.ords.service.ORDSService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * @author John Cox
 */

@Service
public class DigitalIDService {
    private static final Log logger = LogFactory.getLog(DigitalIDEntity.class);

    private final String DIGITAL_ID_FIELDS = "student_id, identity_type_code, identity_value, last_access_time, last_access_channel_code, create_user, create_date, update_user, update_date";
    private final String SEARCH_DIGITALID_QUERY = "select * from digital_identity where lower(identity_value) = lower(?) and lower(identity_type_code) = lower(?);";
    private final String RETRIEVE_DIGITALID_QUERY = "select * from digital_identity where lower(digital_identity_id) = lower(?);";
    private final String INSERT_DIGITALID_QUERY = "insert into digital_identity (" + DIGITAL_ID_FIELDS + ") values (?,?,?,?,?,?,?,?,?)";
    private final String UPDATE_DIGITALID_QUERY = "update digital_identity (" + DIGITAL_ID_FIELDS + ") values (?,?,?,?,?,?,?,?,?)";

    @Autowired
    private ObjectMapper objectMapper;

    ORDSTargetCredential targetCredential;

    @Autowired
    public DigitalIDService (@Value("${ords.username}") String ordsUsername, @Value("${ords.password}") String ordsPassword,  @Value("${ords.url}") String ordsUrl) {
        targetCredential = new ORDSTargetCredential(ordsUsername,
                ordsPassword, ordsUrl);
    }
    /**
     * Search for Digital Identity by id and type (BCeID or BCSC)
     *
     * @param id
     * @param type
     * @return
     */
    public DigitalIDEntity searchDigitalId(String id, String type) {
        String [] parameters = {id, type};
        try {
            //could not get properties to load when initialized in constructor

            JsonNode result = ORDSService.runSelectQuery(targetCredential, SEARCH_DIGITALID_QUERY, parameters);

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("No digital identity found with requested id: " + id);
                throw new NoOrdsResultsFoundException("No digital identity found with requested id: " + id);
            }
        } catch (NoOrdsResultsFoundException | IOException e) {
            logger.debug("There was an issue loading digital identity from ords: " + e);
            throw new NoSuchClientException("Error occurred loading digital identity: " + id);
        } catch (ORDSQueryException e) {
            logger.error("Error occurred loading digital identity: " + e);
            return null;
        }
    }


    public DigitalIDEntity retrieveDigitalID(String id) {
        try {

            JsonNode result = ORDSService.runSelectQuery(targetCredential, RETRIEVE_DIGITALID_QUERY, id);

            if (result != null && result.size() != 0) {
                DigitalIDEntity item = objectMapper.readValue(result.get(0).toString(), DigitalIDEntity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("No digital identity found with requested id: " + id);
                throw new NoOrdsResultsFoundException("No digital identity found with requested id: " + id);
            }
        } catch (NoOrdsResultsFoundException | IOException e) {
            logger.debug("There was an issue loading digital identity from ords: " + e);
            throw new NoSuchClientException("Error occurred loading digital identity: " + id);
        } catch (ORDSQueryException e) {
            logger.error("Error occurred loading digital identity: " + e);
            return null;
        }
    }

    public DigitalIDEntity createDigitalID(DigitalIDEntity digitalID) {
        try {
            ORDSService.runInsertQuery(targetCredential, INSERT_DIGITALID_QUERY, getFields(digitalID));
        } catch (ORDSQueryException e) {
            logger.error("Error occurred adding client: " + e);
        } catch (Exception e) {
            logger.error("Error converting to JSON object: ", e);
        }
        return null;
    }

    private Object [] getFields(DigitalIDEntity digitalID) {
        Date date = new Date();
        System.out.println(date);

        return new Object[] {
                digitalID.getStudentId(),
                digitalID.getIdentityTypeCode(),
                digitalID.getIdentityValue(),
                date,
                digitalID.getLastAccessChannelCode(),
                "API",
                date,
                "API",
                date};
    }
}
