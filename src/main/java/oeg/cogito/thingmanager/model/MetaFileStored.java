package oeg.cogito.thingmanager.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This class represents a file that is stored in this system, the files are always located within a container
 * @author Andrea Cimmino
 *
 */
@Entity
public class MetaFileStored extends FileStored{

	// -- Attributes
	@NotBlank
	private String relatedFile;
	// the subject in the RDF
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String enriches;
	
	
	// Constructors
	
	public MetaFileStored() {
		super();
	}
		
	// -- Getters & Setters


	
	public String getRelatedFile() {
		return relatedFile;
	}

	public void setRelatedFile(String relatedFile) {
		this.relatedFile = relatedFile;
	}

	public String getEnriches() {
		return enriches;
	}

	public void setEnriches(String enriches) {
		this.enriches = enriches;
	}

	// -- HashCode & Equals
	
	
	

	
	
	
}
