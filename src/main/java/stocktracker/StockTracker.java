package stocktracker;

import java.time.LocalDate;
import java.util.ArrayList;

//TODO: Add javadoc comments
//TODO: Keep old save configurations and data in Excel table to use API less and boost speed
//TODO: Add .exe somehow
public class StockTracker {

    public static final String VERSION = "0.X";
    public static final int MAX_STOCKS = 5;

    public static void main(String[] args) {

        runTest();
    }

    private static void runTest()
    {
        System.out.println("$$$");
        //writeData("IVV", "USD", LocalDate.of(2018, 9, 24));
        writeData("IVV", "USD", LocalDate.now().minusDays(139));
        writeData("QQQ", "USD", LocalDate.now().minusDays(139));

        System.out.println("Data fetching done");
        System.out.println("$$$");
        ArrayList<String> testList = new ArrayList<>();
        testList.add("IVV_USD");
        testList.add("QQQ_USD");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        calculateMoney(testList, testAmounts);
        //deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");

    }

    public static void writeData(String ticker, String currencyCode, LocalDate startDate) {
        StockInfoFetcher.getData(ticker, startDate);
        CurrencyRateFetcher.writeCurrencyInfo(currencyCode, startDate);
    }


    public static void calculateMoney(ArrayList<String> ticker_currency, ArrayList<Number> stockAmounts)
    {
        DataAggregator.calculateMoney(ticker_currency, stockAmounts);
        boolean append = false;
        for (int i = 0; i < ticker_currency.size(); i++) {
            FileManager.writeLine("src\\main\\resources\\saved_data\\existingData.txt",
                    ticker_currency.get(i) + " " + stockAmounts.get(i), append);
            append = true;
        }
    }

    public static void deleteTempFiles() {
        FileManager.deleteFiles("src\\main\\resources", "_temp");
    }
}