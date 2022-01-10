package oeg.cogito.thingmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oeg.cogito.thingmanager.TMUtils;
import oeg.cogito.thingmanager.controller.exceptions.InvalidIdException;
import oeg.cogito.thingmanager.controller.exceptions.PersistenObjectExists;
import oeg.cogito.thingmanager.controller.exceptions.PersistenObjectNotFound;
import oeg.cogito.thingmanager.controller.exceptions.UnsupportedFormatException;
import oeg.cogito.thingmanager.model.FileContainer;
import oeg.cogito.thingmanager.model.FileStored;
import oeg.cogito.thingmanager.model.MetaFileStored;
import oeg.cogito.thingmanager.model.factory.FilesFactory;
import oeg.cogito.thingmanager.model.factory.WoT;
import oeg.cogito.thingmanager.repository.ContainerRepository;

@Service
public class ContainerService {

	// -- Attributes
	
	@Autowired
	public ContainerRepository repository;

	@Autowired
	public WoTService wotService;
		
	@Autowired
	public SSEService sseService;
	
	private static final Boolean syncTDD = true;
	
	
	// -- Constructor
	
	public ContainerService() {
		super();
	}
	
	// -- CONTAINERS
	
	// -- Methods: read
	
		public FileContainer read(String id) throws PersistenObjectNotFound {
			Optional<FileContainer> containerOpt = repository.findById(id);
			if(!containerOpt.isPresent())
				throw new PersistenObjectNotFound("There are no Containers with the provided 'id'");
			return containerOpt.get();
			
		}

		public FileStored read(String cid, String fid) {
			FileContainer container = read(cid);
			Optional<FileStored> fileOpt = container.getFiles().parallelStream().filter(file -> file.getUuid().equals(fid)).findFirst();
			if(!fileOpt.isPresent())
				throw new PersistenObjectNotFound("The provided file 'id' does not belong to any stored file");
			return fileOpt.get();
		}


		public List<FileContainer> findAll() {
			return (List<FileContainer>) repository.findAll();
		}
		
		
		// TODO: improve this method with native query
		public List<FileContainer> findByNamesOrIds(List<String> ids, List<String> names) {
			if(ids==null) { // find by names
				return findAll().parallelStream().filter(entry -> names.contains(entry.getName())).collect(Collectors.toList());
			}else if(names==null) { // find by ids
				return findAll().parallelStream().filter(entry ->  ids.contains(entry.getId())).collect(Collectors.toList());
			}else { // find by names or ids
				return findAll().parallelStream().filter(entry -> names.contains(entry.getName()) || ids.contains(entry.getId()) ).collect(Collectors.toList());
			}
		}
	
	// -- Methods: create
	
	public FileContainer create(String id, String name) {
		// 1. validate ID
		if(id!=null && !id.contains(":"))
			throw new InvalidIdException("Privided 'id' has not a valid syntax. Please provide as 'id' a URI or a UUID with a prefix, for instance 'uuid:container:f883cd1c-4f7a-4009-aab9-f843fdd11933'");
		// 2. Create container
		FileContainer container = FilesFactory.createContainer(id, name);
		if(repository.existsById(container.getId()))
			throw new PersistenObjectExists("A Container with the privided 'id' already exists, remove first the existing one");
		// 3. Create associated TD
		if(syncTDD) 
			wotService.containerCreated(container);
		// 4. Persist if no error occurred
		repository.save(container);
		sseService.publishEvent(TMUtils.toJson(container),"container;created");
		return container;
	}
	
	
	
	// -- Methods: delete
	
	public void delete(String id) {
		// 1. Check if resource exists
		Optional<FileContainer> containerOpt = repository.findById(id);
		if(!containerOpt.isPresent())
			throw new PersistenObjectNotFound("The privided 'id' does not belong to any existing Container");
		FileContainer container = containerOpt.get();
		// 2. Delete TD associated
		if(syncTDD) {
			wotService.containerDeletedCascade(container);
		}
		// 3. Update database
		repository.deleteById(id);
		sseService.publishEvent(TMUtils.toJson(container),"container;deleted");
	}
	
	
	// -- Methods: update

	public FileContainer update(String id, String name) {
		// 1. Find and remove existing object
		FileContainer existingContainer = read(id);
		delete(id);	
		// 2. Update container
		existingContainer.setName(name);
		// 3. Update associated TD
		if(syncTDD) 
			wotService.containerUpdate(existingContainer);
		// 4. Register new container
		repository.save(existingContainer);
		sseService.publishEvent(TMUtils.toJson(existingContainer),"updated");
		return existingContainer;
		
	}

	// FILES
	
	public FileContainer addFile(String cid, FileStored file) {
		FileContainer container = read(cid);
		
		container.addFile(file);
		// 2. Update associated TD
		if(syncTDD) {
			wotService.containerUpdate(container);
			wotService.fileCreated(container, file);
		}
		// 3. Store new container
		repository.save(container);
		sseService.publishEvent(TMUtils.toJson(container),"container;updated");
		sseService.publishEvent(TMUtils.toJson(file),"file;created");
		
		return container;
	}
	
	
	

	
	public void deleteFile(String cid, String fid) {
		FileContainer container = read(cid);
		Optional<FileStored> fileOpt = container.getFiles().parallelStream().filter(file -> file.getUuid().equals(fid)).findFirst();
		if(!fileOpt.isPresent())
			throw new PersistenObjectNotFound("Provided file 'id' belongs to a not existing file");
		FileStored file = fileOpt.get();
		container.getFiles().remove(file);
		if(syncTDD) {
			wotService.containerUpdate(container);
			wotService.fileDeleted(file);
			// TODO: delete meta files associated
		}
		repository.save(container);
		sseService.publishEvent(TMUtils.toJson(container),"container;updated");
		sseService.publishEvent(TMUtils.toJson(file),"file;deleted");
	}

	public FileContainer addMetaFile(MetaFileStored file) {
		// Check that container and related file exists
		FileContainer container = read(file.getContainer());
		read(file.getContainer(), file.getRelatedFile());
		// upload file to container
		container.addFile(file);
		
		// 2. Update associated TDs
		if(syncTDD) {
			wotService.containerUpdate(container);
			wotService.containerEnriched(container, file);
		}
		repository.save(container);
		sseService.publishEvent(TMUtils.toJson(container),"container;updated");
		sseService.publishEvent(TMUtils.toJson(file),"meta;created");
		
		return container;
	}
	
	
	
	
}
