package org.example.control;

import org.example.chamber.ChamberState;

import java.util.ArrayList;
import java.util.List;

// a named device and the readings it moves when activated
// (Cooling Fan has 1 effect; Ventilation would have 2: temperature and humidity)
public class Device {
    // final + List.of (unmodifiable) + immutable DeviceEffect records:
    // a Device never changes after creation, so threads can share it without locking
    private final String name;                  // e.g. "Cooling Fan"
    private final List<DeviceEffect> effects;

    public Device(String name, DeviceEffect... effects){
        if (effects.length == 0) {
            throw new IllegalArgumentException(name + " must have at least one effect");
        }
        this.name = name;
        this.effects = List.of(effects);
    }

    public String getName(){
        return name;
    }


    // changes the chamber and returns the result message
    public String activate(ChamberState state){
        List<String> messages = new ArrayList<>();

        // hold the chamber's lock for all effects, so no thread sees e.g. temperature changed but humidity not yet
        // (ChamberState's own methods lock the same object; Java locks are reentrant, so adjust() doesn't block here)
        synchronized (state) {
            for (DeviceEffect e : effects) {
                double newValue = state.adjust(e.sensor(), e.change(), e.min(), e.max());
                messages.add(e.messageStart() + newValue + e.unit());
            }
        }

        return String.join(", ", messages);
    }


}
