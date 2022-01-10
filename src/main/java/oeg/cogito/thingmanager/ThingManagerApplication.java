package oeg.cogito.thingmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.rasc.sse.eventbus.config.EnableSseEventBus;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSseEventBus
public class ThingManagerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ThingManagerApplication.class, args);
	}

}
