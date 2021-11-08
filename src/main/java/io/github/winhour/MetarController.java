package io.github.winhour;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class MetarController {

    /* Interaction with Metar API for wind data */

    int winddirdeg = -1;
    int wspeed = -1;

    public int getWinddirdeg() {
        return winddirdeg;
    }

    public void setWinddirdeg(int winddirdeg) {
        this.winddirdeg = winddirdeg;
    }

    public int getWspeed() {
        return wspeed;
    }

    public void setWspeed(int wspeed) {
        this.wspeed = wspeed;
    }

    /************************************************************************************************************************************************/

    public String getFromMetar(String icao) {

        String xmlString;

        // Host url
        String host = "https://www.aviationweather.gov/adds/dataserver_current/httpparam";
        //String charset = "UTF-8";
        // Headers for a request
        String x_host = "www.aviationweather.gov/adds/dataserver_current/httpparam";
        // Format query for preventing encoding problems

        String query = "dataSource=metars&requestType=retrieve&format=xml&stationString=" + icao + "&hoursBeforeNow=1";

        HttpResponse response = null;
        try {
            response = Unirest.get(host + "?" + query)
                    .header("x-host", x_host)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        assert response != null;
        System.out.println("Status = " + response.getStatus());
        System.out.println("Content Type = " + response.getHeaders().get("Content-Type"));

        xmlString = response.getBody().toString();

        return xmlString;

    }

    /************************************************************************************************************************************************/

    public void parseXMLString(String xmlString){

        Document document = null;
        try {
            document = loadXMLFromString(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Element root = document.getDocumentElement();

        NodeList nList = document.getElementsByTagName("METAR");
        System.out.println("============================");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node node = nList.item(temp);
            System.out.println();    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                //Print each wind's detail
                Element eElement = (Element) node;

                if(getWspeed() == -1) {
                    setWspeed(Integer.parseInt(eElement.getElementsByTagName("wind_speed_kt").item(0).getTextContent()));
                    setWinddirdeg(Integer.parseInt(eElement.getElementsByTagName("wind_dir_degrees").item(0).getTextContent()));
                }


                if (eElement.getElementsByTagName("wind_dir_degrees").getLength()>0) {

                    System.out.println("Wind dir degrees : " + eElement.getElementsByTagName("wind_dir_degrees").item(0).getTextContent());
                    System.out.println("Wind speed kt : " + eElement.getElementsByTagName("wind_speed_kt").item(0).getTextContent());

                }

                else
                {
                    System.out.println("Did not find wind for this ICAO");
                }
            }
        }


    }

    /************************************************************************************************************************************************/

    /* Method to use a String instead of a File for DOM */

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }


}
