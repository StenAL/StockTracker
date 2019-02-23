package stocktracker;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.DailyAdjusted;
import org.patriques.output.timeseries.data.StockData;

import java.io.FileWriter;
import java.util.*;

public class StockInfoFetcher {

    private static final String API_KEY = "NZ04YC2MOTE5AN4P";
    private static final int TIMEOUT = 3000;
    //private static String generated;

    public static void main(String[] args) {
        getData("IVV");
    }

    public static String getData(String ticker) {
        Map<String, String> data = fetchData(ticker);
        String firstDate = writeData(data, ticker);

        System.out.println("Fetcing " + ticker + " done");
        return firstDate;
    }

    public static Map<String, String> fetchData(String ticker)
    {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            HashMap<String, String> dateCloses = new HashMap<>();
            //TODO: On first program launch/100 days after last refresh change outputsize to full
            //TODO: Let user specify start date
            DailyAdjusted response = stockTimeSeries.dailyAdjusted(ticker, OutputSize.COMPACT);
            //Map<String, String> metaData = response.getMetaData();
            //generated = metaData.get("3. Last Refreshed");
            List<StockData> stockData = response.getStockData();
            stockData.forEach(stock -> {
                dateCloses.put("" + stock.getDateTime().toLocalDate(), "" + stock.getClose());
            });
            return dateCloses;

        } catch (AlphaVantageException e) {
            System.out.println("something went wrong");
        }
        return null;
    }
    public static String writeData(Map<String, String> data, String ticker) {
        String filename = System.getProperty("user.dir") + "\\src\\main\\resources\\" + ticker + "_temp.txt";
        Map<String, String> map = new TreeMap<>(data);
        Set<Map.Entry<String, String>> set2 = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator2 = set2.iterator();
        try {
            FileWriter writer = new FileWriter(filename);
            //writer.write("Generated: " + generated + "\n");
            String firstDate = null;
            FileManager.newFile(filename);
            while (iterator2.hasNext()) {
                Map.Entry<String, String> me2 = iterator2.next();
                if (firstDate == null) {firstDate = me2.getKey();}
                String writeLine = me2.getKey()+ " " + me2.getValue();
                FileManager.writeLine(filename, writeLine, true);
            }
            return firstDate;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

