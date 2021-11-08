package io.github.winhour.database;

import io.github.winhour.model.Airport;
import io.github.winhour.model.Runway;
import io.github.winhour.model.Runway_end;

import java.sql.*;
import java.util.List;

public class NavDatabaseConnection {

    /* Interaction with Nav database to populate airport and runway containers */

    private Connection connect() {

        /* Establish database connection */

        Connection conn = null;
        try {
            // db parameters
            System.out.println();
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\navi.sqlite";

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            //System.out.println("Connection to SQLite has been established.\n");

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }

        return conn;

    }

    /************************************************************************************************************************************************/

    public void extractAirportsFromDatabase(List<Airport> airportList){

        /* Get all airports from the database */

        String sql = "SELECT *" + " FROM airport";

        try (Connection conn = this.connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()){

                Airport tmpairport = new Airport();

                tmpairport.setIcao(rs.getString("ident"));
                tmpairport.setElevation(rs.getString("altitude"));
                tmpairport.setName(rs.getString("name"));
                tmpairport.setAtc_tower(rs.getInt("has_tower_object") == 1);
                tmpairport.setClosed(rs.getInt("is_closed") == 1);

                airportList.add(tmpairport);

            }

        } catch (SQLException e) {
        //System.out.println(e.getMessage());
        }


    }

    /************************************************************************************************************************************************/

    public void extractRunwaysFromDatabase(List<Runway> runwayList, List<Runway_end> runwayEndList){

        /* Get all runways from the database */

        String sql = "SELECT *" + " FROM runway";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()){

                Runway tmprunway = new Runway();

                int airport_id = rs.getInt("airport_id");
                String tmpicao = findIcaoForRunway(airport_id);

                tmprunway.setIcao(tmpicao);
                tmprunway.setWidth(rs.getInt("width"));
                tmprunway.setLength(rs.getInt("length"));
                tmprunway.setRunway_start_id(rs.getInt("primary_end_id"));
                tmprunway.setRunway_end_id(rs.getInt("secondary_end_id"));

                runwayList.add(tmprunway);

                extractRunwayEndsFromDatabase(runwayEndList, rs.getInt("primary_end_id"), tmpicao);
                extractRunwayEndsFromDatabase(runwayEndList, rs.getInt("secondary_end_id"), tmpicao);


            }

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }


    }


    /************************************************************************************************************************************************/

    public void extractRunwayEndsFromDatabase(List<Runway_end> runwayEndList, int re_id, String tmpicao){

        /* Get all runway ends from the database */

        String sql = "SELECT *" + " FROM runway_end WHERE runway_end_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1,re_id);

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()){

                Runway_end tmprunwayend = new Runway_end();

                tmprunwayend.setIcao(tmpicao);
                tmprunwayend.setId(re_id);
                tmprunwayend.setDesignator(rs.getString("name"));
                tmprunwayend.setLatitude(rs.getDouble("laty"));
                tmprunwayend.setLongitude(rs.getDouble("lonx"));

                runwayEndList.add(tmprunwayend);

            }

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }


    }

    /************************************************************************************************************************************************/

    public String findIcaoForRunway(int airport_id){

        /* find icao for airport */

        String sql = "SELECT ident" + " FROM airport WHERE airport_id = ?";

        String tmpString = "";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1,airport_id);

            ResultSet rs  = pstmt.executeQuery();

            tmpString = rs.getString("ident");


        } catch (SQLException e) {
            //System.out.println(e.getMessage());
        }

        return tmpString;

    }




}
