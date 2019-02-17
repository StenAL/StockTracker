package stocktracker;

import oracle.xml.parser.v2.DOMParser;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;

import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyConverter {

    private XMLParser xmlParser;

    public static void main(String[] args)  {
        CurrencyConverter converter = new CurrencyConverter();
        converter.writeCurrencyInfo("USD");
    }

    public CurrencyConverter()
    {
        xmlParser = new XMLParser();
    }

    public void writeCurrencyInfo(String currencyCode) {
        //TODO: Add functionality to select startPeriod for currency
        String url_str = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR/D." + currencyCode + ".EUR.SP00.A?startPeriod=2018-02-16&detail=dataonly";
        try {
            xmlParser.downloadXMLFile(currencyCode, new URL(url_str));
            List<String> dataList = xmlParser.parse(currencyCode, xmlParser.dest);
            xmlParser.writeToTextFile(currencyCode, dataList);
            System.out.println("Fetching " + currencyCode + " done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class XMLParser
    {
        DOMParser parser;

        // destination directory!
        String dest = "C:\\Users\\stenl\\Desktop\\jaava\\StockTracker\\src\\data";

        private void downloadXMLFile(String currencyCode, URL url)  {
            try {
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/xml");

                //TODO: Unit testing - assert response code 200
                System.out.println("Response code: " + connection.getResponseCode());
                String readStream = readStream(connection.getInputStream());
                // Give output for the command line
                //System.out.println(readStream);
                List<String> lines = Arrays.asList(readStream.split("\n"));
                Path file = Paths.get(dest + "\\" + currencyCode + "_temp_XML.xml");
                Files.write(file, lines, Charset.forName("UTF-8"));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * If anything ever breaks, use this:
         * https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
         * @param src
         */
        public List<String> parse(String currencyCode, String src) {

            try {
                ArrayList<String> dataList = new ArrayList<>();
                src += "\\" + currencyCode + "_temp_XML.xml";
                File fXmlFile = new File(src);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Obs");

                System.out.println("----------------------------");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        Element date = (Element) eElement.getElementsByTagName("ObsDimension").item(0);
                        Element exchangeRate = (Element) eElement.getElementsByTagName("ObsValue").item(0);

                        //System.out.println("Date : " + date.getAttribute("value"));
                        //System.out.println("Rate : " + exchangeRate.getAttribute("value") + "\n");
                        String line = date.getAttribute("value") + " " + exchangeRate.getAttribute("value") + "\n";
                        dataList.add(line);
                    }
                }
                return  dataList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String readStream(InputStream in) {
            String newLine  = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine + newLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        private void writeToTextFile(String currencyCode, List<String> dataList) {
            String dest = "C:\\Users\\stenl\\Desktop\\jaava\\StockTracker\\src\\data\\" + currencyCode + "_temp.txt";
            try {
                FileWriter writer = new FileWriter(dest);
                for (String dataEntry: dataList) {
                    writer.write(dataEntry);
                    writer.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
