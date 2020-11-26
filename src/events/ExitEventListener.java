package events;

public class ExitEventListener implements InputEventListener {
    @Override
    public void onInputEvent(InputEvent event) {
        if (event.getText().equalsIgnoreCase("exit"))
            System.exit(0);
    }
}
