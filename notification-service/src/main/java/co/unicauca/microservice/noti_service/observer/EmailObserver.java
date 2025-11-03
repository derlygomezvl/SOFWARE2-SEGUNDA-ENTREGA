package co.unicauca.microservice.noti_service.observer;

public class EmailObserver implements Observer{
    @Override
    public void update(String newState, String projectId){
        System.out.println("Simulando email: Proyecto" + projectId + "cambió a:" + newState);
    }
}
