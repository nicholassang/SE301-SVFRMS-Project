package org.example;

import java.util.List;

public interface ChamberRepository {

    ChamberState getChamberState(String chamber);

    List<String> getChamberNames();

    ChamberSummary getChamberSummary(String chamber);
}