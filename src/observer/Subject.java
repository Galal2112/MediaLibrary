package observer;

public interface Subject {
    void register(Observer observer);
    void unregister(Observer observer);
    void notifyObserver();

}
