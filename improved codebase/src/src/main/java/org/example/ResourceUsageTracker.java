package org.example;

// solve race condition of threads modifying total usage of the resource
public class ResourceUsageTracker {
    private int totalWaterUsage = 0;
    private int totalEnergyUsage = 0;

    public synchronized int getTotalWater(){ 
        return totalWaterUsage; 
    }

    public synchronized int getTotalEnergy(){
        return totalEnergyUsage;
    }

    public synchronized void addWater(int liters){
        totalWaterUsage += liters;
    }

    public synchronized void addEnergy(int energy){
        totalEnergyUsage += energy;
    }


}
