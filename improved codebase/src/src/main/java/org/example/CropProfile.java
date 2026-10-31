
package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CropProfile {
    private final String name;

    private final List<Threshold> thresholds =
            new CopyOnWriteArrayList<>();

    public CropProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addUpperLimit(SensorType sensor, double limit,
                              String label, String device,
                              int water, int energy) {
        thresholds.add(new Threshold(
                sensor,
                ThresholdType.ABOVE,
                limit,
                name + " " + label,
                new ControlAction(device, water, energy)
        ));
    }

    public void addLowerLimit(SensorType sensor, double limit,
                              String label, String device,
                              int water, int energy) {
        thresholds.add(new Threshold(
                sensor,
                ThresholdType.BELOW,
                limit,
                name + " " + label,
                new ControlAction(device, water, energy)
        ));
    }

    public void addRule(Threshold threshold) {
        thresholds.add(threshold);
    }

    public ArrayList<Threshold> findBreaches(
            SensorType sensor, double value) {

        ArrayList<Threshold> breaches = new ArrayList<>();

        for (Threshold threshold : thresholds) {
            if (threshold.isBreached(sensor, value)) {
                breaches.add(threshold);
            }
        }

        return breaches;
    }
}
