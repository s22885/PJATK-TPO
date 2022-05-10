/**
 * @author Klik Konrad S22885
 */

package zad1;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class Service {
    private static String token = "";
    private String country;
    private String city="";

    public Service(String country) {
        this.country = country;
    }

    public double getNBPRate() {
        double rez = 0;
        String address1 = "https://www.nbp.pl/kursy/xml/b012z220323.xml";
        String address2 = "https://www.nbp.pl/kursy/xml/a060z220328.xml";

        String currName = getCurrByCountry(country);


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(getDataFromNBP(address2))));

            NodeList currNode = doc.getElementsByTagName("pozycja");
            for (int i = 0; i < currNode.getLength(); i++) {
                Node n = currNode.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element cur = (Element) n;
                    String tmpCurrName = cur.getElementsByTagName("kod_waluty").item(0).getTextContent();
                    String rate = cur.getElementsByTagName("kurs_sredni").item(0).getTextContent();
                    String xTimes = cur.getElementsByTagName("przelicznik").item(0).getTextContent();
                    if (tmpCurrName.equals(currName)) {
                        return Double.parseDouble(rate.replaceAll(",", ".")) *
                                Double.parseDouble(xTimes.replaceAll(",", "."));
                    }
                }
            }
            doc = db.parse(new InputSource(new StringReader(getDataFromNBP(address1))));
            currNode = doc.getElementsByTagName("pozycja");
            for (int i = 0; i < currNode.getLength(); i++) {
                Node n = currNode.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element cur = (Element) n;
                    String tmpCurrName = cur.getElementsByTagName("kod_waluty").item(0).getTextContent();
                    String rate = cur.getElementsByTagName("kurs_sredni").item(0).getTextContent();
                    String xTimes = cur.getElementsByTagName("przelicznik").item(0).getTextContent();
                    if (tmpCurrName.equals(currName)) {
                        return Double.parseDouble(rate.replaceAll(",", ".")) *
                                Double.parseDouble(xTimes.replaceAll(",", "."));
                    }
                }
            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }


        return rez;

    }
    private static String getCountryCode(String countryName){
        Locale.setDefault(Locale.ENGLISH);
        AtomicReference<String> res = new AtomicReference<>("");
        Arrays.stream(Locale.getISOCountries()).forEach(e -> {
            Locale tmp = new Locale("", e);
            if (countryName.equals(tmp.getDisplayCountry())) {
               res.set(tmp.getCountry());
            }
        });
        return res.get();
    }

    public double getRateFor(String curr) {
        double res = 0;


        String mainCurr = getCurrByCountry(country);


        try {
            String excha = "https://api.exchangerate.host/latest";
            URL exchaUrl = new URL(excha);
            BufferedReader bfExcha = new BufferedReader(new InputStreamReader(exchaUrl.openStream()));

            StringWriter exchaData = new StringWriter();

            String line;
            while ((line = bfExcha.readLine()) != null) {
                exchaData.append(line);
            }
            JSONParser exchaParser = new JSONParser();
            JSONObject exchaObMain = (JSONObject) exchaParser.parse(exchaData.toString());
            JSONObject exchaObRates = (JSONObject) exchaObMain.get("rates");

            double rateCountry = Double.parseDouble((String) exchaObRates.get(mainCurr).toString());

            double rateCurr = Double.parseDouble((String) exchaObRates.get(curr).toString());
            res = rateCountry / rateCurr;


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        return res;
    }

    public String getWeather(String city) {
        this.city=city;

        StringWriter res = new StringWriter();

        double geoLat = Double.MAX_VALUE;
        double geoLon = Double.MAX_VALUE;
        try {
            String geoLoc = "http://api.openweathermap.org/geo/1.0/direct?q=" +
                    city +
                    "," +
                    getCountryCode(country) +
                    "&limit=5&appid=" +
                    token;


            URL geoLocUrl = new URL(geoLoc);
            BufferedReader bfLoc = new BufferedReader(new InputStreamReader(geoLocUrl.openStream()));

            StringWriter geoLocData = new StringWriter();

            String line;
            while ((line = bfLoc.readLine()) != null) {
                geoLocData.append(line);
            }
            JSONParser geoLocParser = new JSONParser();
            JSONArray geoLocJnMain = (JSONArray) geoLocParser.parse(geoLocData.toString());
            JSONObject geoLocJnFirst = (JSONObject) geoLocJnMain.get(0);
            geoLat = (double) geoLocJnFirst.get("lat");
            geoLon = (double) geoLocJnFirst.get("lon");


            bfLoc.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        try {
            String geoWea = "https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" +
                    geoLat +
                    "&lon=" +
                    geoLon +
                    "&appid=" +
                    token;

            URL geoWeaUrl = new URL(geoWea);
            BufferedReader bfLoc = new BufferedReader(new InputStreamReader(geoWeaUrl.openStream()));

            StringWriter geoWeaData = new StringWriter();

            String line;
            while ((line = bfLoc.readLine()) != null) {
                geoWeaData.append(line);
            }
            JSONParser geoWeaParser = new JSONParser();
            JSONObject geoWeaJnMain = (JSONObject) geoWeaParser.parse(geoWeaData.toString());
            JSONObject getWeaJnTemp = (JSONObject) geoWeaJnMain.get("main");
            Object temp = getWeaJnTemp.get("temp");
            Object pressure = getWeaJnTemp.get("pressure");
            res.append("Temperature= ").append(String.valueOf(temp)).append("'C");
            res.append("\n");
            res.append("Pressure= ").append(String.valueOf(pressure));

            bfLoc.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }


        return res.toString();
    }

    public String getCity() {
        return city;
    }

    private static String getCurrByCountry(String country) {
        Locale.setDefault(Locale.ENGLISH);
        AtomicReference<String> mainCurr = new AtomicReference<>("");
        Arrays.stream(Locale.getISOCountries()).forEach(e -> {
            Locale tmp = new Locale("", e);
            if (country.equals(tmp.getDisplayCountry())) {
                mainCurr.set(Currency.getInstance(tmp).getCurrencyCode());
            }
        });
        return mainCurr.get();
    }

    private static String getDataFromNBP(String address) {
        StringWriter res = new StringWriter();

        try {
            URL url = new URL(address);
            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                res.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res.toString();
    }

    public String getCountry() {
        return country;
    }
}
