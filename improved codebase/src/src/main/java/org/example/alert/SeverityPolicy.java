package org.example.alert;

import org.example.sensor.SensorType;

import java.util.EnumMap;
import java.util.Map;

public class SeverityPolicy {

    private final Map<SensorType, Double> warningLimits =
            new EnumMap<>(SensorType.class);

    private final Map<SensorType, Double> criticalLimits =
            new EnumMap<>(SensorType.class);

    public void setLimits(
            SensorType sensor,
            double warningLimit,
            double criticalLimit
    ) {
        if (warningLimit < 0 || criticalLimit <= warningLimit) {
            throw new IllegalArgumentException("Invalid severity limits");
        }

        warningLimits.put(sensor, warningLimit);
        criticalLimits.put(sensor, criticalLimit);
    }

    public AlertSeverity evaluate(SensorType sensor, double deviation) {
        if (!warningLimits.containsKey(sensor)) {
            throw new IllegalStateException(
                    "Severity limits not configured for " + sensor
            );
        }

        if (deviation >= criticalLimits.get(sensor)) {
            return AlertSeverity.CRITICAL;
        }

        if (deviation >= warningLimits.get(sensor)) {
            return AlertSeverity.WARNING;
        }

        return AlertSeverity.INFO;
    }
}
