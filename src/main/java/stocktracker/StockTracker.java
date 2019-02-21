package stocktracker;

//TODO: Add javadoc comments
//TODO: Add .exe somehow
public class StockTracker {

    private CurrencyRateFetcher converter;
    public StockViewerGUI gui;
    public static final String VERSION = "0.X";

    public static void main(String[] args) {
        StockTracker tracker = new StockTracker();
        //StockViewerGUI.main(null);
        tracker.runTest();
    }

    public static void runTestWithoutGui()
    {

    }

    public void runTest()
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
        if (gui != null)
        gui.setStatusLabel("Done");

    }

    public StockTracker()
    {
        gui = null;
        converter = new CurrencyRateFetcher();
    }

    public void setGui(StockViewerGUI gui) {
        this.gui = gui;
    }

    public String writeStockData(String ticker)
    {
        if (gui != null)
            gui.setStatusLabel("Fetching stock " + ticker + " data..." );
        return StockInfoFetcher.getData(ticker);
    }

    public void writeCurrencyData(String currencyCode, String firstdate) {
        if (gui != null)
            gui.setStatusLabel("Fetching " + currencyCode + " data..." );
        converter.writeCurrencyInfo(currencyCode, firstdate);
    }

    public void aggregateData(String[] ticker_currency)
    {
        if (gui != null)
            gui.setStatusLabel("Aggregating data..." );
        DataAggregator.aggregate(ticker_currency);
    }
}