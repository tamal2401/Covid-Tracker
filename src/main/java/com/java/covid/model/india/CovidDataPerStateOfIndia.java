package com.java.covid.model.india;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CovidDataPerStateOfIndia {

    private int no;
    private String state;
    private int active;
    private int confirmed;
    private int deaths;
    private String lastupdatedtime;
    private int recovered;
    private DeltaInformation delta;
    private String migratedother;
    private int deltaconfirmed;
    private int deltadeaths;
    private int deltarecovered;
    private String statecode;
    private String statenotes;
}
