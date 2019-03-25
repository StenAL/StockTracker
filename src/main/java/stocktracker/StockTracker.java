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

//TODO: Add more/better jUnit testing
//TODO: Automatically get currency of stock -- use Yahoo Finance page -- make new project for this
//TODO: Account for dividends using AlphaVantage + add boolean for reinvesting dividends
public class StockTracker {

    public static final String VERSION = "1.1.2";
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
        //writeData("IVV", "USD", LocalDate.of(2018, 9, 24));
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
     * Writes data of a specified stock and currency to text files.
     * @param ticker Ticker of the stock to be recorded.
     * @param startDate First date the data is written from.
     */
    public static void writeData(String ticker, LocalDate startDate) {
        String currencyCode = null;
        try {
            StockInfoFetcher.getData(ticker, startDate);
            currencyCode = YahooFinance.get(ticker).getCurrency();
            CurrencyRateFetcher.writeCurrencyInfo(currencyCode, startDate);
            DataAggregator.aggregate(ticker + "_" + currencyCode);
        } catch (AlphaVantageException e) {
            System.out.println("Invalid stock ticker '" + ticker + "'");
        } catch (IOException e) {
            System.out.println("Invalid currency code'" + currencyCode + "'");
        }
    }

    public static void updateData(String ticker, LocalDate startDate, double splitCoefficient) {
        String currencyCode = null;
        try {
            StockInfoFetcher.getData(ticker, startDate, splitCoefficient);
            currencyCode = YahooFinance.get(ticker).getCurrency();
            CurrencyRateFetcher.writeCurrencyInfo(currencyCode, startDate);
        } catch (AlphaVantageException e) {
            System.out.println("Invalid stock ticker '" + ticker + "'");
        } catch (IOException e) {
            System.out.println("Invalid currency code'" + currencyCode + "'");
        }
    }

    /**
     * Calculates the total value of a stock based on the amount owned.
     * @param ticker_currency Ticker and currency of stock.
     * @param stockAmounts Amount of stock owned.
     */
    public static void calculateMoney(List<String> ticker_currency, List<Number> stockAmounts)
    {
        try {
            DataAggregator.calculateMoney(ticker_currency, stockAmounts);
        } catch (IOException e) {
            System.out.println("Something went horrendously wrong");
        }
    }

    /**
     * @param nameList List containing stocks' tickers and currency codes in the form
     *                 "TICKER_CURRENCYCODE".
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
     * Creates three text files. The first one saves the stock tickers and currency codes
     * and stock amounts specified. The others act as a cache and keep fetched data
     * as to not call the APIs too much and improve performance.
     */
    public static void createSave() {
        List<String> dataList = FileManager.readLines(PATH + "aggregated_with_money_temp.csv");
        boolean append = false;
        for (int i = 0; i < dataList.size(); i++) {
            FileManager.writeLine(PATH + "save_data.csv", dataList.get(i), append);
            append = true;
        }
    }

    /**
     * Reads the save files and if data in them is outdated, updates them.
     * @return whether the file is updated or not
     */
    public static boolean updateSave() {
        List<String> saveConfig = FileManager.readLines(PATH + "save_config.csv");
        List<String> saveData = FileManager.readLines(PATH + "save_data.csv");
        List<String> ticker_currency = new ArrayList<>();
        List<Number> stockAmounts = new ArrayList<>();

        LocalDate lastDate = LocalDate.parse(saveData.get(saveData.size()-1).split(",")[0]);
        if (lastDate.isBefore(StockInfoFetcher.getMostRecentDay())) {
            for (String line : saveConfig) {
                ticker_currency.add(line.split(",")[0]);
                try {
                    stockAmounts.add(NumberFormat.getInstance().parse(line.split(",")[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] lineArray = line.split(",")[0].split("_");
                //updateData(lineArray[0], lineArray[1], lastDate.plusDays(1), Double.parseDouble(line.split(",")[2]));
            }
            calculateMoney(ticker_currency, stockAmounts);
            List<String> newDataList = FileManager.readLines(PATH + "aggregated_with_money_temp.csv");

            for (int i = 0; i < newDataList.size(); i++) {
                FileManager.writeLine(PATH + "save_data.csv", newDataList.get(i), true);
            }
        }
        else {
            System.out.println("All up to date");
            return false;
        }
        return true;
    }

    /**
     * Deletes all temporary files created during data fetching and processing
     */
    public static void deleteTempFiles() {
        FileManager.deleteTempFiles(PATH);
    }
}