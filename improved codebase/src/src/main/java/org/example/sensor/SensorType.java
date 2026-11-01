package org.example.sensor;

// every sensor and the facts about it, in one place:
// adding a sensor is one line here (plus any crop rules that use it)
public enum SensorType {
    TEMPERATURE(24.0,  new ReadingRange(10, 40),    new SeverityLimits(3, 7)),
    HUMIDITY   (70.0,  new ReadingRange(40, 100),   new SeverityLimits(5, 15)),
    SOIL       (60.0,  new ReadingRange(20, 80),    new SeverityLimits(5, 15)),
    LIGHT      (600.0, new ReadingRange(300, 1200), new SeverityLimits(100, 300)),
    NUTRIENT   (70.0,  new ReadingRange(20, 100),   new SeverityLimits(5, 15));

    // all final, and the JVM creates each constant once: threads can share them without locking
    private final double defaultValue;          // a chamber's starting reading
    private final ReadingRange simulatedRange;  // what the simulator can produce
    private final SeverityLimits severity;      // deviation that makes an alert WARNING / CRITICAL

    SensorType(double defaultValue, ReadingRange simulatedRange, SeverityLimits severity) {
        this.defaultValue = defaultValue;
        this.simulatedRange = simulatedRange;
        this.severity = severity;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public ReadingRange getSimulatedRange() {
        return simulatedRange;
    }

    public SeverityLimits getSeverity() {
        return severity;
    }
}
