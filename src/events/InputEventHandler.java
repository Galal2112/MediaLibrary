package events;

import java.util.LinkedList;
import java.util.List;

public class InputEventHandler {
    private List<InputEventListener> listenerList=new LinkedList<>();
    public void add(InputEventListener listener) {
        this.listenerList.add(listener);
    }
    public void remove(InputEventListener listener) {
        this.listenerList.remove(listener);
    }
    public void handle(InputEvent event){
        for (InputEventListener listener : listenerList)
            listener.onInputEvent(event);
    }
}
