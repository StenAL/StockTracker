package stocktracker;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.DailyAdjusted;
import org.patriques.output.timeseries.data.StockData;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;

public class StockInfoFetcher {

    private static final String API_KEY = "NZ04YC2MOTE5AN4P";
    private static final int TIMEOUT = 3000;
    //private static String generated;

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        getData("IVV", LocalDate.now().minusDays(365));
        getData("QQQ", LocalDate.now().minusDays(365));

    }

    public static void getData(String ticker, LocalDate startDate) {
        Map<String, String> data = fetchData(ticker, startDate);
        writeData(data, ticker);

        System.out.println("Fetcing " + ticker + " done");
    }

    public static LocalDate getMostRecentDay() {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
        try {
            List<StockData> temp = stockTimeSeries.daily("IVV").getStockData();
            LocalDate lastDate = temp.get(0).getDateTime().toLocalDate();
            return lastDate;
        }
        catch (AlphaVantageException e) {
            return LocalDate.now();
        }
    }

    public static Map<String, String> fetchData(String ticker, LocalDate startDate)
    {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            // Alpha Vantage has two data OutputSizes: COMPACT which returns the first 100
            // stock data entries and FULL which returns all available stock data.
            // 100 stock market days is equivalent to ~140 calendar days (actually
            // a few more because of market holidays)
            OutputSize size;
            if (LocalDate.now().minusDays(140).isBefore(startDate) ) {
                // startDate is in last 140 days
                size = OutputSize.COMPACT;
            }
            else {
                size = OutputSize.FULL;
            }
            HashMap<String, String> dateCloses = new HashMap<>();
            DailyAdjusted response = stockTimeSeries.dailyAdjusted(ticker, size);
            //Map<String, String> metaData = response.getMetaData();
            //generated = metaData.get("3. Last Refreshed");
            List<StockData> stockData = response.getStockData();
            stockData.forEach(stock -> {
                LocalDate entryDate = stock.getDateTime().toLocalDate();
                if (entryDate.isAfter(startDate.minusDays(1))) {
                    dateCloses.put("" + entryDate, "" + stock.getClose());
                }
            });
            return dateCloses;

        } catch (AlphaVantageException e) {
            System.out.println("something went wrong");
            return null;
        }
    }
    public static void writeData(Map<String, String> data, String ticker) {
        String filename = StockTracker.PATH + ticker + "_temp.txt";
        System.out.println(filename);
        Map<String, String> map = new TreeMap<>(data);
        Set<Map.Entry<String, String>> set2 = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator2 = set2.iterator();
        try {
            String firstDate = null;
            boolean append = false;
            while (iterator2.hasNext()) {
                Map.Entry<String, String> me2 = iterator2.next();
                if (firstDate == null) {firstDate = me2.getKey();}
                String writeLine = me2.getKey()+ " " + me2.getValue();
                FileManager.writeLine(filename, writeLine, append);
                append = true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

