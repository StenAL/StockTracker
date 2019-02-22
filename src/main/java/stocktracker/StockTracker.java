package stocktracker;

//TODO: Add javadoc comments
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
        aggregateData(new String[] {"IVV_USD", "QQQ_USD"});
        System.out.println("Files aggregated");
        System.out.println("Done");

    }

    public static String writeStockData(String ticker)
    {
        return StockInfoFetcher.getData(ticker);
    }

    public static void writeCurrencyData(String currencyCode, String firstdate) {
        CurrencyRateFetcher.writeCurrencyInfo(currencyCode, firstdate);
    }

    public static void aggregateData(String[] ticker_currency)
    {
        DataAggregator.aggregate(ticker_currency);
    }
}