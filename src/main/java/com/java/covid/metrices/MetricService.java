package com.java.covid.metrices;

import java.util.Map;

public interface MetricService {

    void increaseCount(String request, int status);

    Map getFullMetric();

}
