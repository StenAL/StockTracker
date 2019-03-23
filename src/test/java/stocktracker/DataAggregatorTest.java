package stocktracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAggregatorTest {

    private static String PATH = StockTracker.PATH;
    private static List<String> dataList;

    @BeforeAll
    static void updateData() {
        try {
            StockInfoFetcher.getData("AAPL", LocalDate.now().minusDays(365));
            StockInfoFetcher.getData("MSFT", LocalDate.now().minusDays(365));
            CurrencyRateFetcher.writeCurrencyInfo("USD", LocalDate.now().minusDays(365));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> testList = new ArrayList<>();
        testList.add("AAPL_USD");
        testList.add("MSFT_USD");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        try {
            DataAggregator.calculateMoney(testList, testAmounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataList = FileManager.readLines(PATH + "aggregated_temp.txt");
    }

    @Test
    void testInvalidData() {
        List<String> invalidList = new ArrayList<>();
        invalidList.add("AAAPL_USD");
        List<Number> amounts = new ArrayList<>();
        amounts.add(1);
        assertThrows(InvalidPathException.class, () -> DataAggregator.calculateMoney(invalidList, amounts));
    }

    @Test
    void testDataValidity() {
        for (String entry : dataList) {
            String[] splitEntry = entry.split(" ");
            assertEquals(splitEntry.length, 7);
            assertDoesNotThrow(() -> LocalDate.parse(splitEntry[0]));
        }
    }

    @Test
    void testDataSize() {
        assertTrue(dataList.size() > 200);
    }

    @Test
    void testFetchingNewData() {
        File dataFile = new File(PATH + "aggregated_temp.txt");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
    }

    @Test
    void testCurrencyAggregation() {
        List<String> data = FileManager.readLines(PATH + "AAPL_USD_temp.txt");
        assertTrue(data.size() > 200);
        assertTrue(new File(PATH + "AAPL_USD_temp.txt").lastModified() > System.currentTimeMillis()-120000);
    }
}