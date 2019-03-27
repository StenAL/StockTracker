package stocktracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DataAggregatorTest {

    private static final String PATH = StockTracker.PATH;
    private static List<String> dataList;

    @BeforeAll
    static void updateData() throws IOException {
        StockInfoFetcher.getData("AAPL", LocalDate.now().minusDays(139));
        StockInfoFetcher.getData("MSFT", LocalDate.now().minusDays(139));
        CurrencyRateFetcher.writeCurrencyInfo("USD", LocalDate.now().minusDays(139));
        DataAggregator.aggregate("AAPL", "USD");
        DataAggregator.aggregate("MSFT", "USD");

        ArrayList<String> testList = new ArrayList<>();
        testList.add("AAPL");
        testList.add("MSFT");
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
        DataAggregator.calculateMoney(testList, testAmounts);
        dataList = FileManager.readLines(PATH + "aggregated_temp.csv");
    }

    @Test
    void testInvalidData() {
        assertThrows(InvalidPathException.class, () -> DataAggregator.aggregate("AAAPL", "USD"));
    }

    @Test
    void testSingleAggregation() {
        File dataFile = new File(PATH + "AAPL_currency_temp.csv");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
        List<String> data = FileManager.readLines(PATH + "AAPL_currency_temp.csv");
        String line = data.get(new Random().nextInt(data.size()-1));
        assertEquals(3, line.split(",").length);
        assertDoesNotThrow(() -> LocalDate.parse(line.split(",")[0]));
    }

    @Test
    void testDataValidity() {
        for (String entry : dataList) {
            String[] splitEntry = entry.split(",");
            assertEquals(splitEntry.length, 5);
            assertDoesNotThrow(() -> LocalDate.parse(splitEntry[0]));
        }
    }

    @Test
    void testDataSize() {
        assertTrue(dataList.size() > 80);
    }

    @Test
    void testFetchingNewData() {
        File dataFile = new File(PATH + "aggregated_temp.csv");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        Thread.sleep(15000);
    }
}