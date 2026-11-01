package org.example.sensor;

// how far past a threshold a reading must be before its alert is WARNING or CRITICAL
// e.g. new SeverityLimits(3, 7) for temperature: 3C over is WARNING, 7C over is CRITICAL
// a record is immutable, so every chamber thread can share it without locking
public record SeverityLimits(double warning, double critical) {

    public SeverityLimits {
        if (warning < 0 || critical <= warning) {
            throw new IllegalArgumentException("Invalid severity limits");
        }
    }
}
