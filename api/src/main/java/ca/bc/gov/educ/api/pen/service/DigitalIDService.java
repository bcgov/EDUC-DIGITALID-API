package ca.bc.gov.educ.api.pen.service;

import ca.bc.gov.educ.api.pen.model.DigitalIdentitityDAO;
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
    private static final Log logger = LogFactory.getLog(DigitalIdentitityDAO.class);

    private final String selectDigitalIdQuery = "select * from digital_identity where lower(digital_identity_id) = lower(?) and lower(identity_type_code) = lower(?);";

    /**
     * Search for Digital Identity by id and type (BCeID or BCSC)
     *
     * @param id
     * @param type
     * @return
     */
    public DigitalIdentitityDAO loadDigitalId(String id, String type) {
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
                DigitalIdentitityDAO item = objectMapper.readValue(result.get(0).toString(), DigitalIdentitityDAO.class);
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
}
