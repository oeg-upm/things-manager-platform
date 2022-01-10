package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when an object from the model is requested but is not found in the database
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersistenObjectNotFound extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public PersistenObjectNotFound() {
    	super();
    }

    public PersistenObjectNotFound(String message) {
       super(message);
    }



}
