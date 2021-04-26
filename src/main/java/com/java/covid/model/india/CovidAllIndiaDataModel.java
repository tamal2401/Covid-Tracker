package com.java.covid.model.india;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CovidAllIndiaDataModel {

    private List<CovidDataPerStateOfIndia> stateData;
    private int totalConfirmed;
    private int totalRecovered;
    private int totalDeath;
    private String lastUpdated;
    private long totalVaccineted;
}
