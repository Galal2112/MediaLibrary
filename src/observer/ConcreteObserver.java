package observer;

public class ConcreteObserver implements Observer {

    private ConcreteObservable concreteObservable;

    private int oldState;

    public ConcreteObserver(ConcreteObservable concreteObservable) {
        this.concreteObservable = concreteObservable;
        this.concreteObservable.register(this);
        this.oldState = this.concreteObservable.getState();
    }

    @Override
    public void updateObserver() {
        int newState = concreteObservable.getState();
        if (newState != this.oldState) {
            System.out.println("neuer Zustand =" + newState);
            this.oldState = newState;
        }
    }
}
