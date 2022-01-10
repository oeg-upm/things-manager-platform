package oeg.cogito.thingmanager.model.factory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;

import oeg.cogito.thingmanager.TMConfiguration;
import oeg.cogito.thingmanager.TMUtils;
import oeg.cogito.thingmanager.controller.exceptions.ThingDirectoryException;


public class WoT {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TD_MIME = "application/td+json";
	
	/**
	 * This method instantiates a URI with the provided id using the domain of the Thing Directory
	 * @param id 
	 * @return a URI associated to a Thing Directory
	 */
	public static String formatURI(String id) {
		StringBuilder builder = new StringBuilder();
		builder.append(TMConfiguration.WoTHive).append("/api/things/").append(id);
		return builder.toString();
	}

	public static String formatSparqlURI(String uri) {
		StringBuilder builder = new StringBuilder();
		StringBuilder query = new StringBuilder();
		query.append("DESCRIBE <").append(uri).append(">");
		
		try {
			builder.append(TMConfiguration.triplestore).append("?query=").append(URLEncoder.encode(query.toString(), StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
	/**
	 * This method retrieves the Thing Description associated to a {@link FileContainer}
	 * @param id the container id
	 * @return a Thing Description as a {@link JsonObject}
	 * @throws ThingDirectoryException
	 */
	public static JsonObject getTd(String id) throws  ThingDirectoryException {
		try {
			HttpClient client = HttpClient.newHttpClient();
			System.out.println(formatURI(id));
			HttpRequest request = HttpRequest.newBuilder(URI.create(formatURI(id))).GET().build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			String body = response.body();
			String statusStr = String.valueOf(response.statusCode());

			if(body!=null && response.statusCode() == 200)
				return TMUtils.toJson(response.body());
			
			throw new ThingDirectoryException(TMUtils.concat("Request for creating a Thing Description failed, status code: ", statusStr,"; message: ",body));
		} catch (IOException | InterruptedException e) {
			throw new ThingDirectoryException(e.toString());
		}
	}
	
	/**
     * This method stores a Thing Description associated to a {@link FileContainer}
	 * @param id the container id
	 * @param td the Thing description to be stored
	 * @throws ThingDirectoryException if an error happened during the registration
	 */
	public static void registerTD(String id, String td) throws ThingDirectoryException{
		try {
			HttpClient client = HttpClient.newHttpClient();
			String directoryURI = formatURI(id);
			HttpRequest request = HttpRequest.newBuilder(URI.create(directoryURI))
					.header(CONTENT_TYPE, TD_MIME).PUT(BodyPublishers.ofString(td)).build();
			
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			String body = response.body();
			String statusStr = String.valueOf(response.statusCode());
			if (response.statusCode() != 201 & response.statusCode() != 204)
				throw new ThingDirectoryException(TMUtils.concat("Request for creating a Thing Description failed, status code: ", statusStr,"; message: ",body));
		} catch (IOException | InterruptedException e) {
			throw new ThingDirectoryException(e.toString());
		} 
	}
	
	/**
	 * This method deletes a Thing Description associated to a {@link FileContainer}
	 * @param id the container id
	 * @throws ThingDirectoryException if an error happened during the deletion
	 */
	public static void deleteTD(String id) throws ThingDirectoryException{
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder(URI.create(formatURI(id))).DELETE().build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			String body = response.body();
			String statusStr = String.valueOf(response.statusCode());
			if (response.statusCode() != 204 & response.statusCode() != 404)
				throw new ThingDirectoryException(TMUtils.concat("Request for deleting a Thing Description failed, status code: ", statusStr,"; message: ",body));
		} catch (IOException | InterruptedException e) {
			throw new ThingDirectoryException(e.toString());
		}
		
	}
}
