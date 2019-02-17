package stocktracker;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.DailyAdjusted;
import org.patriques.output.timeseries.data.StockData;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class StockInfoFetcher {

    private static final String API_KEY = "NZ04YC2MOTE5AN4P";
    private static final int TIMEOUT = 3000;
    private static String generated;

    public static void main(String[] args) {
        getData("IVV");
    }

    public static String getData(String ticker) {
        Map data = fetchData(ticker);
        String firstDate = writeData(data, ticker);

        System.out.println("Fetcing " + ticker + " done");
        return firstDate;
    }

    public static Map fetchData(String ticker)
    {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            HashMap<LocalDate, Double> dateCloses = new HashMap<>();
            //TODO: On first program launch/100 days after last refresh change outputsize to full
            //TODO: Let user specify start date
            DailyAdjusted response = stockTimeSeries.dailyAdjusted(ticker, OutputSize.COMPACT);
            Map<String, String> metaData = response.getMetaData();
            generated = metaData.get("3. Last Refreshed");
            List<StockData> stockData = response.getStockData();
            stockData.forEach(stock -> {
                dateCloses.put(stock.getDateTime().toLocalDate(), stock.getClose());
            });
            return dateCloses;

        } catch (AlphaVantageException e) {
            System.out.println("something went wrong");
        }
        return null;
    }
    public static String writeData(Map data, String ticker) {
        String filename = System.getProperty("user.dir") + "\\src\\data\\" + ticker + "_temp.txt";
        Map<Integer, String> map = new TreeMap<Integer, String>(data);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        try {
            FileWriter writer = new FileWriter(filename);
            //writer.write("Generated: " + generated + "\n");
            String firstDate = null;
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();
                if (firstDate == null) {firstDate = me2.getKey().toString();}
                String writeLine = me2.getKey().toString() + " " + me2.getValue().toString() + "\n";
                writer.write(writeLine);
                writer.flush();
            }
            return firstDate;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

