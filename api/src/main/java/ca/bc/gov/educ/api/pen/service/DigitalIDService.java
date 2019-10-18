package ca.bc.gov.educ.api.pen.service;

import ca.bc.gov.educ.api.pen.model.DigitalIdentity;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author John Cox
 */

@Component
public class DigitalIDService {
    private static final Log logger = LogFactory.getLog(DigitalIdentity.class);

    @Autowired
    private ApplicationProperties props;
    private ObjectMapper objectMapper;
    private ORDSTargetCredential targetCredential;

    public DigitalIDService() {
        objectMapper = new ObjectMapper();
    }
    /**
     * Search for Digital Identity by id and type (BCeID or BCSC)
     *
     * @param id
     * @param type
     * @return
     */
    @Cacheable("DigitalIdentity")
    public DigitalIdentity loadDigitalId(String id, String type) {
        String selectDigitalId = "select * from digital_identity;";
        String [] parameters = {id, type};
        logger.warn("Connecting to ORDS");
        try {
            logger.warn("Connecting to ORDS");
            //could not get properties to load when initialized in constructor
            targetCredential = new ORDSTargetCredential(ApplicationProperties.ORDS_USERNAME,
                    ApplicationProperties.ORDS_PASSWORD, ApplicationProperties.ORDS_URL);
            logger.warn("Connected to ORDS, running query");
            JsonNode result = ORDSService.runSelectQuery(targetCredential, selectDigitalId);
            logger.warn("Query returned: " + result);
            if (result != null) {
                DigitalIdentity item = objectMapper.readValue(result.get(0).toString(), DigitalIdentity.class);
                logger.debug(item);
                return item;
            } else {
                logger.warn("No digital identity found with requested id: " + id);
                return null;
            }
        } catch (NoOrdsResultsFoundException | IOException e) {
            logger.warn("No digital identity found with requested id: " + id);
            return null;
        } catch (ORDSQueryException e) {
            logger.error("Error occurred loading digital identity: " + e);
            return null;
        }
    }
}
