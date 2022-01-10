package oeg.cogito.thingmanager.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represents a file that is stored in this system, the files are always located within a container
 * @author Andrea Cimmino
 *
 */
@Entity
@IdClass(FileStoredId.class)
public class FileStored {

	// -- Attributes
	
	private String uuid;
	
	@Id
	private String name;
	@Id
	private String containerId;
	
	@NotEmpty(message = "The 'content' of a File can not be empty")
	@JsonIgnore
	@Lob
	private byte[] content;

	@Id
	private String format;
	
	private String description;
	

	
	
	// Constructors
	
	public FileStored() {
		super();
	}
		
	// -- Getters & Setters


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getContainer() {
		return containerId;
	}

	public void setContainer(String container) {
		this.containerId = container;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	// -- HashCode & Equals
	
	@Override
	public int hashCode() {
		return Objects.hash(containerId, format, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileStored other = (FileStored) obj;
		return Objects.equals(containerId, other.containerId) && Objects.equals(format, other.format)
				&& Objects.equals(name, other.name);
	}
	
	

	
	
	
	
	

	
	
	
}
