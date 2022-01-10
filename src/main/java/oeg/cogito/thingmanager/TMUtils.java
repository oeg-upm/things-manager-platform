package oeg.cogito.thingmanager;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import oeg.cogito.thingmanager.controller.exceptions.UnsupportedFormatException;

public class TMUtils {

	// -- Attributes

	private static List<String> allowedFormats = new ArrayList<>();
	private static ObjectMapper mapper = new ObjectMapper();
	private static final Gson GSON = new Gson();
	private static List<String> enrichmentFormats = new ArrayList<>();
	
	static {
		allowedFormats.add("ifc");
	
		enrichmentFormats.add("ttl");
		enrichmentFormats.add("jpg");
		enrichmentFormats.add("png");
		enrichmentFormats.add("pdf");
	}
	// --Constructor
	
	private TMUtils() {
		super();
	}
	
	// -- Methods
	
	public static String concat(String ... args) {
		StringBuilder builder = new StringBuilder();
		for(int index=0; index < args.length; index++)
			builder.append(args[index]);
		return builder.toString();
	}
	
	public static void checkFormat(String format) throws UnsupportedFormatException {
		if(!allowedFormats.contains(format.toLowerCase()))
			throw new UnsupportedFormatException("Provided format is not supported by the platform. Please provide one of: "+allowedFormats.toString());
	}
	
	public static void checkMetaFormat(String format) throws UnsupportedFormatException {
		if(!enrichmentFormats.contains(format))
			throw new UnsupportedFormatException("Provided format is not supported by the platform for meta files. Please provide one of: "+enrichmentFormats.toString());
	}
	
	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * This method instantiates a thing description as a {@link JsonObject}
	 * @param td a {@link String} representation of the thing description
	 * @return the {@link JsonObject} representation of the thing description
	 */
	public static JsonObject toJson(String td) {
		  return GSON.fromJson(td, JsonObject.class);
	}
	
}
