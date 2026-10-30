package org.example;

import java.util.ArrayList;

// Crops' profile plus its lists of rules from Threshold
public class CropProfile {
    private String name;
    private ArrayList<Threshold> thresholds = new ArrayList<>();

    public CropProfile(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    // add rules when the reading goes 'above' the limit
    public void addAbove(String sensor, double limit, String label, String device, int water, int energy) {
        thresholds.add(new Threshold(sensor, true, limit, name + " " + label, device, water, energy));
    }

    // add rules when the reading goes 'below' the limit
    public void addBelow(String sensor, double limit, String label, String device, int water, int energy) {
        thresholds.add(new Threshold(sensor, false, limit, name + " " + label, device, water, energy));
    }

    // Used for the rules that has no crop name
    public void addRule(Threshold t) {
        thresholds.add(t);
    }

    // return every rule it breaks when reading one sensor , ex. Tomato
    public ArrayList<Threshold> getBreaches(String sensor, double value) {
        ArrayList<Threshold> result = new ArrayList<>();
        for (Threshold t : thresholds) {
            if (t.isBreached(sensor, value)) {
                result.add(t);
            }
        }
        return result;
    }

}
