package co.unicauca.microservice.noti_service.observer;

import co.unicauca.microservice.noti_service.rabbit.RabbitPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQObserver implements Observer {
    @Autowired
    private RabbitPublisher rabbitPublisher;
    @Override
    public void update(String newState, String projectId){
        rabbitPublisher.publishProjectStatusChanged(projectId,newState, "Proyecto" + projectId +"cambió a estado:"+ newState);
    }

}
