package oeg.cogito.thingmanager.controller;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ch.rasc.sse.eventbus.SseEvent;
import ch.rasc.sse.eventbus.SseEventBus;


@RestController
public class SSEController {
	
	private final SseEventBus eventBus;
	  public  SSEController(SseEventBus eventBus) {
	    this.eventBus = eventBus;
	  }

	  @GetMapping("/sse")
	  public SseEmitter register() {
		  String id = UUID.randomUUID().toString();
	    return this.eventBus.createSseEmitter(id, SseEvent.DEFAULT_EVENT);
	  }

}
