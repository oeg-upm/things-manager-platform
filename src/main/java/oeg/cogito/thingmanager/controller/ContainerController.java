package oeg.cogito.thingmanager.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import oeg.cogito.thingmanager.model.FileContainer;
import oeg.cogito.thingmanager.service.ContainerService;

@RestController
public class ContainerController {

	// -- Attributes
	
	@Autowired
	public ContainerService containerService;
	
	// -- Constructor
	
	// -- Methods: read
	
	@GetMapping("/containers") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<FileContainer> listEntries(@RequestParam(name="id", required=false) List<String> ids, @RequestParam(name="name", required=false) List<String> names) {
		if(ids == null && names ==null) {
			return containerService.findAll();
		}else {
			return containerService.findByNamesOrIds(ids, names);
		}
	}
	
	@GetMapping("/containers/{id}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
    public FileContainer getContainer(@PathVariable String id) {
			return containerService.read(id);
	}
	
	// -- Methods: create
	/*
	@PostMapping("/containers/{id}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
    public FileContainer newContainer(@PathVariable String id, @RequestParam(name="name", required=true) String name) {
		return containerService.create(id, name);
	}
	*/
	
	@PostMapping("/containers") 
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
    public FileContainer newAnonymousContainer(@RequestParam(name="name", required=true) String name) {
		
		return containerService.create(null, name);
	}
	
	// -- Methods: update
	
	@PutMapping("/containers/{id}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
    public FileContainer updateContainer(@PathVariable String id, @RequestParam(name="name", required=false) String name) {
		return containerService.update(id, name);
	}
	
	// -- Methods: delete

	@DeleteMapping("/containers/{id}") 
	@ResponseBody
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFile(@PathVariable String id) {
		containerService.delete(id);
	}
	
	
	
}
