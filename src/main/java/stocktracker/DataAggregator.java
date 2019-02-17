package stocktracker;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataAggregator {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        aggregate(new String[] {"IVV_USD"});
    }

    /**
     * There are more dates in the currency file than in the ticker one because stock markets
     * are closed on nation holidays. Therefore we use the the last day's stock market close
     * on dates with no values. If the first day of the whole file happens to be a market
     * holiday then we use the next available day's close value instead.
     * @param ticker_currency
     */
    public static void aggregate(String[] ticker_currency) {
        String workingDir = System.getProperty("user.dir") + "\\src\\data\\";
        for (String combination: ticker_currency) {
            List<String> stockDates = new ArrayList<>();
            List<String> currencyDates = new ArrayList<>();
            List<String> aggregateDates = new ArrayList<>();
            List<String> missingDates = new ArrayList<>();
            List<String> currencyRates = new ArrayList<>();
            List<String> stockRates = new ArrayList<>();
            String ticker = combination.split("_")[0];
            String currency = combination.split("_")[1];
            try {
                Files.lines(Paths.get(workingDir + ticker + "_temp.txt"))
                        .forEach(line -> {stockDates.add(line.split(" ")[0]);
                        stockRates.add(line.split(" ")[1]);
                        });

                Files.lines(Paths.get(workingDir + currency + "_temp.txt"))
                        .forEach(line -> {currencyDates.add(line.split(" ")[0]);
                            currencyRates.add(line.split(" ")[1]);});

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
                    String missing = date.split(" ")[1];
                    date = date.split(" ")[0];
                    if (missing.equals("C")) {
                        currencyDates.add(date);
                        Collections.sort(currencyDates);
                        int index = currencyDates.indexOf(date);
                        try {
                            currencyRates.add(index, currencyRates.get(index-1));
                        } catch (IndexOutOfBoundsException e) {
                            currencyRates.add(index, currencyRates.get(index));
                        }
                    }
                    else {
                        stockDates.add(date);
                        Collections.sort(stockDates);
                        int index = stockDates.indexOf(date);
                        try {
                            stockRates.add(index, stockRates.get(index-1));
                        } catch (IndexOutOfBoundsException e) {
                            stockRates.add(index, stockRates.get(index));
                        }
                    }
                    System.out.println("Missing: " + missing + " on " + date);
                }
                for (String date: aggregateDates) {
                    if (!missingDates.contains(date)) {
                        String line = "";
                        line += date;
                    }
                }
                if (aggregateDates.size() != currencyRates.size() || aggregateDates.size() != stockRates.size()) {
                    System.out.println("Something went horrendously wrong :(");
                }
                for (int i = 0; i < aggregateDates.size(); i++) {
                    String line = aggregateDates.get(i) + " " + stockRates.get(i) + " " + currencyRates.get(i);
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
