package ca.bc.gov.educ.api.digitalID.exception;

/**
 * InvalidParameterException to provide error details when unexpected parameters are passed to endpoint
 *
 * @author John Cox
 *
 */

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String... searchParamsMap) {
        super(InvalidParameterException.generateMessage(searchParamsMap));
    }

    private static String generateMessage(String... searchParams) {
        String message = "Unexpected request parameters provided: ";
        for (String parameter:searchParams) {
            message += parameter + ", ";
        }
        message = message.substring(0, message.length()-2);
        return message;
    }
}
