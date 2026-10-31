package org.example;

import java.util.HashMap;

// where all the crops is with adding and removing new crops
public class CropRegister {
    private final HashMap<String, CropProfile> crops = new HashMap<>();
    private final FacilityRules facilityRules = new FacilityRules();

    public CropRegister(){
        loadDefaultCrops();
    }

    public synchronized void addCrop(CropProfile crop){
        crops.put(crop.getName(), crop);
    }

    public synchronized void removeCrop(String name){
        crops.remove(name);
    }

    public synchronized CropProfile getCrop(String name) {
        CropProfile crop = crops.get(name);

        if (crop == null) {
            throw new IllegalArgumentException(
                "Unknown crop: " + name
            );
        }

        return crop;
    }


    public FacilityRules getFacilityRules() {
        return facilityRules;
    }

    // fix the giant if-else statement (SOLID --> Open Close, code smell --> DRY)
    private void loadDefaultCrops(){
        CropProfile tomato = new CropProfile("Tomato");
        tomato.addUpperLimit(SensorType.TEMPERATURE, 28, "High Temp", "Cooling Fan", 0, 10);
        tomato.addLowerLimit(SensorType.TEMPERATURE, 22, "Low Temp", null, 0, 15);
        tomato.addUpperLimit(SensorType.HUMIDITY, 80, "High Humidity", "Dehumidifier", 0, 0);
        tomato.addLowerLimit(SensorType.HUMIDITY, 60, "Low Humidity", "Humidifier", 20, 0);
        tomato.addLowerLimit(SensorType.SOIL, 45, "Low Soil Moisture", "Water Pump", 50, 0);
        addCrop(tomato);

        CropProfile strawberry = new CropProfile("Strawberry");
        strawberry.addUpperLimit(SensorType.TEMPERATURE, 24, "High Temp", "Cooling Fan", 0, 10);
        strawberry.addLowerLimit(SensorType.TEMPERATURE, 18, "Low Temp", null, 0, 15);
        strawberry.addUpperLimit(SensorType.HUMIDITY, 85, "High Humidity", "Dehumidifier", 0, 0);
        strawberry.addLowerLimit(SensorType.HUMIDITY, 70, "Low Humidity", "Humidifier", 25, 0);
        addCrop(strawberry);

        CropProfile lettuce = new CropProfile("Lettuce");
        lettuce.addUpperLimit(SensorType.TEMPERATURE, 21, "High Temp", "Cooling Fan", 0, 0);
        lettuce.addLowerLimit(SensorType.TEMPERATURE, 15, "Low Temp", null, 0, 0);
        addCrop(lettuce);

        CropProfile spinach = new CropProfile("Spinach");
        spinach.addUpperLimit(SensorType.TEMPERATURE, 20, "High Temp", "Cooling Fan", 0, 0);
        spinach.addLowerLimit(SensorType.TEMPERATURE, 10, "Low Temp", null, 0, 0);
        addCrop(spinach);

        CropProfile chili = new CropProfile("Chili");
        chili.addUpperLimit(SensorType.TEMPERATURE, 32, "High Temp", "Cooling Fan", 0, 0);
        chili.addLowerLimit(SensorType.TEMPERATURE, 24, "Low Temp", null, 0, 0);
        addCrop(chili);

        CropProfile cucumber = new CropProfile("Cucumber");
        cucumber.addUpperLimit(SensorType.TEMPERATURE, 27, "High Temp", "Cooling Fan", 0, 0);
        cucumber.addLowerLimit(SensorType.TEMPERATURE, 18, "Low Temp", null, 0, 0);
        addCrop(cucumber);

        // these 2 rules applies to every crops
        facilityRules.addRule(new Threshold(
            SensorType.LIGHT,
            ThresholdType.BELOW,
            500,
            "Low Light Level",
            new ControlAction("Grow Light", 0, 0)
        ));

        facilityRules.addRule(new Threshold(
            SensorType.NUTRIENT,
            ThresholdType.BELOW,
            40,
            "Low Nutrient Level",
            new ControlAction("Nutrient Injector", 0, 0)
        ));
    }
}
