package stocktracker;

//TODO: Add javadoc comments
// TODO: Keep old saves/data in Excel table
//TODO: Add .exe somehow
public class StockTracker {

    public static final String VERSION = "0.X";

    public static void main(String[] args) {
        runTest();
    }

    public static void runTest()
    {
        System.out.println("$$$");
        writeStockData("QQQ");
        String firstDate = writeStockData("IVV");
        writeCurrencyData("USD", firstDate);
        System.out.println("Data fetching done");
        System.out.println("$$$");
        calculateMoney(new String[] {"IVV_USD", "QQQ_USD"}, new String[] {"5", "10"});
        //deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");

    }

    public static String writeStockData(String ticker)
    {
        return StockInfoFetcher.getData(ticker);
    }

    public static void writeCurrencyData(String currencyCode, String firstdate) {
        CurrencyRateFetcher.writeCurrencyInfo(currencyCode, firstdate);
    }

    public static void calculateMoney(String[] ticker_currency, String[] stockAmounts)
    {
        DataAggregator.calculateMoney(ticker_currency, stockAmounts);
        boolean append = false;
        for (int i = 0; i < ticker_currency.length; i++) {
            FileManager.writeLine("src\\main\\resources\\saved_data\\existingData.txt",
                    ticker_currency[i] + " " + stockAmounts[i], append);
            append = true;
        }
    }

    public static void deleteTempFiles() {
        FileManager.deleteFiles("src\\main\\resources", "_temp");
    }
}