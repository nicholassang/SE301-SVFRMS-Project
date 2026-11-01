package org.example.alert;

import org.example.sensor.SensorType;
import org.example.sensor.SeverityLimits;

// each sensor carries its own limits (SensorType), so there is nothing to configure here
// and no state at all: every chamber thread can call evaluate() without locking
public class SeverityPolicy {

    public AlertSeverity evaluate(SensorType sensor, double deviation) {
        SeverityLimits limits = sensor.getSeverity();

        if (deviation >= limits.critical()) {
            return AlertSeverity.CRITICAL;
        }

        if (deviation >= limits.warning()) {
            return AlertSeverity.WARNING;
        }

        return AlertSeverity.INFO;
    }
}
