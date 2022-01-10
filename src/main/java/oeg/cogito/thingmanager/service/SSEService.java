package oeg.cogito.thingmanager.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import ch.rasc.sse.eventbus.SseEvent;
import oeg.cogito.thingmanager.TMUtils;

@Service
@EnableScheduling
public class SSEService {

	 private final ApplicationEventPublisher eventPublisher;
	  public SSEService(ApplicationEventPublisher eventPublisher) {
	    this.eventPublisher = eventPublisher;
	  }

	  public void publishEvent(String id, String order) {
	    SseEvent event =  SseEvent.ofData(TMUtils.concat(order,";",id));
	    this.eventPublisher.publishEvent(event);
	  }
	  
	 
}
