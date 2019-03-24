package stocktracker;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.DailyAdjusted;
import org.patriques.output.timeseries.data.StockData;

import java.time.LocalDate;
import java.util.*;

class StockInfoFetcher {

    private static final String API_KEY = "NZ04YC2MOTE5AN4P";
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        getData("AAPL", LocalDate.now().minusDays(35));
        getData("IVV", LocalDate.now().minusDays(365));
        getData("QQQ", LocalDate.now().minusDays(365));
    }

    static void getData(String ticker, LocalDate startDate, double splitCoefficient) {
        Map<String, String> data = fetchData(ticker, startDate, splitCoefficient);
        writeData(data, ticker);

        System.out.println("Fetcing " + ticker + " done");
    }

    static void getData(String ticker, LocalDate startDate) {
        Map<String, String> data = fetchData(ticker, startDate, 1);
        writeData(data, ticker);
        System.out.println("Fetcing " + ticker + " done");
    }

    private static Map<String, String> fetchData(String ticker, LocalDate startDate, double splitCoefficient)
    {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

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
        boolean start = true;
        Collections.reverse(stockData);
        for (StockData stock: stockData) {
            LocalDate entryDate = stock.getDateTime().toLocalDate();
            if (entryDate.isAfter(startDate.minusDays(1))) {
                if (start) {
                    splitCoefficient /= stock.getSplitCoefficient();
                    start = false;
                }
                if (stock.getSplitCoefficient() != 1) {
                    splitCoefficient *= stock.getSplitCoefficient();
                }

                // Padding with trailing zeroes:
                String actualPrice = "" + Math.round(stock.getClose()*splitCoefficient*100)/100.0;
                while (actualPrice.split("\\.")[1].length() < 2) {
                    actualPrice = actualPrice.concat("0");
                }

                dateCloses.put("" + entryDate, "" + actualPrice);
            }
        }
        List<String> oldConfig = FileManager.readLines(StockTracker.PATH + "save_config.csv");
        List<String> newConfig = new ArrayList<>();
        for (String line: oldConfig) {
            if (line.startsWith(ticker) && splitCoefficient != 1) {
                String[] splitLine = line.split(" ");
                line = splitLine[0] + "," + splitLine[1] + "," + splitCoefficient;
            }
            newConfig.add(line);
        }
        FileManager.writeList(StockTracker.PATH + "save_config.csv", newConfig);
        return dateCloses;
    }

    private static void writeData(Map<String, String> data, String ticker) {
        String filename = StockTracker.PATH + ticker + "_temp.csv";
        Map<String, String> map = new TreeMap<>(data);
        Set<Map.Entry<String, String>> set2 = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator2 = set2.iterator();
        try {
            boolean append = false;
            while (iterator2.hasNext()) {
                Map.Entry<String, String> me2 = iterator2.next();
                String writeLine = me2.getKey()+ "," + me2.getValue();
                FileManager.writeLine(filename, writeLine, append);
                append = true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static LocalDate getMostRecentDay() {
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
        try {
            List<StockData> temp = stockTimeSeries.daily("IVV").getStockData();
            // last date is first in list
            return temp.get(0).getDateTime().toLocalDate();
        }
        catch (AlphaVantageException e) {
            return LocalDate.now();
        }
    }
}

