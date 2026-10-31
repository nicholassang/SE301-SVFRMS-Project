package org.example.control;

// Immutable request passed from a chamber thread to the control thread.
public record ControlRequest(String chamber, ControlAction action) {

    public ControlRequest {
        if (chamber == null || chamber.isBlank()) {
            throw new IllegalArgumentException("chamber must not be empty");
        }

        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
    }
}
