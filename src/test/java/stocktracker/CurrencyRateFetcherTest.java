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

    private static final String PATH = System.getProperty("user.dir") + "/src/test/resources/";
    private static List<String> dataList;

    @BeforeAll
    static void setUp() throws IOException {
        StockTracker.PATH = PATH;
        dataList = CurrencyRateFetcher.getCurrencyInfo("USD", LocalDate.now().minusDays(365));
    }

    @Test
    void testEuroFetching() throws IOException {
        List<String> data = CurrencyRateFetcher.getCurrencyInfo("EUR", LocalDate.now().minusDays(365));
        String line = data.get(new Random().nextInt(data.size()-1));
        assertEquals("1.000", line.split(",")[1]);
        assertDoesNotThrow(() -> LocalDate.parse(line.split(",")[0]));
    }

    @Test
    void testInvalidCurrency() {
        assertThrows(FileNotFoundException.class, () -> CurrencyRateFetcher
                .getCurrencyInfo("USDD", LocalDate.now().minusDays(365)));
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

    @AfterAll
    static void teardown()  {
        new File(PATH + "USD_XML_temp.xml").delete();
    }
}