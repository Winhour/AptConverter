package io.github.winhour;

import io.github.winhour.database.AirrunDatabaseConnection;
import io.github.winhour.database.AptDatabaseConnection;
import io.github.winhour.database.NavDatabaseConnection;
import io.github.winhour.model.Airport;
import io.github.winhour.model.Runway;
import io.github.winhour.model.Runway_end;

import java.util.ArrayList;
import java.util.List;

public class MainClass {

    /* Main class */

    public static void main(String[] args) {

        List<Airport> airportList = new ArrayList<>();
        List<Runway> runwayList = new ArrayList<>();
        List<Runway_end> runwayEndList = new ArrayList<>();


        //aptDBUseCase(airportList, runwayList, runwayEndList);

        //naviDBUseCase(airportList, runwayList, runwayEndList);

        /*
        MetarController mc = new MetarController();
        String tmp = mc.getFromMetar("EPWA");
        System.out.println(tmp);
        mc.parseXMLString(tmp);*/


        /* 32.137, 22.196 for no wind */

        double lat = -32.137;
        double lon = -72.196;

        AirportFinder af = new AirportFinder();

        af.getResult(lat, lon);


    }

    /************************************************************************************************************************************************/

    public static void aptDBUseCase(List<Airport> airportList, List<Runway> runwayList, List<Runway_end> runwayEndList){
        AptDatabaseConnection adc = new AptDatabaseConnection();
        adc.extractFromDatabase(airportList, runwayList, runwayEndList);

        AirrunDatabaseConnection ardc = new AirrunDatabaseConnection();

        ardc.deleteFromAirports();
        ardc.deleteFromRunways();
        ardc.deleteFromRunwayEnds();

        int i = 0;

        int asize = airportList.size();
        int rsize = runwayList.size();
        int rEsize = runwayEndList.size();

        int totalSize = asize + rsize + rEsize;
        double percentage;

        for (Airport a: airportList){

            percentage = ((double)i/(double)totalSize)*100;
            checkIfSendPercentage(percentage);

            ardc.insertToAirports(a);
            i++;
        }

        System.out.println("DONE WITH AIRPORTS!");

        for (Runway r: runwayList){
            percentage = ((double)i/(double)totalSize)*100;
            checkIfSendPercentage(percentage);

            ardc.insertToRunways(r);
            i++;
        }

        System.out.println("DONE WITH RUNWAYS!");

        for (Runway_end r: runwayEndList){
            percentage = ((double)i/(double)totalSize)*100;
            checkIfSendPercentage(percentage);

            ardc.insertToRunwayEnds(r);
            i++;
        }

        System.out.println("DONE WITH RUNWAY ENDS!\n");


        System.out.println("DONE!");
    }

    /************************************************************************************************************************************************/

    public static void naviDBUseCase(List<Airport> airportList, List<Runway> runwayList, List<Runway_end> runwayEndList) {

        NavDatabaseConnection ndc = new NavDatabaseConnection();

        ndc.extractAirportsFromDatabase(airportList);

        ndc.extractRunwaysFromDatabase(runwayList, runwayEndList);

        AirrunDatabaseConnection ardc = new AirrunDatabaseConnection();


        ardc.deleteFromAirports();
        ardc.deleteFromRunways();
        ardc.deleteFromRunwayEnds();

        int i = 0;

        int asize = airportList.size();
        int rsize = runwayList.size();
        int rEsize = runwayEndList.size();

        int totalSize = asize + rsize + rEsize;
        double percentage;

        for (Airport a: airportList){

            percentage = ((double)i/(double)totalSize)*100;
            System.out.println(percentage);

            ardc.insertToAirports(a);
            i++;
        }

        System.out.println("DONE WITH AIRPORTS!");

        for (Runway r: runwayList){
            percentage = ((double)i/(double)totalSize)*100;
            System.out.println(percentage);

            ardc.insertToRunways(r);
            i++;
        }

        System.out.println("DONE WITH RUNWAYS!");

        for (Runway_end r: runwayEndList){
            percentage = ((double)i/(double)totalSize)*100;
            System.out.println(percentage);

            ardc.insertToRunwayEnds(r);
            i++;
        }

        System.out.println("DONE WITH RUNWAY ENDS!\n");


    }

    /************************************************************************************************************************************************/

    public static void checkIfSendPercentage(double percentage){

        if((percentage>=4.9992 && percentage<=5.000) || (percentage>=9.9992 && percentage<=10.000) || (percentage>=14.9992 && percentage<=15.000)
        || (percentage>=19.9992 && percentage<=20.000) || (percentage>=24.9992 && percentage<=25.000) || (percentage>=29.9992 && percentage<=30.000)
        || (percentage>=34.9992 && percentage<=35.000) || (percentage>=39.9992 && percentage<=40.000) || (percentage>=44.9992 && percentage<=45.000)
        || (percentage>=49.9992 && percentage<=50.000) || (percentage>=54.9992 && percentage<=55.000) || (percentage>=59.9992 && percentage<=60.000)
        || (percentage>=64.9992 && percentage<=65.000) || (percentage>=69.9992 && percentage<=70.000) || (percentage>=74.9992 && percentage<=75.000)
        || (percentage>=79.9992 && percentage<=80.000) || (percentage>=84.9992 && percentage<=85.000) || (percentage>=89.9992 && percentage<=90.000)
        || (percentage>=94.9992 && percentage<=95.000) || (percentage>=99.9992 && percentage<=100.000)){
            System.out.println("Progress percentage: " + Math.round(percentage) + "%");
        }

    }

    /************************************************************************************************************************************************/

    public static void testfoo(){

        int i = 0;
        int lowesta = 100;

        int j = 0;

        while(j<1000){
            i = i+5;
            if (i > lowesta) System.out.println("monkaS");
            lowesta = lowesta + 6;
            System.out.println(lowesta);
            j++;
        }

        System.out.println(i);

    }




}
