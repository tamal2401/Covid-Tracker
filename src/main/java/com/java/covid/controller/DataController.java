package com.java.covid.controller;

import com.java.covid.metrices.MetricService;
import com.java.covid.metrices.MetricServiceImpl;
import com.java.covid.model.india.CovidAllIndiaDataModel;
import com.java.covid.model.CovidStatModel;
import com.java.covid.model.timeseries.TimeSeriesDataModel;
import com.java.covid.service.CovidDattaCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DataController {

    @Autowired
    CovidDattaCollectorService dataService;

    @Autowired
    MetricService metricService;

    @GetMapping(path = "/welcome/mapping")
    public String visualizePage(Model model) throws IOException {

        List<TimeSeriesDataModel> plotData = dataService.seriesModel;
        /*List<EachDayCountMapping> activeIncidentList = plotData.
                stream()
                .map(each -> new EachDayCountMapping(each.getTotalconfirmed(), each.getDate()))
                .collect(Collectors.toList());*/
        List<Integer> activeIncidentList = plotData.stream().map(each -> each.getTotalconfirmed()).collect(Collectors.toList());
        List<String> dailyDate = plotData.stream().map(each -> each.getDate()).collect(Collectors.toList());
        List<Integer> recoveredList = plotData.stream().map(each -> each.getTotalrecovered()).collect(Collectors.toList());

        model.addAttribute("count", activeIncidentList);
        model.addAttribute("date", dailyDate);
        model.addAttribute("recovered", recoveredList);
        return "visualize_data";
    }

    @GetMapping(path = "/welcome/india")
    public String welcomePage(Model model) throws IOException {
        CovidAllIndiaDataModel allIndiaStats = dataService.getAllIndianStats();
        model.addAttribute("totalCured", formatNumber(allIndiaStats.getTotalRecovered()));
        model.addAttribute("totalConfirmed", formatNumber(allIndiaStats.getTotalConfirmed()));
        model.addAttribute("totalDeath", formatNumber(allIndiaStats.getTotalDeath()));
        model.addAttribute("totalDataOfIndia", allIndiaStats.getStateData());
        model.addAttribute("totalVaccineted", formatNumber(allIndiaStats.getTotalVaccineted()));
        model.addAttribute("lastUpdated", allIndiaStats.getLastUpdated());
        return "indian_data";
    }

    @GetMapping(path = "/welcome/global")
    public String GlobalDataPage(Model model) throws IOException {
        List<CovidStatModel> globalStats = dataService.getGlobaldata();
        long totalCountWorldWide = globalStats.stream().mapToLong(each -> each.getLatestCases()).sum();
        model.addAttribute("allGlobalData", globalStats);
        model.addAttribute("TotalCountWorldWide", formatNumber(totalCountWorldWide));
        return "global_data";
    }

    @GetMapping(path = {"/", "/welcome"})
    public String getIndexPage(Model model) throws IOException {
        List<CovidStatModel> globalStats = dataService.getGlobaldata();
        long totalCountWorldWide = globalStats.stream().mapToLong(each -> each.getLatestCases()).sum();
        model.addAttribute("TotalCountWorldWide", formatNumber(totalCountWorldWide));
        return "home";
    }

    @GetMapping(path = "/metric")
    public ResponseEntity<Map> metric() throws IOException {
        return ResponseEntity.ok().body(metricService.getFullMetric());
    }



    private String formatNumber(long num) {
        if (num > 999 && num < 1000000) {
            return String.valueOf((num / 1000)) + "K +"; // convert to K for number from > 1000 < 1 million
        } else if (num > 1000000) {
            return String.valueOf((num / 1000000)) + "M +"; // convert to M for number from > 1 million
        }
        return String.valueOf(num); // if value < 1000, nothing to do
    }
}
