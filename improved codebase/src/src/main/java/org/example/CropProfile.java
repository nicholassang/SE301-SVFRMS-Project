package org.example;

import java.util.ArrayList;

// Crops' profile plus its lists of rules from Threshold
public class CropProfile {
    private String name;
    private ThresholdRules rules = new ThresholdRules();

    public CropProfile(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    // add rules when the reading goes 'above' the limit
    public void addAbove(SensorType sensor, double limit,
                        String label, String device, int water, int energy) {
        rules.addRule(new Threshold(
            sensor, ThresholdType.ABOVE, limit,
            name + " " + label, device, water, energy
        ));
    }

    // add rules when the reading goes 'below' the limit
    public void addBelow(SensorType sensor, double limit,
                        String label, String device, int water, int energy) {
        rules.addRule(new Threshold(
            sensor, ThresholdType.BELOW, limit,
            name + " " + label, device, water, energy
        ));
    }

    // Used for the rules that has no crop name
    public void addRule(Threshold t) {
        rules.addRule(t);
    }

    // return every rule it breaks when reading one sensor , ex. Tomato
    public java.util.ArrayList<Threshold> getBreaches(SensorType sensor, double value) {
        return rules.getBreaches(sensor, value);
    }
}
