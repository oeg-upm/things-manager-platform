package oeg.cogito.thingmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oeg.cogito.thingmanager.controller.exceptions.PersistenObjectNotFound;
import oeg.cogito.thingmanager.model.FileStored;
import oeg.cogito.thingmanager.repository.FileRepository;

@Service
public class FilesService {

	@Autowired
	public FileRepository filesRepository;
	
	public List<FileStored> findAll() {
		return (List<FileStored>) filesRepository.findAll();
	}
	
	public FileStored findById(String id) {
		Optional<FileStored> fileOpt = filesRepository.findByUuid(id);
		if(!fileOpt.isPresent())
			throw new PersistenObjectNotFound("Provided 'id' belongs to no stored file");
		return fileOpt.get();
		
	}

	public FileStored findByContainer(String cid, String fid) {
		return filesRepository.findByUuidAndContainerId(fid, cid);
	}	
}
