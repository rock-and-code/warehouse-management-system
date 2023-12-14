package com.example.warehouseManagement.Domains;

import java.util.List;

public class StatePool {
    private StatePool() {
    };

    public static List<State> getStates() {
        return List.of(
            State.builder().name("Alabama").abbreviation("AL").build(),
            State.builder().name("Alaska").abbreviation("AK").build(),
            State.builder().name("American Samoa").abbreviation("AS").build(),
            State.builder().name("Arizona").abbreviation("AZ").build(),
            State.builder().name("Arkansas").abbreviation("AR").build(),
            State.builder().name("California").abbreviation("CA").build(),
            State.builder().name("Colorado").abbreviation("CO").build(),
            State.builder().name("Connecticut").abbreviation("CT").build(),
            State.builder().name("Delaware").abbreviation("DE").build(),
            State.builder().name("District Of Columbia").abbreviation("DC").build(),
            State.builder().name("Florida").abbreviation("FL").build(),
            State.builder().name("Georgia").abbreviation("GA").build(),
            State.builder().name("Guam").abbreviation("GU").build(),
            State.builder().name("Hawaii").abbreviation("HI").build(),
            State.builder().name("Idaho").abbreviation("ID").build(),
            State.builder().name("Illinois").abbreviation("IL").build(),
            State.builder().name("Indiana").abbreviation("IN").build(),
            State.builder().name("Iowa").abbreviation("IA").build(),
            State.builder().name("Kansas").abbreviation("KS").build(),
            State.builder().name("Kentucky").abbreviation("KY").build(),
            State.builder().name("Louisiana").abbreviation("LA").build(),
            State.builder().name("Maine").abbreviation("ME").build(),
            State.builder().name("Maryland").abbreviation("MD").build(),
            State.builder().name("Massachusetts").abbreviation("MA").build(),
            State.builder().name("Michigan").abbreviation("MI").build(),
            State.builder().name("Minnesota").abbreviation("MN").build(),
            State.builder().name("Mississippi").abbreviation("MS").build(),
            State.builder().name("Missouri").abbreviation("MO").build(),
            State.builder().name("Montana").abbreviation("MT").build(),
            State.builder().name("Nebraska").abbreviation("NE").build(),
            State.builder().name("Nevada").abbreviation("NV").build(),
            State.builder().name("New Hampshire").abbreviation("NH").build(),
            State.builder().name("New Jersey").abbreviation("NJ").build(),
            State.builder().name("New Mexico").abbreviation("NM").build(),
            State.builder().name("New York").abbreviation("NY").build(),
            State.builder().name("North Carolina").abbreviation("NC").build(),
            State.builder().name("North Dakota").abbreviation("ND").build(),
            State.builder().name("Northern Mariana Is").abbreviation("MP").build(),
            State.builder().name("Ohio").abbreviation("OH").build(),
            State.builder().name("Oklahoma").abbreviation("OK").build(),
            State.builder().name("Oregon").abbreviation("OR").build(),
            State.builder().name("Pennsylvania").abbreviation("PA").build(),
            State.builder().name("Puerto Rico").abbreviation("PR").build(),
            State.builder().name("Rhode Island").abbreviation("RI").build(),
            State.builder().name("South Carolina").abbreviation("SC").build(),
            State.builder().name("South Dakota").abbreviation("SD").build(),
            State.builder().name("Tennessee").abbreviation("TN").build(),
            State.builder().name("Texas").abbreviation("TX").build(),
            State.builder().name("Utah").abbreviation("UT").build(),
            State.builder().name("Vermont").abbreviation("VT").build(),
            State.builder().name("Virginia").abbreviation("VA").build(),
            State.builder().name("Virgin Islands").abbreviation("VI").build(),
            State.builder().name("Washington").abbreviation("WA").build(),
            State.builder().name("West Virginia").abbreviation("WV").build(),
            State.builder().name("Wisconsin").abbreviation("WI").build(),
            State.builder().name("Wyoming").abbreviation("WY").build());
    }
}
