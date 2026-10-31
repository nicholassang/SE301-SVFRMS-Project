package org.example;

// one device request sent from a chamber thread to the control thread
// record = immutable, so both threads can read it safely without locking
public record ControlRequest(String chamber, String device) {

    // reject bad requests where they are created, instead of crashing the control thread later
    public ControlRequest {
        if (chamber == null || chamber.isBlank()) {
            throw new IllegalArgumentException("chamber must not be empty");
        }
        if (device == null || device.isBlank()) {
            throw new IllegalArgumentException("device must not be empty");
        }
    }
}
