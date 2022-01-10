package oeg.cogito.thingmanager.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import oeg.cogito.thingmanager.TMUtils;
import oeg.cogito.thingmanager.controller.exceptions.InternalException;
import oeg.cogito.thingmanager.model.FileContainer;
import oeg.cogito.thingmanager.model.FileStored;
import oeg.cogito.thingmanager.model.MetaFileStored;
import oeg.cogito.thingmanager.model.factory.FilesFactory;
import oeg.cogito.thingmanager.service.ContainerService;
import oeg.cogito.thingmanager.service.FilesService;

@RestController
public class FileController {

	// -- Attributes
	
	@Autowired
	public FilesService filesService;
	
	@Autowired
	public ContainerService containerService;
	
	// -- Methods: read
	
	@GetMapping("/files") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<FileStored> listEntries() {
		return filesService.findAll();
	}
	
	@GetMapping("/files/{id}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public FileStored listEntries(@PathVariable String id) {
		return filesService.findById(id);
	}
	
	
	@GetMapping("/containers/{cid}/files/{fid}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String listEntries(HttpServletResponse response, @PathVariable String cid,  @PathVariable String fid) {
		FileStored file = filesService.findByContainer(cid, fid);
		return new String(file.getContent());
		
	}
	
	// -- Methods: create 
	
	
	@PostMapping("/containers/{cid}/files") 
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
    public FileContainer createAnonymousFile(@PathVariable String cid, @RequestParam(name="file", required=true) MultipartFile file) {
		try {
			String name = FilenameUtils.getBaseName(file.getOriginalFilename());
			String format = FilenameUtils.getExtension(file.getOriginalFilename());

			TMUtils.checkFormat(format);
			FileStored fileStored = FilesFactory.createFile(null, format, name, file.getBytes(), cid);
			return containerService.addFile(cid, fileStored);
			
		} catch (IOException e) {
			throw new InternalException(e.toString());
		}
	}
	
	@PostMapping("/containers/{cid}/files/{fid}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
    public FileContainer createMetaFile(@PathVariable String cid, @PathVariable String fid, @RequestParam(name="file", required=true) MultipartFile file, @RequestParam(name="subject", required=false) String subject) {
		try {
			String name = FilenameUtils.getBaseName(file.getOriginalFilename());
			String format = FilenameUtils.getExtension(file.getOriginalFilename());

			TMUtils.checkMetaFormat(format);
			MetaFileStored fileStored = FilesFactory.createMetaFile(null, format, name, file.getBytes(), cid, fid, subject);
			return containerService.addMetaFile(fileStored);
			
		} catch (IOException e) {
			throw new InternalException(e.toString());
		}
	}
	
	// -- Methods: delete 
		
	@DeleteMapping("/containers/{cid}/files/{fid}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAnonymousFile(HttpServletResponse response, @PathVariable String cid, @PathVariable String fid) {
		containerService.deleteFile(cid, fid);
	}


	
	
}
