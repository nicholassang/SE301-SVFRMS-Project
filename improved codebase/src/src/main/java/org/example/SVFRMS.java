package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import java.util.logging.Logger;

public class SVFRMS {
    private static final Logger logger = AppLogger.getLogger();

    public static ArrayList<String> sensorLogs = new ArrayList<>();
    public static ArrayList<String> reports = new ArrayList<>();
    public static ArrayList<String> controlQueue = new ArrayList<>();
    public static int controlQueueSize = 5;
    public static volatile boolean controlThreadRunning = true;


    public static ResourceUsageTracker usage = new ResourceUsageTracker();

    public static Object reportLock = new Object();
    public static Object waterPumpLock = new Object();
    public static Object nutrientLock = new Object();

    static Random random = new Random();


    public static ChamberRepository chambers = new ChamberRepository();

    public static void handleEvent(String type, String chamber, String crop, String message){
        if(type.equals("LOG")){
            sensorLogs.add(new Date()+" : "+message);
        }
        else if(type.equals("ALERT_LOG")){
            logger.info("[" + chamber + "][" + crop + "][ALERT] " + message);
        }
        else if(type.equals("CONTROL_LOG")){
            logger.info("\t\u001B[32m[" + chamber + "][CONTROL] " + message + "\u001B[0m");
        }
        else if(type.equals("CREATE_ALERT")){
            AlertManager.addAlert(message);
            logger.info("[" + chamber + "][" + crop + "][ALERT] " + message);
        }
        else if(type.equals("TRACK_ALERT")){
            chambers.getChamberSummary(chamber).recordAlert(message);
        }
        else if(type.equals("TRACK_ACTION")){
            chambers.getChamberSummary(chamber).recordAction(message);
        }
    }

    public static void activateControl(String chamber, String control){
        ChamberState state = chambers.getChamberState(chamber);

            if(control.equals("Cooling Fan")){
                double newTemp = state.adjust("Temperature", -5, 18, Double.MAX_VALUE);
                handleEvent("TRACK_ACTION", chamber, "", "Cooling Fan Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Cooling Fan Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Temperature reduced to " + state.temperature + "C");
            }
            else if(control.equals("Water Pump")){
                double newSoil = state.adjust("Soil", 20, -Double.MAX_VALUE, 100);
                handleEvent("TRACK_ACTION", chamber, "", "Water Pump Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Water Pump Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Soil moisture increased to " + state.soilMoisture + "%");
            }
            else if(control.equals("Nutrient Injector")){
                double newNutrient = state.adjust("Nutrient", 15, -Double.MAX_VALUE, 100);                handleEvent("TRACK_ACTION", chamber, "", "Nutrient Injector Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Nutrient Injector Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Nutrient level increased to " + state.nutrientLevel + "%");
            }
            else if(control.equals("Humidifier")){
                double newHumidity = state.adjust("Humidity", 10, -Double.MAX_VALUE, 100);
                handleEvent("TRACK_ACTION", chamber, "", "Humidifier Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Humidifier Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Humidity increased to " + state.humidity + "%");
            }
            else if(control.equals("Dehumidifier")){
                double newHumidity = state.adjust("Humidity", -10, 30, Double.MAX_VALUE);
                handleEvent("TRACK_ACTION", chamber, "", "Dehumidifier Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Dehumidifier Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Humidity reduced to " + state.humidity + "%");
            }
            else if(control.equals("Grow Light")){
                double newLight = state.adjust("Light", 150, -Double.MAX_VALUE, 1000);
                handleEvent("TRACK_ACTION", chamber, "", "Grow Light Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Grow Light Activated");
                handleEvent("CONTROL_LOG", chamber, "", "Light level increased to " + state.lightLevel + " lux");
            }
    }

    public static void addControlRequest(String chamber, String control){
        synchronized(controlQueue){
            while(controlQueue.size() == controlQueueSize){
                try{controlQueue.wait();}catch(Exception e){}
            }
            controlQueue.add(chamber + "|" + control);
            controlQueue.notifyAll();
        }
    }

