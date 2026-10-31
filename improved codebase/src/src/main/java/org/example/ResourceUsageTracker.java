package org.example;

public class ResourceUsageTracker {

    private int totalWaterUsage = 0;
    private int totalEnergyUsage = 0;
    private int totalNutrientUsage = 0;

    public synchronized int getTotalWater() {
        return totalWaterUsage;
    }

    public synchronized int getTotalEnergy() {
        return totalEnergyUsage;
    }

    public synchronized int getTotalNutrient() {
        return totalNutrientUsage;
    }

    public synchronized void addWater(int liters) {
        totalWaterUsage += liters;
    }

    public synchronized void addEnergy(int energy) {
        totalEnergyUsage += energy;
    }

    public synchronized void addNutrient(int amount) {
        totalNutrientUsage += amount;
    }
}