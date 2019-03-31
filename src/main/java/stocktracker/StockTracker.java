package stocktracker;

import org.patriques.output.AlphaVantageException;
import yahoofinance.YahooFinance;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO: Add enum for constants (version, path, max_stocks)
public class StockTracker {

    public static final String VERSION = "1.4.1";
    public static final int MAX_STOCKS = 5;
    public static final String PATH;

    static {
        try {
            PATH = URLDecoder.decode(new File(StockTracker.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                        .getParent() + "/", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Something wrong with file path");
        }
    }

    public static void main(String[] args) {
        runNewTest();
        //runExistingTest();
    }

    private static void runNewTest()
    {
        System.out.println("$$$");
        ArrayList<String> testList = new ArrayList<>();
        testList.add("IVV");
        testList.add("QQQ");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        createConfig(testList, testAmounts);
        writeData("IVV", LocalDate.now().minusDays(139));
        writeData("QQQ", LocalDate.now().minusDays(139));

        System.out.println("Data fetching done");
        System.out.println("$$$");
        calculateMoney(testList, testAmounts);
        createSave();
        //deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");
    }

    private static void runExistingTest() {
        updateSave();
    }

    /**
     * @param nameList List of tickers of stocks.
     * @param amountList List containing amounts of stocks specified in nameList owned.
     */
    public static void createConfig(ArrayList<String> nameList, ArrayList<Number> amountList) {
        boolean append = false;
        for (int i = 0; i < nameList.size(); i++) {
            String line = nameList.get(i) + "," + amountList.get(i) + ",1.0";
            FileManager.writeLine(PATH + "save_config.csv", line, append);
            append = true;
        }
    }

    /**
     * Writes data of a specified stock and its trading currency to a csv file.
     * @param ticker Ticker of the stock to be recorded.
     * @param startDate First date the data is written from.
     */
    public static void writeData(String ticker, LocalDate startDate) {
        updateData(ticker, startDate, 1);
    }

    public static void updateData(String ticker, LocalDate startDate, double splitCoefficient) {
        try {
            List<String> stockData = StockInfoFetcher.getData(ticker, startDate, splitCoefficient);
            FileManager.writeList(StockTracker.PATH + ticker + "_temp.csv", stockData);

            String currencyCode = YahooFinance.get(ticker).getCurrency();
            List<String> currencyData = CurrencyRateFetcher.getCurrencyInfo(currencyCode, startDate);
            FileManager.writeList(StockTracker.PATH + currencyCode + "_temp.csv", currencyData);

            List<String> aggregatedList =  DataAggregator.aggregateStock(ticker, currencyCode);
            FileManager.writeList(StockTracker.PATH + ticker + "_currency_temp.csv", aggregatedList);
        } catch (AlphaVantageException e) {
            System.out.println("Invalid stock ticker '" + ticker + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the total value of stocks based on the amount owned.
     * @param tickers List of tickers of stocks.
     * @param stockAmounts List of amounts of stocks owned.
     */
    public static void calculateMoney(List<String> tickers, List<Number> stockAmounts) {
        List<String> aggregatedDataList = DataAggregator.calculateMoney(tickers, stockAmounts);
        FileManager.writeList(StockTracker.PATH + "aggregated_with_money_temp.csv", aggregatedDataList);
    }

    /**
     * Creates a csv file that acts as a cache and stores fetched data
     * as to not call the APIs too much and improve performance.
     */
    public static void createSave() {
        FileManager.copyFile(PATH + "aggregated_with_money_temp.csv", PATH + "save_data.csv");
        FileManager.copyFile(PATH + "dividends_aggregated_temp.csv", PATH + "save_dividends.csv");
    }

    /**
     * Reads the save file and if data in them is outdated, updates it.
     * @return boolean whether the file is updated or not
     */
    public static boolean updateSave() {
        List<String> saveData = FileManager.readLines(PATH + "save_data.csv");

        LocalDate lastDate = LocalDate.parse(saveData.get(saveData.size()-1).split(",")[0]);
        if (lastDate.isBefore(StockInfoFetcher.getMostRecentDay())) {
            List<String> saveConfig = FileManager.readLines(PATH + "save_config.csv");
            List<String> tickers = new ArrayList<>();
            List<Number> stockAmounts = new ArrayList<>();

            for (String line : saveConfig) {
                tickers.add(line.split(",")[0]);
                try {
                    stockAmounts.add(NumberFormat.getInstance().parse(line.split(",")[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateData(line.split(",")[0], lastDate.plusDays(1), Double.parseDouble(line.split(",")[2]));
            }
            calculateMoney(tickers, stockAmounts);

            List<String> newDataList = FileManager.readLines(PATH + "aggregated_with_money_temp.csv");
            for (String s : newDataList) {
                FileManager.writeLine(PATH + "save_data.csv", s, true);
            }
            List<String> newDividendList = FileManager.readLines(PATH + "aggregated_with_money_temp.csv");
            for (String s : newDividendList) {
                FileManager.writeLine(PATH + "save_dividends.csv", s, true);
            }

            return true;
        }
        else {
            System.out.println("All up to date");
            return false;
        }
    }

    /**
     * Deletes all temporary files created during data fetching and processing
     */
    public static void deleteTempFiles() {
        FileManager.deleteTempFiles(PATH);
    }
}