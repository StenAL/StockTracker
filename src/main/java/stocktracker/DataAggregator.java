package stocktracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//TODO: add unit tests
class DataAggregator {
    public static void main(String[] args) throws IOException {
        test();
    }

    private static void test() throws IOException {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("QQQ_USD");
        testList.add("IVV_USD");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        calculateMoney(testList, testAmounts);
    }

    static void calculateMoney(List<String> tickers, List<Number> stockAmounts) throws IOException {
        aggregate(tickers);
        List<String> finalData = FileManager.readLines(StockTracker.PATH + "aggregated_temp.csv");
        List<String> dateMoney = new ArrayList<>();
        for (String line: finalData) {
            String[] components = line.split(",");
            double money = 0;
            for (int i = 1; i < components.length; i += 2) {
                double stockPrice = Double.parseDouble(components[i]);
                double currencyRate = Double.parseDouble(components[i+1]);

                money += stockPrice/currencyRate * stockAmounts.get((i-1)/2).doubleValue();
            }
            money = Math.round(money * 100D) / 100D;
            // Padding with trailing zeroes:
            String paddedMoney = "" + money;
            while (paddedMoney.split("\\.")[1].length() < 2) {
                paddedMoney = paddedMoney.concat("0");
            }
            dateMoney.add(line + "," + paddedMoney);
        }
        FileManager.writeList(StockTracker.PATH + "aggregated_with_money_temp.csv", dateMoney);
    }

    private static void aggregate(List<String> tickers) throws IOException {
        String workingDir = StockTracker.PATH;
        List<String> data;
        try {
            String dest = workingDir + "aggregated_temp.csv";
            data = FileManager.readLines(workingDir + "/" + tickers.get(0) + "_currency_temp.csv");
            for (int i = 1; i < tickers.size(); i++) {
                List<String> fileLines = FileManager.readLines(workingDir + "\\" + tickers.get(i) + "_currency_temp.csv");
                for (int j = 0; j < fileLines.size(); j++) {
                    String stockPrice = fileLines.get(j).split(",")[1];
                    String currencyRate = fileLines.get(j).split(",")[2];
                    data.set(j, data.get(j) + "," + stockPrice + "," + currencyRate);
                }
            }
            FileManager.writeList(dest, data);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * There are more dates in the currency file than in the ticker one because stock markets
     * are closed on nation holidays. Therefore we use the the last day's stock market close
     * on dates with no values. If the first day of the whole file happens to be a market
     * holiday then we use the next available day's close value instead.
     */
    static void aggregate(String ticker_currency) throws IOException {
        String workingDir = StockTracker.PATH;
        String ticker = ticker_currency.split("_")[0];
        String currency = ticker_currency.split("_")[1];
        List<String> stockDates = new ArrayList<>();
        List<String> currencyDates = new ArrayList<>();
        List<String> aggregateDates = new ArrayList<>();
        List<String> missingDates = new ArrayList<>();
        List<String> currencyRates = new ArrayList<>();
        List<String> stockRates = new ArrayList<>();
        for (String line: FileManager.readLines(workingDir + ticker + "_temp.csv")) {
            stockDates.add(line.split(",")[0]);
            stockRates.add(line.split(",")[1]);
        }

        for (String line: FileManager.readLines(workingDir + currency + "_temp.csv")) {
            currencyDates.add(line.split(",")[0]);
            currencyRates.add(line.split(",")[1]);
        }

        for (String stockDate: stockDates) {
            aggregateDates.add(stockDate);
            if (!currencyDates.contains(stockDate)) {missingDates.add(stockDate + " C");} // C for currency
        }
        for (String currencyDate: currencyDates) {
            if (!aggregateDates.contains(currencyDate)) {
                missingDates.add(currencyDate + " S"); // S for stock
                aggregateDates.add(currencyDate);
            }
        }
        Collections.sort(aggregateDates);
        for (String date: missingDates) {
            //System.out.println(date);
            String missing = date.split(" ")[1];
            date = date.split(" ")[0];
            if (missing.equals("C")) {
                fillMissingDates(currencyDates, currencyRates, date);
            }
            else {
                fillMissingDates(stockDates, stockRates, date);
            }
            //TODO: Add actual logging for this
            System.out.println(ticker + ": missing " + missing + " on " + date);
        }

        if (aggregateDates.size() != currencyRates.size() || aggregateDates.size() != stockRates.size()) {
            System.out.println("Something went horrendously wrong :(");
            throw new IOException();
        }


        String dest = StockTracker.PATH + ticker + "_currency_temp.csv";
        List<String> writeList = new ArrayList<>();
        for (int i = 0; i < aggregateDates.size(); i++) {
            writeList.add(aggregateDates.get(i) + "," + stockRates.get(i) + "," + currencyRates.get(i));
        }
        FileManager.writeList(dest, writeList);

    }

    private static void fillMissingDates(List<String> datesList, List<String> ratesList, String date) {
        datesList.add(date);
        Collections.sort(datesList);
        int index = datesList.indexOf(date);
        try {
            ratesList.add(index, ratesList.get(index-1));
        } catch (IndexOutOfBoundsException e) {
            ratesList.add(index, ratesList.get(index));
        }
    }
}
