package io.github.winhour.database;

import io.github.winhour.model.Airport;
import io.github.winhour.model.Runway;
import io.github.winhour.model.Runway_end;

import java.sql.*;
import java.util.List;

public class AptDatabaseConnection {

    /* Interaction with Apt database to populate airport and runway containers */


    private Connection connect() {

        /* Establish database connection */

        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\apt.db";

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            //System.out.println("Connection to SQLite has been established.\n");

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }

        return conn;

    }


    /************************************************************************************************************************************************/

    public void extractFromDatabase(List<Airport> airportList, List<Runway> runwayList, List<Runway_end> runwayEndList){
        String sql = "SELECT I" + " FROM airport_import";

        String tmpString;

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            ResultSet rs  = pstmt.executeQuery();

            String icao = "";
            int rNumbertmp = 1;
            int rNumbertmp2 = 2;

            while (rs.next()){
                tmpString = rs.getString("I");
                if(tmpString != null && !tmpString.equals("") && !tmpString.equals("null") && tmpString.length()>10) {

                    Airport tmpAirport = new Airport();

                    int i = tmpString.indexOf(' ');
                    String signifier = tmpString.substring(0, i);                    //signifier
                    String rest = tmpString.substring(i);
                    rest = rest.trim();

                    if (signifier.equals("1")) {

                        i = rest.indexOf(' ');
                        String elevation = rest.substring(0, i);                    //elevation
                        String rest2 = rest.substring(i);
                        rest2 = rest2.trim();
                        rest2 = rest2.replaceAll("\\s+"," ");

                        String[] arr = rest2.split(" ");

                        String atc = arr[0];                              //atc
                        String depr = arr[1];                              //deprecated
                        icao = arr[2];                              //icao
                        String closed = arr[3];                              //closed
                        String airportName = "";                                  //name
                        if(closed.equals("[X]")){

                            airportName = getNameFromParsing(arr, 4, airportName);

                        } else {
                            closed = "[ ]";
                            airportName = getNameFromParsing(arr, 3, airportName);
                        }

                        /*System.out.println("Airport: signifier: " + signifier + "   elevation: " + elevation + "   atc: " + atc + "   depr: " + depr + "   icao: "
                                + icao + "   closed: " + closed + "   name: " + airportName);*/

                        tmpAirport.setAtc_tower(Boolean.parseBoolean(atc));
                        tmpAirport.setElevation(elevation);
                        tmpAirport.setIcao(icao);
                        tmpAirport.setName(airportName);
                        if(closed.equals("[X]")) tmpAirport.setClosed(true);
                        else tmpAirport.setClosed(false);

                        airportList.add(tmpAirport);

                    }

                    if (signifier.equals("100")){

                        Runway tmpRunway = new Runway();
                        Runway_end tmpRunwayEnd = new Runway_end();
                        Runway_end tmpRunwayEnd2 = new Runway_end();

                        rest = rest.replaceAll("\\s+"," ");
                        String[] arr = rest.split(" ");

                        String rWidth = arr[0];
                        String sType = arr[1];
                        String rsSurface = arr[2];
                        String rSmoothness = arr[3];
                        String clLights = arr[4];
                        String eLights = arr[5];
                        String aSigns = arr[6];


                        /*System.out.println("Runway: icao: " + icao + "   signifier: " + signifier + "   rWidth: " + rWidth + "   sType: " + sType + "   rsSurface: " + rsSurface
                                + "   rsSmoothness: " + rSmoothness + "   clLights: " + clLights + "   eLights: " + eLights + "   aSigns: " + aSigns + "   runway_number:" + runway_number_gl);*/

                        tmpRunway.setIcao(icao);
                        tmpRunway.setWidth(Double.parseDouble(rWidth));
                        tmpRunway.setSurface_type(Integer.parseInt(sType));
                        tmpRunway.setRunway_shoulder_surface(Integer.parseInt(rsSurface));
                        tmpRunway.setRunway_smoothness(Double.parseDouble(rSmoothness));
                        tmpRunway.setCentre_line_lights(Boolean.parseBoolean(clLights));
                        tmpRunway.setEdge_lights(Integer.parseInt(eLights));
                        tmpRunway.setAuto_signs(Boolean.parseBoolean(aSigns));
                        tmpRunway.setRunway_start_id(rNumbertmp);
                        tmpRunway.setRunway_end_id(rNumbertmp2);



                        String designator = arr[7];
                        String latitude = arr[8];
                        String longitude = arr[9];
                        String dThreshold = arr[10];
                        String overrun = arr[11];
                        String rMarkings = arr[12];
                        String aLighting = arr[13];
                        String tdzLighting = arr[14];
                        String reil = arr[15];

                        /*
                        System.out.println("Runway end: icao: " + icao + "   number: " + designator + "   runway_number: "  + runway_number + "   latitude: " + latitude + "   longitude: " + longitude
                                + "   dThreshold: " + dThreshold + "   overrun: " + overrun + "   rMarkings: " + rMarkings
                                + "   aLighting: " + aLighting + "   tdzLighting: " + tdzLighting + "   reil: " + reil);
                         */

                        tmpRunwayEnd.setIcao(icao);
                        tmpRunwayEnd.setDesignator(designator);
                        tmpRunwayEnd.setId(rNumbertmp);
                        tmpRunwayEnd.setLatitude(Double.parseDouble(latitude));
                        tmpRunwayEnd.setLongitude(Double.parseDouble(longitude));
                        tmpRunwayEnd.setDisplaced_threshold(Double.parseDouble(dThreshold));
                        tmpRunwayEnd.setOverrun(Double.parseDouble(overrun));
                        tmpRunwayEnd.setRunway_markings(Integer.parseInt(rMarkings));
                        tmpRunwayEnd.setApproach_lighting(Integer.parseInt(aLighting));
                        tmpRunwayEnd.setTdz_lighting(Boolean.parseBoolean(tdzLighting));
                        tmpRunwayEnd.setReil(Integer.parseInt(reil));

                        runwayEndList.add(tmpRunwayEnd);


                        String designator2 = arr[16];
                        String runway_number2 = Integer.toString(rNumbertmp);
                        String latitude2 = arr[17];
                        String longitude2 = arr[18];
                        String dThreshold2 = arr[19];
                        String overrun2 = arr[20];
                        String rMarkings2 = arr[21];
                        String aLighting2 = arr[22];
                        String tdzLighting2 = arr[23];
                        String reil2 = arr[24];

                        /*
                        System.out.println("Runway end: icao: " + icao + "   number: " + designator2 + "   runway_number: "  + runway_number2 + "   latitude: " + latitude2 + "   longitude: " + longitude2
                                + "   dThreshold: " + dThreshold2 + "   overrun: " + overrun2 + "   rMarkings: " + rMarkings2
                                + "   aLighting: " + aLighting2 + "   tdzLighting: " + tdzLighting2 + "   reil: " + reil2);

                        System.out.println("");
                         */

                        tmpRunwayEnd2.setIcao(icao);
                        tmpRunwayEnd2.setDesignator(designator2);
                        tmpRunwayEnd2.setId(rNumbertmp2);
                        tmpRunwayEnd2.setLatitude(Double.parseDouble(latitude2));
                        tmpRunwayEnd2.setLongitude(Double.parseDouble(longitude2));
                        tmpRunwayEnd2.setDisplaced_threshold(Double.parseDouble(dThreshold2));
                        tmpRunwayEnd2.setOverrun(Double.parseDouble(overrun2));
                        tmpRunwayEnd2.setRunway_markings(Integer.parseInt(rMarkings2));
                        tmpRunwayEnd2.setApproach_lighting(Integer.parseInt(aLighting2));
                        tmpRunwayEnd2.setTdz_lighting(Boolean.parseBoolean(tdzLighting2));
                        tmpRunwayEnd2.setReil(Integer.parseInt(reil2));

                        runwayEndList.add(tmpRunwayEnd2);

                        int length = tmpRunway.calculateLength(Double.parseDouble(latitude), Double.parseDouble(latitude2), Double.parseDouble(longitude), Double.parseDouble(longitude2));
                        tmpRunway.setLength(length);

                        runwayList.add(tmpRunway);

                        rNumbertmp = rNumbertmp + 2;
                        rNumbertmp2 = rNumbertmp2 + 2;


                    }

                }

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /************************************************************************************************************************************************/

    public String getNameFromParsing(String[] arr, int n, String word8){
        for (;n<arr.length;n++){
            if(word8.equals("")){
                word8 += arr[n];
            } else {
                word8 += " " + arr[n];
            }
        }

        return word8;
    }






}
