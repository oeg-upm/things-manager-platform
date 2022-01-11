package oeg.cogito.thingmanager.model.factory;

import java.util.UUID;

import oeg.cogito.thingmanager.TMConfiguration;
import oeg.cogito.thingmanager.TMUtils;
import oeg.cogito.thingmanager.model.FileContainer;
import oeg.cogito.thingmanager.model.FileStored;
import oeg.cogito.thingmanager.model.MetaFileStored;
/**
 * Factory class for the creation of {@link FileStored} and {@link FileContainer} objects.
 * @author Andrea Cimmino
 *
 */
public class FilesFactory {

	// -- Atributes
	
	private static final String PREFIX_CONTAINER = "uuid:container:";	
	private static final String PREFIX_FILE = "uuid:file:";	
	private static final String PREFIX_META = "uuid:meta:";	

	
	// -- Constructor
	
	private FilesFactory() {
		super();
	}
	
	// -- Methods
	
	/**
	 * This method creates a {@link FileStored} object
	 * @param id if null a random UUID will be assigned
	 * @param format the file format
	 * @param name the name of the file
	 * @param content the content of the file
	 * @param containerId the container id where this file will be stored
	 * @return a {@link FileStored} object
	 */
	public static FileStored createFile(String id, String format, String name, byte[] content, String containerId) {
		FileStored file = new FileStored();
		if(id==null)
			id = TMUtils.concat(PREFIX_FILE, UUID.randomUUID().toString());
		file.setUuid(id);
		file.setContent(content);
		file.setFormat(format);
		file.setName(name);
		file.setContainer(containerId);
		file.setDescription(WoT.formatURI(id));
		return file;
	}
	
	/**
	 * This method creates a {@link MetaFileStored} object
	 * @param id if null a random UUID will be assigned
	 * @param format the file format
	 * @param name the name of the file
	 * @param content the content of the file
	 * @param containerId the container id where this file will be stored
	 * @return a {@link FileStored} object
	 */                              
	public static MetaFileStored createMetaFile(String id, String format, String name, byte[] content, String containerId, String relatedFile, String subject) {
		MetaFileStored file = new MetaFileStored();
		if(id==null)
			id = TMUtils.concat(PREFIX_META, UUID.randomUUID().toString());
		file.setUuid(id);
		file.setContent(content);
		file.setFormat(format);
		file.setName(name);
		file.setContainer(containerId);
		file.setDescription(WoT.formatURI(relatedFile));
		file.setRelatedFile(relatedFile);
		file.setEnriches(subject);
		return file;
	}
	
	/**
	 * This method creates a {@link FileContainer}
	 * @param id if null a random UUID will be assigned
	 * @param name the name of the container
	 * @return  a {@link FileContainer} object
	 */
	public static FileContainer createContainer(String id, String name) {
		FileContainer container = new FileContainer();
		container.setName(name);
		if(id==null)
			id = PREFIX_CONTAINER.concat(UUID.randomUUID().toString());
		container.setId(id);
		container.setDescription(WoT.formatURI(id));
		return container;
	}
	
	
	
	
	public static String buildFileStoredRawURI(FileStored file) {
		StringBuilder builder = new StringBuilder();
		builder.append(TMConfiguration.currentHost);
		builder.append("/containers/").append(file.getContainer()).append("/files/").append(file.getUuid());
		return builder.toString();
	}
	
	public static String buildFileStoredURI(FileStored file) {
		StringBuilder builder = new StringBuilder();
		builder.append(TMConfiguration.currentHost);
		builder.append("/files/").append(file.getUuid());
		return builder.toString();
	}
}
