package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown if a {@link FileStored} provided by a user has a format that is not supported by this system.
 * The format is checked against a list of supported formats located in the file './template/formats.csv' (by default).
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnsupportedFormatException extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public UnsupportedFormatException() {
    	super();
    }

    public UnsupportedFormatException(String message) {
       super(message);
    }



}
