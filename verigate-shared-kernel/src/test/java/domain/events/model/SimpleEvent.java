package domain.events.model;

public final class SimpleEvent {
    private final String id;
    private final String message;

    public SimpleEvent(final String id, final String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
}
