package stocktracker;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO: Add javadoc comments
//TODO: Keep old save configurations and data in Excel table to use API less and boost speed
//TODO: Add .exe somehow
public class StockTracker {

    public static final String VERSION = "0.X";
    public static final int MAX_STOCKS = 5;

    public static void main(String[] args) {

        //runNewTest();
        runExistingTest();
    }

    private static void runNewTest()
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
        createSave(testList, testAmounts);
        //deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");

    }

    private static void runExistingTest() {
        updateSave();
    }

    public static void writeData(String ticker, String currencyCode, LocalDate startDate) {
        StockInfoFetcher.getData(ticker, startDate);
        CurrencyRateFetcher.writeCurrencyInfo(currencyCode, startDate);
    }

    public static void createSave(ArrayList<String> nameList, ArrayList<Number> amountList) {
        boolean append = false;
        for (int i = 0; i < nameList.size(); i++) {
            String line = nameList.get(i) + " " + amountList.get(i);
            FileManager.writeLine("src\\main\\resources\\saved_data\\save_config.txt", line, append);
            append = true;
        }
        List<String> dataList = FileManager.readLines("src\\main\\resources\\aggregated_temp.txt");
        List<String> moneyList = FileManager.readLines("src\\main\\resources\\money.txt");
        append = false;
        for (int i = 0; i < dataList.size(); i++) {
            FileManager.writeLine("src\\main\\resources\\saved_data\\save_data.txt", dataList.get(i), append);
            FileManager.writeLine("src\\main\\resources\\saved_data\\save_money.txt", moneyList.get(i), append);
            append = true;
        }
    }

    public static void updateSave() {
        List<String> saveConfig = FileManager.readLines("src\\main\\resources\\saved_data\\save_config.txt");
        List<String> saveData = FileManager.readLines("src\\main\\resources\\saved_data\\save_data.txt");
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
                writeData(lineArray[0], lineArray[1], lastDate.plusDays(1));
            }
            calculateMoney(ticker_currency, stockAmounts);
            List<String> newDataList = FileManager.readLines("src\\main\\resources\\aggregated_temp.txt");
            List<String> newMoneyList = FileManager.readLines("src\\main\\resources\\money.txt");

            for (int i = 0; i < newDataList.size(); i++) {
                FileManager.writeLine("src\\main\\resources\\saved_data\\save_data.txt", newDataList.get(i), true);
                FileManager.writeLine("src\\main\\resources\\saved_data\\save_money.txt", newMoneyList.get(i), true);
            }
        }
        else {
            System.out.println("All up to date");
        }
    }

    public static void calculateMoney(List<String> ticker_currency, List<Number> stockAmounts)
    {
        DataAggregator.calculateMoney(ticker_currency, stockAmounts);
    }

    public static void deleteTempFiles() {
        FileManager.deleteFiles("src\\main\\resources", "_temp");
    }
}