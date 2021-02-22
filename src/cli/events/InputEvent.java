package cli.events;

import java.util.EventObject;

public class InputEvent extends EventObject {
    private String text;

    public InputEvent(Object source, String text) {
        super(source);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
