package org.example;

public class ControlAction {
    private final String device;
    private final int waterCost;
    private final int energyCost;

    public ControlAction(String device, int waterCost, int energyCost) {
        this.device = device;
        this.waterCost = waterCost;
        this.energyCost = energyCost;
    }

    public String getDevice() {
        return device;
    }

    public int getWaterCost() {
        return waterCost;
    }

    public int getEnergyCost() {
        return energyCost;
    }
}