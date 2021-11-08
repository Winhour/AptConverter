package io.github.winhour;

import io.github.winhour.database.AirrunDatabaseConnection;
import io.github.winhour.model.DistanceContainer;

import java.util.HashSet;
import java.util.Set;

public class AirportFinder {

    /* Intermediary module to interact with the Airrun database and grab wind data from Metar */

    double lonlatThreshold = 0.5;
    int minimalLength = 6000;
    Set<DistanceContainer> dCont = new HashSet<>();
    String resultIcao = "Icao not found";
    int tmpwind = -1;

    AirrunDatabaseConnection arc = new AirrunDatabaseConnection();


    /************************************************************************************************************************************************/


    public double getLonlatThreshold() {
        return lonlatThreshold;
    }

    public void setLonlatThreshold(double lonlatThreshold) {
        this.lonlatThreshold = lonlatThreshold;
    }

    public int getMinimalLength() {
        return minimalLength;
    }

    public void setMinimalLength(int minimalLength) {
        this.minimalLength = minimalLength;
    }

    public Set<DistanceContainer> getdCont() {
        return dCont;
    }

    public void setdCont(Set<DistanceContainer> dCont) {
        this.dCont = dCont;
    }

    public String getResultIcao() {
        return resultIcao;
    }

    public void setResultIcao(String resultIcao) {
        this.resultIcao = resultIcao;
    }

    public int getTmpwind() {
        return tmpwind;
    }

    public void setTmpwind(int tmpwind) {
        this.tmpwind = tmpwind;
    }

    /************************************************************************************************************************************************/

    public void findClosestAirport(double lat, double lon){

        //Usage depreciated

        arc.checkIfLatitudeLongitudeInSquare(lat,lon,lonlatThreshold,minimalLength);

    }

    /************************************************************************************************************************************************/

    public void getResult(double lat, double lon){

        /* Main functionality of this module */

        arc.checkIfLatitudeLongitudeInSquare(lat,lon,lonlatThreshold,minimalLength);

        System.out.println("Found Icao: " + arc.getResultTmp() + "\n\n");

        dCont = arc.getdCont();

        for (DistanceContainer d: dCont){
            System.out.println(d.getIcao() + "   " + d.getDistance());
        }

        System.out.println("");

        resultIcao = arc.getResultTmp();

        metarInteraction();

        if(tmpwind < 0){

            System.out.println("\n\n\n{{{SECOND TRY}}}\n\n\n");

            arc.checkIfLatitudeLongitudeInSquareV2(lat,lon,minimalLength);

            dCont = arc.getdCont();

            for (DistanceContainer d: dCont){
                System.out.println(d.getIcao() + "   " + d.getDistance());
            }

            System.out.println("");

            resultIcao = arc.getResultTmp();

            metarInteraction();

        }

        if (tmpwind < 0){

            System.out.println("\n\n\n{{{THIRD TRY}}}\n\n\n");

            arc.checkIfLatitudeLongitudeInSquareV3(lat,lon,minimalLength);

            dCont = arc.getdCont();

            for (DistanceContainer d: dCont){
                System.out.println(d.getIcao() + "   " + d.getDistance());
            }

            System.out.println("");

            resultIcao = arc.getResultTmp();

            metarInteraction();

        }

    }

    /************************************************************************************************************************************************/

    public void findClosestInSetAndDeleteFromSet(Set<DistanceContainer> dCont){

        /* Find the airport with the closest distance from the Set, after deleting the previous element, because wind data has not been found for it */

        int tmpint = 9999999;
        String tmpicao = "Icao not found";

        dCont.removeIf(s -> s.getIcao().equals(resultIcao));

        for (DistanceContainer d: dCont){
            System.out.println("\n" + d.getIcao() + "   " + d.getDistance());
            if(d.getDistance()<tmpint){
                tmpint = d.getDistance();
            }
        }

        for (DistanceContainer d: dCont){
            if(d.getDistance() == tmpint){
                tmpicao = d.getIcao();
            }
        }

        System.out.println(tmpicao);
        resultIcao = tmpicao;

        if(dCont.size()>0){
            metarInteraction();
        }


    }

    /************************************************************************************************************************************************/

    public void metarInteraction(){

        /* Interact with the Metar controller, get wind data */

        MetarController mc = new MetarController();
        System.out.println("Taken Icao " + resultIcao);
        String tmp = mc.getFromMetar(resultIcao);
        System.out.println(tmp);
        mc.parseXMLString(tmp);


        if(mc.getWspeed() == -1 && mc.getWinddirdeg() == -1){

            findClosestInSetAndDeleteFromSet(dCont);
            if (dCont.size()<=0){
                tmpwind = -2;
            }

        }
        else {
            System.out.println("\n\n" + mc.getWspeed() + "   " + mc.getWinddirdeg() + "   " + resultIcao);
            tmpwind = mc.getWspeed();
        }



    }

    /************************************************************************************************************************************************/

}
