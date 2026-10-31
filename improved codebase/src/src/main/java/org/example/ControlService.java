package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// replace the activateControl in SVFRMS
public class ControlService {
    // the control thread reads this on every request, addDevice() may write at any time
    private final Map<String, Device> devices = new ConcurrentHashMap<>();
    private final ChamberRepository chambers;
    private final EventRecorder events;

    public ControlService(ChamberRepository chambers, EventRecorder events){
        this.chambers = chambers;
        this.events = events;
        loadDefaultDevices();
    }

    public void addDevice(Device device){
        devices.put(device.getName(), device);
    }

    public void activateControl(String chamber, String deviceName){
        Device device = devices.get(deviceName);
        if (device == null) {
            return;   // ignored unknown devices
        }
        String result = device.activate(chambers.getChamberState(chamber));
        events.recordAction(chamber, deviceName + " Activated", result);
    }

    private void loadDefaultDevices(){
        double NO_MIN = -Double.MAX_VALUE;
        double NO_MAX = Double.MAX_VALUE;
        addDevice(new Device("Cooling Fan", SensorType.TEMPERATURE, -5, 18, NO_MAX, "Temperature reduced to ", "C"));
        addDevice(new Device("Water Pump", SensorType.SOIL,20, NO_MIN, 100, "Soil moisture increased to ", "%"));
        addDevice(new Device("Nutrient Injector", SensorType.NUTRIENT, 15, NO_MIN, 100, "Nutrient level increased to ", "%"));
        addDevice(new Device("Humidifier", SensorType.HUMIDITY, 10, NO_MIN, 100, "Humidity increased to ", "%"));
        addDevice(new Device("Dehumidifier", SensorType.HUMIDITY, -10, 30, NO_MAX, "Humidity reduced to ", "%"));
        addDevice(new Device("Grow Light", SensorType.LIGHT, 150, NO_MIN, 1000, "Light level increased to ", " lux"));
    }

}
