package ca.bc.gov.educ.api.pen.model;

import lombok.Data;

/**
 * Digital Identity DAO object
 *
 * @author John Cox
 */

@Data
public class DigitalIdentity {
    private int digitalIdentityID;
    private int studentID;
    private IdentityTypeCode identityTypeCode;
    private int identityValue;
    private int lastAccessTime;
    private LastAccessChannel lastAccessChannel;
}
