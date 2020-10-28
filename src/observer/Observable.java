package observer;

public interface Observable {
    void register(Observer observer);
    void unsubscribe(Observer observer);
    void benachrichtige();

}
