package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when an internal exception or error is thrown within the system
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public InternalException() {
    	super();
    }

    public InternalException(String message) {
       super(message);
    }



}
