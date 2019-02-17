package stocktracker;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConverter {

    XMLParser xmlParser;

    public static void main(String[] args) throws IOException, SAXException {
        CurrencyConverter converter = new CurrencyConverter();
        converter.test();
    }

    public CurrencyConverter()
    {
        xmlParser = new XMLParser();
    }

    public void test() throws IOException, SAXException {
        System.out.println("asd");
        String url_str = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR/D.USD.EUR.SP00.A?startPeriod=2018-02-16&detail=dataonly";
        xmlParser.downloadFile(new URL(url_str));
        //xmlParser.parse(new URL(url_str));

    }

    private class XMLParser
    {
        DOMParser parser;
        private void parse(URL url) throws IOException, SAXException {
            parser = new DOMParser();
            parser.setErrorStream(System.err);
            parser.setValidationMode(DOMParser.DTD_VALIDATION);
            parser.showWarnings(true);
            //parser.parse(url);
            XMLDocument doc = parser.getDocument();
            System.out.print("The elements are: ");
            printElements(doc);

            System.out.println("The attributes of each element are: ");
            //printElementAttributes(doc);



        }

        private void downloadFile(URL url)  {
            try {
                String dest = "C:\\Users\\stenl\\Desktop\\jaava\\StockTracker\\data.xml";
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/xml");

                System.out.println("Response code: " + connection.getResponseCode());
                String readStream = readStream(connection.getInputStream());
                // Give output for the command line
                System.out.println(readStream);

            } catch (Exception e) {}


                /**BufferedInputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(dest);
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                System.out.println("done");
            }
            catch (IOException e) {
                e.printStackTrace();
            }**/
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

        private void printElements(Document doc)
        {
            NodeList nl = doc.getElementsByTagName("*");
            Node n;

            for (int i=0; i<nl.getLength(); i++)
            {
                n = nl.item(i);
                System.out.print(n.getNodeName() + " ");
            }

            System.out.println();
        }

    }
}
