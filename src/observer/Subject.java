package observer;

public interface Subject {
    void register(Observer observer);
    void unsubscribe(Observer observer);
    void benachrichtige();

}
