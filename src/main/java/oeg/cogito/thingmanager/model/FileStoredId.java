package oeg.cogito.thingmanager.model;

import java.io.Serializable;
import java.util.Objects;


public class FileStoredId implements Serializable {

	// -- Attributes
	
	private static final long serialVersionUID = -4641598808250988894L;
	private String name;
	private String containerId;
	private String format;
	
	// Constructors
	
	public FileStoredId() {
		super();
	}
		
	// -- Getters & Setters


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
		FileStoredId other = (FileStoredId) obj;
		return Objects.equals(containerId, other.containerId) && Objects.equals(format, other.format)
				&& Objects.equals(name, other.name);
	}
	
	

	
	
	
	
	

	
	
	
}
