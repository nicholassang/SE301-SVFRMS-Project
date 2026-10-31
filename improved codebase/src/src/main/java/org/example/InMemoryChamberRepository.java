package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryChamberRepository implements ChamberRepository {

    private final HashMap<String, ChamberState> chamberStates =
            new HashMap<>();

    private final HashMap<String, ChamberSummary> chamberSummaries =
            new HashMap<>();

    @Override
    public synchronized ChamberState getChamberState(String chamber) {

        if (!chamberStates.containsKey(chamber)) {
            chamberStates.put(chamber, new ChamberState());
        }

        return chamberStates.get(chamber);
    }

    @Override
    public synchronized List<String> getChamberNames() {

        ArrayList<String> names =
                new ArrayList<>(chamberStates.keySet());

        for (String chamber : chamberSummaries.keySet()) {
            if (!names.contains(chamber)) {
                names.add(chamber);
            }
        }

        Collections.sort(names);

        return names;
    }

    @Override
    public synchronized ChamberSummary getChamberSummary(String chamber) {

        if (!chamberSummaries.containsKey(chamber)) {
            chamberSummaries.put(chamber, new ChamberSummary());
        }

        return chamberSummaries.get(chamber);
    }
}