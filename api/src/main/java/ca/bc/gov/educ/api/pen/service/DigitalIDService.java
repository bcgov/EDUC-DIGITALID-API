package ca.bc.gov.educ.api.pen.service;

import ca.bc.gov.educ.api.pen.model.DIGITAL_IDENTITY;
import ca.bc.gov.educ.api.pen.props.ApplicationProperties;
import ca.bc.gov.educ.ords.exception.NoOrdsResultsFoundException;
import ca.bc.gov.educ.ords.exception.ORDSQueryException;
import ca.bc.gov.educ.ords.model.ORDSTargetCredential;
import ca.bc.gov.educ.ords.service.ORDSService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author John Cox
 */

@Service
public class DigitalIDService {
    private static final Log logger = LogFactory.getLog(DIGITAL_IDENTITY.class);

    private final String selectDigitalIdQuery = "select di.digital_identity_id, di.student_id, di.identity_type_code, di.identity_value, di.last_access_time, di.last_access_channel_code, di.create_user, di.create_date, di.update_user, di.update_date from digital_identity di left join identity_type_code itc on di.identity_type_code=itc.identity_type_code where lower(di.digital_identity_id) = lower(?) and lower(itc.description) = lower(?);";

    /**
     * Search for Digital Identity by id and type (BCeID or BCSC)
     *
     * @param id
     * @param type
     * @return
     */
    public DIGITAL_IDENTITY loadDigitalId(String id, String type) {
        String [] parameters = {id, type};
        try {
            logger.debug("Connecting to ORDS");
            //could not get properties to load when initialized in constructor
            ORDSTargetCredential targetCredential = new ORDSTargetCredential(ApplicationProperties.ORDS_USERNAME,
                    ApplicationProperties.ORDS_PASSWORD, ApplicationProperties.ORDS_URL);
            ObjectMapper objectMapper = new ObjectMapper();

            logger.debug("----> Using URL: " + ApplicationProperties.ORDS_URL);
            logger.debug("----> Using User: " + ApplicationProperties.ORDS_USERNAME);
            logger.debug("----> Querying by " + type + " with id " + id);

            JsonNode result = ORDSService.runSelectQuery(targetCredential, selectDigitalIdQuery, parameters);

            logger.debug("Query returned: " + result);

            if (result != null && result.size() != 0) {
                DIGITAL_IDENTITY item = objectMapper.readValue(result.get(0).toString(), DIGITAL_IDENTITY.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("No digital identity found with requested id: " + id);
                return null;
            }
        } catch (NoOrdsResultsFoundException | IOException e) {
            throw new NoSuchClientException("There was an issue loading digital identity from ords: " + e);
        } catch (ORDSQueryException e) {
            logger.error("Error occurred loading digital identity: " + e);
            return null;
        }
    }
}
