package org.example.chamber;

import java.util.List;

public interface ChamberRepository {

    ChamberState getChamberState(String chamber);

    List<String> getChamberNames();

    ChamberSummary getChamberSummary(String chamber);
}
