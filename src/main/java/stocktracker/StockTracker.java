package stocktracker;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO: migrate files to .csv format?
//TODO: Add unit testing
//TODO: Automatically get currency of stock -- use Yahoo Finance page
//TODO: Add euro support
//TODO: Account for dividends and splits using AlphaVantage
//TODO: Add license file? Reasearch about licenses
//TODO: Aggregate save_data and save_money in file
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
        testList.add("IVV_USD");
        testList.add("QQQ_USD");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        createConfig(testList, testAmounts);
        //writeData("IVV", "USD", LocalDate.of(2018, 9, 24));
        writeData("IVV", "USD", LocalDate.now().minusDays(139), 1);
        writeData("QQQ", "USD", LocalDate.now().minusDays(139), 1);

        System.out.println("Data fetching done");
        System.out.println("$$$");
        createSave();
        deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");
    }

    private static void runExistingTest() {
        updateSave();
    }

    /**
     * Writes data of a specified stock and currency to text files.
     * @param ticker Ticker of the stock to be recorded.
     * @param currencyCode Currcency code of currency to be recorded.
     * @param startDate First date the data is written from.
     */
    public static void writeData(String ticker, String currencyCode, LocalDate startDate, double splitCoefficient) {
        StockInfoFetcher.getData(ticker, startDate, splitCoefficient);
        CurrencyRateFetcher.writeCurrencyInfo(currencyCode, startDate);
    }

    /**
     * Calculates the total value of a stock based on the amount owned.
     * @param ticker_currency Ticker and currency of stock.
     * @param stockAmounts Amount of stock owned.
     */
    public static void calculateMoney(List<String> ticker_currency, List<Number> stockAmounts)
    {
        DataAggregator.calculateMoney(ticker_currency, stockAmounts);
    }

    /**
     * @param nameList List containing stocks' tickers and currency codes in the form
     *                 "TICKER_CURRENCYCODE".
     * @param amountList List containing amounts of stocks specified in nameList owned.
     */
    private static void createConfig(ArrayList<String> nameList, ArrayList<Number> amountList) {
        boolean append = false;
        for (int i = 0; i < nameList.size(); i++) {
            String line = nameList.get(i) + " " + amountList.get(i);
            FileManager.writeLine(PATH + "save_config.txt", line, append);
            append = true;
        }
    }

    /**
     * Creates three text files. The first one saves the stock tickers and currency codes
     * and stock amounts specified. The others act as a cache and keep fetched data
     * as to not call the APIs too much and improve performance.
     */
    public static void createSave() {
        List<String> dataList = FileManager.readLines(PATH + "aggregated_temp.txt");
        List<String> moneyList = FileManager.readLines(PATH + "money.txt");
        boolean append = false;
        for (int i = 0; i < dataList.size(); i++) {
            FileManager.writeLine(PATH + "save_data.txt", dataList.get(i), append);
            FileManager.writeLine(PATH + "save_money.txt", moneyList.get(i), append);
            append = true;
        }
    }

    /**
     * Reads the save files and if data in them is outdated, updates them.
     */
    public static void updateSave() {
        List<String> saveConfig = FileManager.readLines(PATH + "save_config.txt");
        List<String> saveData = FileManager.readLines(PATH + "save_data.txt");
        List<String> ticker_currency = new ArrayList<>();
        List<Number> stockAmounts = new ArrayList<>();

        LocalDate lastDate = LocalDate.parse(saveData.get(saveData.size()-1).split(" ")[0]);
        if (lastDate.isBefore(StockInfoFetcher.getMostRecentDay())) {
            for (String line : saveConfig) {
                ticker_currency.add(line.split(" ")[0]);
                try {
                    stockAmounts.add(NumberFormat.getInstance().parse(line.split(" ")[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] lineArray = line.split(" ")[0].split("_");
                writeData(lineArray[0], lineArray[1], lastDate.plusDays(1), Double.parseDouble(line.split(" ")[2]));
            }
            calculateMoney(ticker_currency, stockAmounts);
            List<String> newDataList = FileManager.readLines(PATH + "aggregated_temp.txt");
            List<String> newMoneyList = FileManager.readLines(PATH + "money.txt");

            for (int i = 0; i < newDataList.size(); i++) {
                FileManager.writeLine(PATH + "save_data.txt", newDataList.get(i), true);
                FileManager.writeLine(PATH + "save_money.txt", newMoneyList.get(i), true);
            }
        }
        else {
            System.out.println("All up to date");
        }
    }

    /**
     * Deletes all temporary files created during data fetching and processing
     */
    public static void deleteTempFiles() {
        FileManager.deleteTempFiles(PATH);
    }
}