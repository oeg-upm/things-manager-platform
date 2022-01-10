package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown where an error is output by the Thing Directory
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ThingDirectoryException extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public ThingDirectoryException() {
    	super();
    }

    public ThingDirectoryException(String message) {
       super(message);
    }



}
