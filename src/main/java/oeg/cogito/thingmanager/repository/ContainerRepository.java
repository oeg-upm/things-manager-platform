package oeg.cogito.thingmanager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import oeg.cogito.thingmanager.model.FileContainer;


public interface ContainerRepository extends CrudRepository<FileContainer, String> {
	 List<FileContainer> findByName(@Param("name") String name);
	 List<FileContainer> findByIdOrName(@Param("id") String id, @Param("name") String name);
}