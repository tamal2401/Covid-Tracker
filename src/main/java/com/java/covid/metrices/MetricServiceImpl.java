package com.java.covid.metrices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MetricServiceImpl implements MetricService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private ConcurrentMap<String, ConcurrentHashMap<Integer, Integer>> metricMap;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void increaseCount(String request, int status) {
        ConcurrentHashMap<Integer, Integer> statusMap = metricMap.get(request);
        if (statusMap == null) {
            statusMap = new ConcurrentHashMap<>();
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
                    "src/main/resources/metric/metrics.json"), ConcurrentHashMap.class);
        } catch (IOException e) {
            log.error("Error occured while reading application metrics.json file. Termination application start up");
            e.printStackTrace();
            System.exit(11);
        }
    }
}
