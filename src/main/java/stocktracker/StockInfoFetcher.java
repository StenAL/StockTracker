package stocktracker;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.data.StockData;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.*;

public class StockInfoFetcher {

    private static final String API_KEY = "NZ04YC2MOTE5AN4P";
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        getData("IVV");
    }

    public static void getData(String ticker) {
        Map data = fetchData(ticker);
        writeData(data, ticker);

        System.out.println("Fetcing " + ticker + " done");
    }

    public static Map fetchData(String ticker)
    {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            HashMap<LocalDateTime, Double> dateCloses = new HashMap<>();
            //TODO: Let user determine outputsize using GUI
            Daily response = stockTimeSeries.daily(ticker, OutputSize.COMPACT);
            Map<String, String> metaData = response.getMetaData();
            System.out.println("Information: " + metaData.get("1. Information"));
            System.out.println("Stock: " + metaData.get("2. Symbol"));

            List<StockData> stockData = response.getStockData();
            stockData.forEach(stock -> {
                //System.out.println("date:   " + stock.getDateTime());
                //System.out.println("close:  " + stock.getClose());
                dateCloses.put(stock.getDateTime(), stock.getClose());
            });
            return dateCloses;

        } catch (AlphaVantageException e) {
            System.out.println("something went wrong");
        }
        return null;
    }
    public static void writeData(Map data, String ticker) {
        //System.out.println(data);
        String filename = "C:\\Users\\stenl\\Desktop\\jaava\\StockTracker\\src\\data\\" + ticker + "_temp.txt";
        Map<Integer, String> map = new TreeMap<Integer, String>(data);
        //System.out.println("After Sorting:");
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        try {
            FileWriter writer = new FileWriter(filename);
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();
                //System.out.print(me2.getKey() + ": ");
                //System.out.println(me2.getValue());
                String writeLine = me2.getKey().toString() + " " + me2.getValue().toString() + "\n";
                //System.out.println(writeLine);
                writer.write(writeLine);
                writer.flush();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

