package stocktracker;

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

public class CurrencyRateFetcher {

    private XMLParser xmlParser;
    private String currencyCode;
    // destination directory!
    private static final String DEST = System.getProperty("user.dir") + "\\src\\main\\resources\\";

    public static void main(String[] args)  {
        writeCurrencyInfo("USD", "2018-09-24");
    }

    public static void writeCurrencyInfo(String currencyCode, String firstDate) {
        CurrencyRateFetcher fetcher = new CurrencyRateFetcher(currencyCode);

        //TODO: Add functionality to select startPeriod for currency
        String url_str = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR/D." + currencyCode +
                ".EUR.SP00.A?startPeriod=" + firstDate + "&detail=dataonly";
        try {
            fetcher.xmlParser.downloadXMLFile(new URL(url_str));
            List<String> dataList = fetcher.xmlParser.parse(DEST);
            FileManager.writeList(DEST + currencyCode + "_temp.txt", dataList);
            System.out.println("Fetching " + currencyCode + " done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CurrencyRateFetcher(String currencyCode) {
        this.currencyCode = currencyCode;
        this.xmlParser = new XMLParser();
    }

    private class XMLParser
    {
        private void downloadXMLFile(URL url)  {
            try {
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/xml");

                //TODO: Unit testing - assert response code 200
                System.out.println("Response code: " + connection.getResponseCode());
                String readStream = readStream(connection.getInputStream());
                List<String> lines = Arrays.asList(readStream.split("\n"));
                Path file = Paths.get(DEST + "\\" + currencyCode + "_XML_temp.xml");
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
        public List<String> parse(String src) {
            try {
                ArrayList<String> dataList = new ArrayList<>();
                src += currencyCode + "_XML_temp.xml";
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

                        String line = date.getAttribute("value") + " " + exchangeRate.getAttribute("value");
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
    }
}
