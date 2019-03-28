package stocktracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyRateFetcherTest {

    private static final String PATH = StockTracker.PATH;
    private static List<String> dataList;

    @BeforeAll
    static void setUp() throws IOException {
        CurrencyRateFetcher.writeCurrencyInfo("USD", LocalDate.now().minusDays(365));
        dataList = FileManager.readLines(PATH + "USD_temp.csv");
    }

    @Test
    void testEuroFetching() throws IOException {
        CurrencyRateFetcher.writeCurrencyInfo("EUR", LocalDate.now().minusDays(365));
        File dataFile = new File(PATH + "EUR_temp.csv");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
        List<String> data = FileManager.readLines(PATH + "EUR_temp.csv");
        String line = data.get(new Random().nextInt(data.size()-1));
        assertEquals("1.000", line.split(",")[1]);
        assertDoesNotThrow(() -> LocalDate.parse(line.split(",")[0]));
    }

    @Test
    void testInvalidCurrency() {
        assertThrows(FileNotFoundException.class, () -> CurrencyRateFetcher
                .writeCurrencyInfo("USDD", LocalDate.now().minusDays(365)));
    }

    @Test
    void testDataValidity() {
        for (String entry : dataList) {
            String[] splitEntry = entry.split(",");
            assertEquals(splitEntry.length, 2);
            assertDoesNotThrow(() -> LocalDate.parse(splitEntry[0]));
        }
    }

    @Test
    void testDataSize() {
        assertTrue(dataList.size() > 200);
    }

    @Test
    void testFetchingNewData() {
        File dataFile = new File(PATH + "USD_temp.csv");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        Thread.sleep(20000);
    }
}