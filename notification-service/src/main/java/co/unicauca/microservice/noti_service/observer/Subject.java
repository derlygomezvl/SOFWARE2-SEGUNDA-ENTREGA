package co.unicauca.microservice.noti_service.observer;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer observer){
        observers.add(observer);
    }
    public void notifyObservers(String newState, String projectId) {
        System.out.println("[Subject] Notifying state change: " + newState + " for project " + projectId);
        for (Observer obs : observers) {
            obs.update(newState, projectId);
        }
    }
}
