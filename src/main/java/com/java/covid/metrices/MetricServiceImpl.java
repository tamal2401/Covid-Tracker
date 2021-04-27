package com.java.covid.metrices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MetricServiceImpl implements MetricService {

    private ConcurrentMap<String, ConcurrentHashMap<Integer, Integer>> metricMap;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void increaseCount(String request, int status) {
        ConcurrentHashMap<Integer, Integer> statusMap = metricMap.get(request);
        if (statusMap == null) {
            statusMap = new ConcurrentHashMap<Integer, Integer>();
        }

        Integer count = statusMap.get(status);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        statusMap.put(status, count);
        metricMap.put(request, statusMap);
    }

    @Override
    public Map<String, ConcurrentHashMap<Integer, Integer>> getFullMetric() {
        return metricMap;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        metricMap = new ConcurrentHashMap<>();
        try {
            metricMap = this.mapper.readValue(ResourceUtils.getFile(
                    "classpath:metric/metrics.json"), ConcurrentHashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
