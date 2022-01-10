package oeg.cogito.thingmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * This class represents a container that is meant to store files
 * @author Andrea Cimmino
 *
 */
@Entity
public class FileContainer {

	// -- Attributes
	
	@Id
	private String id;
	
	private String name;	
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileStored> files = new ArrayList<>();
	
	private String description;
	
	// -- Constructors 
	
	public FileContainer() {
		super();

	}
	
	// -- Getters & Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
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

	public List<FileStored> getFiles() {
		return files;
	}

	public void setFiles(List<FileStored> files) {
		this.files = files;
	}
	
	public void addFile(FileStored file) {
		files.add(file);
		file.setContainer(id);
	}

	// -- HashCode & Equals
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileContainer other = (FileContainer) obj;
		return Objects.equals(name, other.name);
	}

	// -- Representation
	
	@Override
	public String toString() {
		return "DigitalTwin [id=" + id + ", name=" + name + "]";
	}

	


	

	

}
