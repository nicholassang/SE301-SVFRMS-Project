package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//solve race condition of threads calling getChamberState and getChamberSummary at the same time
public class ChamberRepository {
    private HashMap<String, ChamberState> chamberStates = new HashMap<>();
    private HashMap<String, ChamberSummary> chamberSummaries = new HashMap<>();

    // synchronized for checking and create into one step
    public synchronized ChamberState getChamberState(String chamber){
        if(!chamberStates.containsKey(chamber)){
            chamberStates.put(chamber, new ChamberState());
        }
        return chamberStates.get(chamber);
    }

    public synchronized ChamberSummary getChamberSummary(String chamber){
        if(!chamberSummaries.containsKey(chamber)){
            chamberSummaries.put(chamber, new ChamberSummary());
        }
        return chamberSummaries.get(chamber);
    }

    public synchronized ArrayList<String> getChamberNames(){
        ArrayList<String> names = new ArrayList<>(chamberSummaries.keySet());
        Collections.sort(names);
        return names;
    }
}
