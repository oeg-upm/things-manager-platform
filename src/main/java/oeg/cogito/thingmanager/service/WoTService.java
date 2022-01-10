package oeg.cogito.thingmanager.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Service;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import oeg.cogito.thingmanager.TMConfiguration;
import oeg.cogito.thingmanager.TMUtils;
import oeg.cogito.thingmanager.controller.exceptions.ThingDirectoryException;
import oeg.cogito.thingmanager.model.FileContainer;
import oeg.cogito.thingmanager.model.FileStored;
import oeg.cogito.thingmanager.model.MetaFileStored;
import oeg.cogito.thingmanager.model.factory.FilesFactory;
import oeg.cogito.thingmanager.model.factory.WoT;

@Service
public class WoTService {

	private static final String TD_TEMPLATE  = "{\n"
			+ "    \"@context\": [\"https://www.w3.org/2019/wot/td/v1\", {\"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\"}],\n"
			+ "    \"securityDefinitions\": { \"nosec_sc\": { \"scheme\": \"nosec\" }},\n"
			+ "    \"security\": [\"nosec_sc\"]\n"
			+ "}";
	
	private static final String TD_PROPERTIES = "properties";
	private static final String TD_FORMS = "forms";
	private static final String TD_TITLE = "title";
	private static final String TD_ID = "id";
	private static final String TD_DESCRIPTION = "description";
	
	
	public void containerCreated(FileContainer container) {
		JsonObject td = creatPlainTD(container.getId(), container.getName());
		WoT.registerTD(container.getId(), td.toString());
	}
	
	private JsonObject creatPlainTD(String uuid, String name, String container) {
		JsonObject plainTd = TMUtils.toJson(TD_TEMPLATE);
		plainTd.addProperty(TD_ID, uuid);
		plainTd.addProperty(TD_TITLE, name);
		plainTd.addProperty(TD_DESCRIPTION, WoT.formatURI(container));
		
		return plainTd;
	}
	
	private JsonObject creatPlainTD(String id, String name) {
		JsonObject plainTd = TMUtils.toJson(TD_TEMPLATE);
		plainTd.addProperty(TD_ID, id);
		plainTd.addProperty(TD_TITLE, name);
		return plainTd;
	}
	
	public  void containerDeletedCascade(FileContainer container) {
		WoT.deleteTD(container.getId());
		container.getFiles().forEach(file -> WoT.deleteTD(file.getUuid()));
	}
	
	public  void containerUpdate(FileContainer container) {
		JsonObject td = creatPlainTD(container.getId(), container.getName());
		processContainerFiles(td, container.getFiles());
		WoT.registerTD(container.getId(), td.toString());
	}
	
	private  void processContainerFiles(JsonObject td, List<FileStored> files) {
		
		JsonObject properties = new JsonObject();
		List<FileStored> trueFiles = files.parallelStream().filter(file -> !(file instanceof MetaFileStored)).collect(Collectors.toList());
		List<MetaFileStored> metaFiles = files.parallelStream().filter(MetaFileStored.class::isInstance).map(MetaFileStored.class::cast).collect(Collectors.toList());

		for(int index=0; index < trueFiles.size(); index++) {
			FileStored file = trueFiles.get(index);
			// find associated meta-files
			List<MetaFileStored> relatedMetaFiles = metaFiles.parallelStream().filter(meta-> meta.getRelatedFile().equals(file.getUuid())).collect(Collectors.toList());
			metaFiles.removeAll(relatedMetaFiles);
			// 
			properties.add(file.getName(), fromFileToTDEntry(file, relatedMetaFiles));	
		}
		for(int index=0; index < metaFiles.size(); index++) {
			properties.add(metaFiles.get(index).getName(), fromFileToTDEntry(metaFiles.get(index), null));
		}
	
		td.add(TD_PROPERTIES, properties);
	}
		
	private  JsonObject fromFileToTDEntry(FileStored file, List<MetaFileStored> metaFiles) {
		
		JsonArray forms = buildForms(file, metaFiles);
		JsonObject info = new JsonObject();
		info.addProperty(TD_TITLE, file.getUuid());
		info.add(TD_FORMS, forms);
		
		return info;
	}
	
