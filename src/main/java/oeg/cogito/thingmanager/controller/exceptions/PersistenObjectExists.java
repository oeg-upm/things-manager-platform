package oeg.cogito.thingmanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown where an object from the model must be created in the database, but it already exists.
 * @author Andrea Cimmino
 *
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class PersistenObjectExists extends RuntimeException {

	private static final long serialVersionUID = -8191207544199515403L;


    public PersistenObjectExists() {
    	super();
    }

    public PersistenObjectExists(String message) {
       super(message);
    }



}
