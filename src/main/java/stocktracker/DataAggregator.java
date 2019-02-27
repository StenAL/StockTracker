package stocktracker;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataAggregator {
    // TODO: Add workingDir field
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("IVV_USD");
        testList.add("QQQ_USD");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        calculateMoney(testList, testAmounts);
    }

    public static void calculateMoney(ArrayList<String> ticker_currency, ArrayList<Number> stockAmounts) {
        aggregate(ticker_currency);
        List<String> finalData = FileManager.readLines("src\\main\\resources\\aggregated_temp.txt");
        List<String> dateMoney = new ArrayList<>();
        for (String line: finalData) {
            String[] components = line.split(" ! ");
            String date = components[0];
            double money = 0;
            for (int i = 1; i < components.length; i++) {
                double stockPrice = Double.parseDouble(components[i].split(" ")[0]);
                double currencyRate = Double.parseDouble(components[i].split(" ")[1]);

                //System.out.println(stockPrice + " " + currencyRate);
                money += stockPrice/currencyRate * stockAmounts.get(i-1).doubleValue();
            }
            money = Math.round(money * 100D) / 100D;
            dateMoney.add(date + " " + money);
        }
        FileManager.writeList("src\\main\\resources\\money.txt", dateMoney);
    }

    /**
     * There are more dates in the currency file than in the ticker one because stock markets
     * are closed on nation holidays. Therefore we use the the last day's stock market close
     * on dates with no values. If the first day of the whole file happens to be a market
     * holiday then we use the next available day's close value instead.
     * @param ticker_currency
     */
    public static void aggregate(String ticker_currency) {
        String workingDir = System.getProperty("user.dir") + "\\src\\main\\resources\\";
        String ticker = ticker_currency.split("_")[0];
        String currency = ticker_currency.split("_")[1];
        List<String> stockDates = new ArrayList<>();
        List<String> currencyDates = new ArrayList<>();
        List<String> aggregateDates = new ArrayList<>();
        List<String> missingDates = new ArrayList<>();
        List<String> currencyRates = new ArrayList<>();
        List<String> stockRates = new ArrayList<>();
        try {
            for (String line: FileManager.readLines(workingDir + ticker + "_temp.txt")) {
                stockDates.add(line.split(" ")[0]);
                stockRates.add(line.split(" ")[1]);
            }

            for (String line: FileManager.readLines(workingDir + currency + "_temp.txt")) {
                currencyDates.add(line.split(" ")[0]);
                currencyRates.add(line.split(" ")[1]);
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
            }


            String dest = System.getProperty("user.dir") + "\\src\\main\\resources\\" + ticker + "_" + currency + "_temp.txt";
            List<String> writeList = new ArrayList<>();
            for (int i = 0; i < aggregateDates.size(); i++) {
                writeList.add(aggregateDates.get(i) + " " + stockRates.get(i) + " " + currencyRates.get(i));
            }
            FileManager.writeList(dest, writeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void aggregate(ArrayList<String> ticker_currency) {
        String workingDir = System.getProperty("user.dir") + "\\src\\main\\resources\\";
        for (String combination: ticker_currency) {
            aggregate(combination);
        }
        List<String> data;
        try {
            String dest = workingDir + "aggregated_temp.txt";
            data = Files.readAllLines(Paths.get(workingDir + "\\" + ticker_currency.get(0) + "_temp.txt"));
            for (int i = 0; i < data.size(); i++) {
                String line = data.get(i);
                data.set(i, line.substring(0,11) + "! " + line.substring(11));
            }
            for (int i = 1; i < ticker_currency.size(); i++) {
                List<String> fileLines = Files.readAllLines(Paths.get(workingDir + "\\" + ticker_currency.get(i) + "_temp.txt"));
                for (int j = 0; j < fileLines.size(); j++) {
                    String stockPrice = fileLines.get(j).split(" ")[1];
                    String currencyRate = fileLines.get(j).split(" ")[2];
                    data.set(j, data.get(j) + " ! " + stockPrice + " " + currencyRate);
                }
            }
            FileManager.writeList(dest, data);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
