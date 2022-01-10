package oeg.cogito.thingmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import oeg.cogito.thingmanager.model.FileStored;


public interface FileRepository extends CrudRepository<FileStored, String> {
	List<FileStored> findByName(@Param("name") String name);
	Optional<FileStored> findByUuid(@Param("uuid") String uuid);
	FileStored findByUuidAndContainerId(@Param("uuid") String id, @Param("containerId") String cid);
}