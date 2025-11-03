package co.unicauca.microservice.noti_service.observer;

public interface Observer {
    void update(String newState, String projectId);
}
