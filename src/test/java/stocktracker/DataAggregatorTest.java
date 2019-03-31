package stocktracker;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static List<String> dividendDataList;

    @BeforeAll
    static void updateData() throws IOException {
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
        assertThrows(FileNotFoundException.class, () -> DataAggregator.aggregateStock("AAAPL", "USD"));
    }

    @Test
    void testSingleAggregation() {
        File dataFile = new File(PATH + "AAPL_currency_temp.csv");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis() - 120000);
        List<String> data = FileManager.readLines(PATH + "AAPL_currency_temp.csv");
        String line = data.get(new Random().nextInt(data.size() - 1));
        assertTrue(data.size() > 80);
        assertEquals(3, line.split(",").length);
        assertDoesNotThrow(() -> LocalDate.parse(line.split(",")[0]));
    }

    @Nested
    @DisplayName("compoundAggregation")
    class compoundAggregate {
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
            assertTrue(dataFile.lastModified() > System.currentTimeMillis() - 120000);
        }

        @Test
        void testDividendAggregation() {
            List<String> dividendDataList = FileManager.readLines(PATH + "dividends_aggregated_temp.csv");
            if (dividendDataList.size() > 0) {
                String[] dividendLine = dividendDataList.get(0).split(",");
                assertTrue(dividendLine.length >= 3);
                assertDoesNotThrow(() -> LocalDate.parse(dividendLine[0]));
                assertDoesNotThrow(() -> Double.parseDouble(dividendLine[2]));
            }
        }
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        Thread.sleep(60000);
    }
}