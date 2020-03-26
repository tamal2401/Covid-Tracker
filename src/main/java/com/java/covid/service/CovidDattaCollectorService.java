package com.java.covid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.java.covid.model.CovidAllIndiaDataModel;
import com.java.covid.model.CovidDataPerState;
import com.java.covid.model.CovidStatModel;
import com.java.covid.model.timeseries.TimeSeriesDataModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CovidDattaCollectorService {

    private static final String TIME_SERIES_DATA_URL = "https://api.covid19india.org/data.json";
    private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private String INDIAN_DATA_URL = "https://www.mohfw.gov.in/";

    List<CovidStatModel> allStats = new ArrayList<>();
    CovidAllIndiaDataModel consolidatedDataOfIndia = new CovidAllIndiaDataModel();
    List<TimeSeriesDataModel> seriesModel = new ArrayList<>();

    public List<CovidStatModel> getGlobalStats() {
        return allStats;
    }

    //@Async("myExecutor")
    @Scheduled(cron = "0 0/4 * * * *")
    //@Bean
    public void getCovidData() throws IOException {
        List<CovidStatModel> tempStats = new ArrayList<>();
        RestTemplate template = new RestTemplate();
        String response = template.getForObject(DATA_URL, String.class);
        Reader reader = new StringReader(response);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withNullString("").parse(reader);
        records.forEach(record -> {
            CovidStatModel model = new CovidStatModel();
            model.setState(record.get("Province/State"));
            model.setCountry(record.get("Country/Region"));
            model.setLatestCases(getCellValue(record, 1));
            model.setChangeInCasualties(getCellValue(record, 1) - getCellValue(record, 2));
            tempStats.add(model);
        });
        List<CovidStatModel> collectionObj = sortDataCountryWise(tempStats);
        this.allStats = collectionObj;
        System.out.println("global data fetched");
    }

    private int getCellValue(CSVRecord record, int lastCellIndex) {
        int countt = 0;
        String cellValue = record.get(record.size() - lastCellIndex);
        if (null == cellValue) {
            cellValue = "0";
        }
        return Integer.parseInt(cellValue);
    }

    private List<CovidStatModel> sortDataCountryWise(List<CovidStatModel> tempStats) {

        Map<String, TempDataPerCountry> tempMap = new HashMap<>();


        for (CovidStatModel stat : tempStats) {
            if (tempMap.containsKey(stat.getCountry())) {
                TempDataPerCountry countryValue = tempMap.get(stat.getCountry());
                countryValue.incrementCurrentCount(stat.getLatestCases());
                countryValue.incrementChangeInDelta(stat.getChangeInCasualties());
            } else {
                tempMap.put(stat.getCountry(), new TempDataPerCountry(stat.getLatestCases(), stat.getChangeInCasualties()));
            }
        }

        // Map<String, TempDataPerCountry> sortedMap = new TreeMap<>(tempMap);

        List<CovidStatModel> collectionObj = tempMap.entrySet().stream().map(each -> {
            CovidStatModel model = new CovidStatModel();
            model.setChangeInCasualties(each.getValue().getChangeinLastoneDay());
            model.setCountry(each.getKey());
            model.setLatestCases(each.getValue().getEffectedCount());
            return model;
        }).sorted(Comparator.comparingInt(CovidStatModel::getLatestCases).reversed()).collect(Collectors.toList());

        return collectionObj;
    }

    class TempDataPerCountry {
        private int effectedCount;
        private int changeinLastoneDay;

        public TempDataPerCountry(int effectedCount, int changeinLastoneDay) {
            this.effectedCount = effectedCount;
            this.changeinLastoneDay = changeinLastoneDay;
        }

        public int getEffectedCount() {
            return effectedCount;
        }

        public void setEffectedCount(int effectedCount) {
            this.effectedCount = effectedCount;
        }

        public int getChangeinLastoneDay() {
            return changeinLastoneDay;
        }

        public void setChangeinLastoneDay(int changeinLastoneDay) {
            this.changeinLastoneDay = changeinLastoneDay;
        }

        public void incrementCurrentCount(int latestCases) {
            this.effectedCount = this.effectedCount + latestCases;
        }

        public void incrementChangeInDelta(int changeInCasualties) {
            this.changeinLastoneDay = this.changeinLastoneDay + changeInCasualties;
        }
    }

    public static void main(String[] args) {
        String demo = "1#";
    }

    //@Async("myExecutor")
    @Scheduled(cron = "0 0/4 * * * *")
    //@Bean
    public void getIndianStats() throws IOException {

        int totalEffectedInIndia = 0;
        int totalCured = 0;
        int totalDeath = 0;

        CovidAllIndiaDataModel allData = new CovidAllIndiaDataModel();
        List<CovidDataPerState> allStateData = new ArrayList<>();
        Document doc = Jsoup.connect(INDIAN_DATA_URL).get();
        List<Element> dataRows = doc.getElementsByAttributeValue("class", "content newtab").first().select("div > table > tbody > tr");

        if (dataRows.size() > 1) {
            dataRows = dataRows.subList(0, 27);
            for (Element each : dataRows) {
                CovidDataPerState localData = new CovidDataPerState();
                List<Element> allTdElements = each.select("td");

                int slNo = getIntegerCellvalueBasedOnindex(allTdElements, 0);
                localData.setNo(slNo);

                String stateName = allTdElements.get(1).text();
                localData.setState(stateName);

                int totalCasesIndian = getIntegerCellvalueBasedOnindex(allTdElements, 2);
                localData.setTotalCasesIndian(totalCasesIndian);

                int totalCasesForeign = getIntegerCellvalueBasedOnindex(allTdElements, 3);
                localData.setTotalCasesForeign(totalCasesForeign);

                int cured = getIntegerCellvalueBasedOnindex(allTdElements, 4);
                localData.setCured(cured);

                int death = getIntegerCellvalueBasedOnindex(allTdElements, 5);
                localData.setDeath(death);

                totalEffectedInIndia += localData.getTotal();
                totalCured += cured;
                totalDeath += death;

                allStateData.add(localData);
            }
        }

        allData.setStateData(allStateData);
        allData.setTotalEffectedCount(totalEffectedInIndia);
        allData.setTotalCured(totalCured);
        allData.setTotalDeath(totalDeath);

        this.consolidatedDataOfIndia = allData;
        System.out.println("Indian stat fetched");
    }

    private Integer getIntegerCellvalueBasedOnindex(List<Element> allTdElements, int i) {
        return Integer.valueOf(allTdElements.get(i).text().replaceAll("[^0-9]", ""));
    }

    public CovidAllIndiaDataModel getAllIndianStats() {
        return this.consolidatedDataOfIndia;
    }

    public List<CovidStatModel> getGlobaldata() {
        return this.allStats;
    }

    @Bean
    public String getTimeSeriesData() {
        List<CovidStatModel> tempStats = new ArrayList<>();
        List<TimeSeriesDataModel> listOfTimeSeriesData = new ArrayList<>();
        RestTemplate template = new RestTemplate();
        String response = template.getForObject(TIME_SERIES_DATA_URL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JSONObject obj = new JSONObject(response);
        JSONArray array = obj.getJSONArray("cases_time_series");
        array.forEach(each -> {
            //TimeSeriesDataModel temp = gson.fromJson(each.toString(), TimeSeriesDataModel.class);
            StringReader reader = new StringReader(each.toString());
            try {
                TimeSeriesDataModel temp = mapper.readValue(reader, TimeSeriesDataModel.class);
                listOfTimeSeriesData.add(temp);
                this.seriesModel = listOfTimeSeriesData;
            } catch (IOException e) {
                System.out.println("error occured while deserializing :" + e.getMessage());
            }
        });
        createTimeSeriesChart(listOfTimeSeriesData);
        return response;
    }

    private void createTimeSeriesChart(List<TimeSeriesDataModel> listOfTimeSeriesData) {
        if(listOfTimeSeriesData.size()>0){
            DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

            listOfTimeSeriesData.stream().forEach(each -> line_chart_dataset.addValue(each.getTotalconfirmed(), "count", each.getDate()));

            JFreeChart lineChartObject = ChartFactory.createLineChart(
                    "Effected Count VS Time","Time",
                    "Effected Count",
                    line_chart_dataset, PlotOrientation.VERTICAL,
                    true,true,false);

            int width = 640;    /* Width of the image */
            int height = 480;   /* Height of the image */
            File lineChart = new File( "src/main/resources/effectedount_vs_time.jpeg" );
            try {
                ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
            }catch (IOException e){
                System.out.println("Error occured while saving Effected Count VS Time graph chart : "+e.getMessage());
            }
        }
    }
}
