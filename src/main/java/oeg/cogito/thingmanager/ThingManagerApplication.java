package oeg.cogito.thingmanager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.JsonObject;

import ch.rasc.sse.eventbus.config.EnableSseEventBus;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSseEventBus
public class ThingManagerApplication {
	
	private static final String CONFIG_FILE = "./tm-config.json";
	private static final String CONFIG_TDD_KEY = "wot directory";
	private static final String CONFIG_TSTORE_KEY = "triple store";
	private static final String CONFIG_HOST_KEY = "host";
	
	public static void main(String[] args) {
		SpringApplication.run(ThingManagerApplication.class, args);
		File config = new File(CONFIG_FILE);
		if(!config.exists()) {
			System.out.println("No configuration file found! providing a default one ");
			try {
				FileUtils.writeByteArrayToFile(config, errorMessage().replace("'", "").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		try{
			 String text = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)), StandardCharsets.UTF_8);
			 JsonObject json = TMUtils.toJson(text);
			 if(!json.has(CONFIG_TDD_KEY)) {
				System.out.println("Missing '"+CONFIG_TDD_KEY+"' mandatory key in 'tm-config.json', please provide a json similar to: ");
				System.out.println(errorMessage());
				System.exit(-1); 
			 }
			 if(!json.has(CONFIG_TSTORE_KEY)) {
				System.out.println("Missing '"+CONFIG_TSTORE_KEY+"' mandatory key in 'tm-config.json', please provide a json similar to: ");
				System.out.println(errorMessage());
				System.exit(-1);
			 }
			 if(!json.has(CONFIG_HOST_KEY)) {
					System.out.println("Missing '"+CONFIG_HOST_KEY+"' mandatory key in 'tm-config.json', please provide a json similar to: ");
					System.out.println(errorMessage());
					System.exit(-1);
				 }
			 TMConfiguration.triplestore = json.get(CONFIG_TSTORE_KEY).getAsString();
			 TMConfiguration.WoTHive = json.get(CONFIG_TDD_KEY).getAsString();
			 TMConfiguration.currentHost = json.get(CONFIG_HOST_KEY).getAsString();
			 System.out.println("----| Configuration: wot directory is "+TMConfiguration.WoTHive);
			 System.out.println("----| Configuration: triple store is "+TMConfiguration.triplestore);
			 System.out.println("----| Configuration: host is "+TMConfiguration.currentHost);

		}catch(Exception e) {
			System.out.println(e.toString());
			System.exit(-1);
		}

	}
	
	private static String errorMessage() {
		return "'{ "
				+ "\n  \"wot directory\" : \"https://wothive.linkeddata.es\", "
				+ "\n  \"triple store\" : \"http://localhost:8890/sparql\","
				+ "\n  \"host\" : \"http://localhost:8080\""
				+ "\n  }' \n";
	}

}
