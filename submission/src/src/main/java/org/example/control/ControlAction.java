package org.example.control;

public class ControlAction {

    private final String device;
    private final int waterCost;
    private final int energyCost;
    private final int nutrientCost;

    public ControlAction(
            String device,
            int waterCost,
            int energyCost,
            int nutrientCost) {

        this.device = device;
        this.waterCost = waterCost;
        this.energyCost = energyCost;
        this.nutrientCost = nutrientCost;
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

    public int getNutrientCost() {
        return nutrientCost;
    }
}
