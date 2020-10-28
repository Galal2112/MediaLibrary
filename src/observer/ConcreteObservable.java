package observer;

import java.util.LinkedList;
import java.util.List;

public class ConcreteObservable implements Observable {

    private final List<Observer> observerList = new LinkedList<>();

    @Override
    public void register(Observer observer) {
        this.observerList.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        this.observerList.remove(observer);
    }

    @Override
    public void benachrichtige() {
        for(Observer b:this.observerList) b.updateObserver();

    }
    private int state;
    public int getState() { return state; }
    public void setState(int state){
        this.state=state;
        this.benachrichtige();
    }
}
