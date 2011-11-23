package de.gmorling.jxbinding;

public class BindingViolation {

    private final String label;

    private final String message;

    public BindingViolation(String message) {
        this( null, message );
    }

    public BindingViolation(String label, String message) {
        this.label = label;
        this.message = message;
    }

    public String getLabel() {
        return label;
    }

    public String getMessage() {
        return message;
    }

}
