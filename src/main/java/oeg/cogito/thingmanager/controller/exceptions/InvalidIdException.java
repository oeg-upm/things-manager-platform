package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when an id related to a {@link FileStored} or {@link FileContaier} is not valid
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidIdException extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public InvalidIdException() {
    	super();
    }

    public InvalidIdException(String message) {
       super(message);
    }



}
