package stocktracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyRateFetcherTest {

    private static final String PATH = StockTracker.PATH;
    private static List<String> dataList;

    @BeforeAll
    static void setUp() {
        try {
            CurrencyRateFetcher.writeCurrencyInfo("USD", LocalDate.now().minusDays(365));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataList = FileManager.readLines(PATH + "USD_temp.txt");
    }

    @Test
    void testInvalidCurrency() {
        assertThrows(FileNotFoundException.class, () -> CurrencyRateFetcher
                .writeCurrencyInfo("USDD", LocalDate.now().minusDays(365)));
    }

    @Test
    void testDataValidity() {
        for (String entry : dataList) {
            String[] splitEntry = entry.split(" ");
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
        File dataFile = new File(PATH + "USD_temp.txt");
        assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        Thread.sleep(20000);
    }
}