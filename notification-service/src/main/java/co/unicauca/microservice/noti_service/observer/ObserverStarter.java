package co.unicauca.microservice.noti_service.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class ObserverStarter implements CommandLineRunner {
    @Autowired
    private ApplicationContext context;
    @Override
    public void run(String... args) {
        Subject subject = new Subject();

        Observer emailObserver = new EmailObserver();
        RabbitMQObserver rabbitObserver = context.getBean(RabbitMQObserver.class);
        subject.addObserver(emailObserver);
        subject.addObserver(rabbitObserver);
        subject.notifyObservers("Approved", "PROY-001");
    }
}