	private  JsonArray buildForms(FileStored file, List<MetaFileStored> metaFiles) {
		JsonObject form1 = new JsonObject();
		form1.addProperty("href", FilesFactory.buildFileStoredRawURI(file));
		form1.addProperty("contentType", file.getFormat());
	
		JsonObject form2 = new JsonObject();
		form2.addProperty("href", FilesFactory.buildFileStoredURI(file));
		form2.addProperty("contentType", "json");
		
		
		JsonArray forms = new JsonArray();
		forms.add(form1);
		forms.add(form2);
		if(metaFiles==null)
			metaFiles = new ArrayList<>();
		for(int index=0; index <metaFiles.size(); index++) {
			MetaFileStored metaFile = metaFiles.get(index);
			JsonObject formY = new JsonObject();
			formY.addProperty("href", FilesFactory.buildFileStoredRawURI(metaFile));
			formY.addProperty("contentType", metaFile.getFormat());
			
			JsonObject formX = new JsonObject();
			formX.addProperty("href", FilesFactory.buildFileStoredURI(metaFile));
			formX.addProperty("contentType", "json");
			forms.add(formY);
			forms.add(formX);
		}
		
		return forms;
	}
	
	public void fileCreated(FileContainer container, FileStored file) {
		JsonObject td = creatPlainTD(file.getUuid(), file.getName(), file.getContainer());
		
		JsonObject properties = new JsonObject();
		properties.add(file.getName(), fromFileToTDEntry(file, null));
		td.add(TD_PROPERTIES, properties);
		
		WoT.registerTD(file.getUuid(), td.toString());
	}


	public void containerEnriched(FileContainer container, MetaFileStored metaFile) {
		//1. retrieve thing description of the file
		JsonObject td = WoT.getTd(metaFile.getRelatedFile());
		if(metaFile.getFormat().equals("ttl"))
			injectRDFInTd(td, metaFile);
		//2. Check if we are enriching in general (TD name) or specifically a subject
		String propertyName = metaFile.getEnriches();
		if(metaFile.getEnriches()==null)
			propertyName = td.get(TD_TITLE).getAsString();
		try {
			JsonObject properties = td.remove(TD_PROPERTIES).getAsJsonObject();
			JsonObject property = properties.remove(propertyName).getAsJsonObject();
					
			JsonObject form = new JsonObject();
			form.addProperty("href", FilesFactory.buildFileStoredRawURI(metaFile));
			form.addProperty("contentType", metaFile.getFormat());
			JsonArray forms = property.remove(TD_FORMS).getAsJsonArray();
			forms.add(form);
					
			property.add(TD_FORMS, forms);
			properties.add(propertyName, property);
			td.add(TD_PROPERTIES, properties);
		}catch(Exception e) {
			throw new ThingDirectoryException("Provided subject does not exists as a property in the Thing Description");
		}
		System.out.println(td);
		WoT.registerTD(metaFile.getRelatedFile(), td.toString());
	}

	private void injectRDFInTd(JsonObject td, MetaFileStored metaFile) {
		JsonObject properties = td.remove(TD_PROPERTIES).getAsJsonObject();
		
		Model rdfModel = ModelFactory.createDefaultModel();
		rdfModel.read(new ByteArrayInputStream(metaFile.getContent()), TMConfiguration.WoTHive, "ttl");
		 Query query = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n select ?s ?type ?label { ?s ?p ?o . OPTIONAL { ?s a ?t} . OPTIONAL {?s rdfs:label ?label} . }");
		 ResultSet rs = QueryExecutionFactory.create(query, rdfModel).execSelect();
		 while(rs.hasNext()) {
			QuerySolution qs = rs.next();
			if(qs.contains("?s")) {
				String propertyName = qs.get("?s").toString();
				JsonObject property = new JsonObject();
				JsonArray forms = new JsonArray();
				if(properties.has(propertyName)) {
					property = properties.remove(propertyName).getAsJsonObject();
					if(properties.has(TD_FORMS))
						forms = property.remove(TD_FORMS).getAsJsonArray();
				}
				JsonObject form = new JsonObject();
				form.addProperty("href", WoT.formatSparqlURI(propertyName));
				form.addProperty("contentType", metaFile.getFormat());
				forms.add(form);
				
				property.add(TD_FORMS, forms);
				properties.add(propertyName, property);
				/*if(qs.contains("?type"))
					properties.addProperty("@type", qs.get("?type").toString());
				if(qs.contains("?label"))
					properties.addProperty("title", qs.get("?label").toString());*/
			}
		 }
		 td.add(TD_PROPERTIES, properties);
	}

	public void fileDeleted(FileStored file) {
		WoT.deleteTD(file.getUuid());
	}
}