    public static void processSensor(String chamber,String crop,String sensor,double value){
        handleEvent("LOG", "", "", "Processing "+crop+" "+sensor+" = "+value);
        ChamberState state = chambers.getChamberState(chamber);
        state.setState(sensor, value);

        if(sensor.equals("Temperature")){
            if(crop.equals("Tomato")){
                if(value>28){handleEvent("TRACK_ALERT", chamber, "", chamber+" Tomato High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Tomato High Temp"); usage.addEnergy(10); addControlRequest(chamber, "Cooling Fan");}
                if(value<22){handleEvent("TRACK_ALERT", chamber, "", chamber+" Tomato Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Tomato Low Temp"); usage.addEnergy(15);}
            }
            else if(crop.equals("Strawberry")){
                if(value>24){handleEvent("TRACK_ALERT", chamber, "", chamber+" Strawberry High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Strawberry High Temp"); usage.addEnergy(10); addControlRequest(chamber, "Cooling Fan");}
                if(value<18){handleEvent("TRACK_ALERT", chamber, "", chamber+" Strawberry Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Strawberry Low Temp"); usage.addEnergy(15);}
            }
            else if(crop.equals("Lettuce")){
                if(value>21){handleEvent("TRACK_ALERT", chamber, "", chamber+" Lettuce High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Lettuce High Temp"); addControlRequest(chamber, "Cooling Fan");}
                if(value<15){handleEvent("TRACK_ALERT", chamber, "", chamber+" Lettuce Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Lettuce Low Temp");}
            }
            else if(crop.equals("Spinach")){
                if(value>20){handleEvent("TRACK_ALERT", chamber, "", chamber+" Spinach High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Spinach High Temp"); addControlRequest(chamber, "Cooling Fan");}
                if(value<10){handleEvent("TRACK_ALERT", chamber, "", chamber+" Spinach Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Spinach Low Temp");}
            }
            else if(crop.equals("Chili")){
                if(value>32){handleEvent("TRACK_ALERT", chamber, "", chamber+" Chili High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Chili High Temp"); addControlRequest(chamber, "Cooling Fan");}
                if(value<24){handleEvent("TRACK_ALERT", chamber, "", chamber+" Chili Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Chili Low Temp");}
            }
            else if(crop.equals("Cucumber")){
                if(value>27){handleEvent("TRACK_ALERT", chamber, "", chamber+" Cucumber High Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Cucumber High Temp"); addControlRequest(chamber, "Cooling Fan");}
                if(value<18){handleEvent("TRACK_ALERT", chamber, "", chamber+" Cucumber Low Temp"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Cucumber Low Temp");}
            }
        }
        else if(sensor.equals("Humidity")){
            if(crop.equals("Tomato")){
                if(value>80){handleEvent("TRACK_ALERT", chamber, "", chamber+" Tomato High Humidity"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Tomato High Humidity"); addControlRequest(chamber, "Dehumidifier");}
                if(value<60){handleEvent("TRACK_ALERT", chamber, "", chamber+" Tomato Low Humidity"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Tomato Low Humidity"); usage.addWater(20); addControlRequest(chamber, "Humidifier");}
            }
            else if(crop.equals("Strawberry")){
                if(value>85){handleEvent("TRACK_ALERT", chamber, "", chamber+" Strawberry High Humidity"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Strawberry High Humidity"); addControlRequest(chamber, "Dehumidifier");}
                if(value<70){handleEvent("TRACK_ALERT", chamber, "", chamber+" Strawberry Low Humidity"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Strawberry Low Humidity"); usage.addWater(25); addControlRequest(chamber, "Humidifier");}
            }
        }
        else if(sensor.equals("Soil")){
            if(crop.equals("Tomato")){
                if(value<45){handleEvent("TRACK_ALERT", chamber, "", chamber+" Tomato Low Soil Moisture"); handleEvent("CREATE_ALERT", chamber, crop, chamber+" Tomato Low Soil Moisture"); usage.addWater(50); addControlRequest(chamber, "Water Pump");}
            }
        }
        else if(sensor.equals("Light")){
            if(value<500){
                handleEvent("TRACK_ALERT", chamber, "", chamber+" Low Light Level");
                handleEvent("CREATE_ALERT", chamber, crop, chamber+" Low Light Level");
                addControlRequest(chamber, "Grow Light");
            }
        }
        else if(sensor.equals("Nutrient")){
            if(value<40){
                handleEvent("TRACK_ALERT", chamber, "", chamber+" Low Nutrient Level");
                handleEvent("CREATE_ALERT", chamber, crop, chamber+" Low Nutrient Level");
                addControlRequest(chamber, "Nutrient Injector");
            }
        }
    }

    public static void irrigationCycle(){
        totalWaterUsage += 100;
    }

    public static void feedingCycle(){
        totalEnergyUsage += 20;
    }

    static class ControlThread extends Thread{
        public void run(){
            while(true){
                String request;

                synchronized(controlQueue){
                    while(controlQueue.isEmpty() && controlThreadRunning){
                        try{controlQueue.wait();}catch(Exception e){}
                    }

                    if(controlQueue.isEmpty() && !controlThreadRunning){
                        break;
                    }

                    request = controlQueue.remove(0);
                    controlQueue.notifyAll();
                }

                String[] parts = request.split("\\|");
                activateControl(parts[0], parts[1]);
            }
        }
    }

    static class ChamberThread extends Thread{
        String chamber;
        String crop;

        ChamberThread(String chamber,String crop){
            this.chamber=chamber;
            this.crop=crop;
        }

        public void run(){
            for(int i=0;i<20;i++){
                processSensor(chamber,crop,"Temperature",10+random.nextInt(30));
                processSensor(chamber,crop,"Humidity",40+random.nextInt(60));
                processSensor(chamber,crop,"Soil",20+random.nextInt(60));
                processSensor(chamber,crop,"Light",300+random.nextInt(900));
                processSensor(chamber,crop,"Nutrient",20+random.nextInt(80));
                usage.addWater(2);
                usage.addEnergy(1);
            }
        }
    }

    public static void showAlerts(){
        for(String a: AlertManager.getActiveAlerts()){
            logger.info(a);
        }
    }

    public static void showLogs(){
        for(String s: sensorLogs){
            logger.info(s);
        }
    }

    public static void main(String[] args) throws Exception {
        Thread activateControlThread = new ControlThread();
        Thread t1 = new ChamberThread("CH1","Tomato");
        Thread t2 = new ChamberThread("CH2","Strawberry");
        Thread t3 = new ChamberThread("CH3","Lettuce");
        Thread t4 = new ChamberThread("CH4","Chili");

        activateControlThread.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        synchronized(controlQueue){
            controlThreadRunning = false;
            controlQueue.notifyAll();
        }
        activateControlThread.join();

        ArchiveManager.archiveAlerts();
        ReportManager.generateDailyReport();

        logger.info("System finished sensor processing.");
        ReportManager.saveArchiveReport();
    }
}
