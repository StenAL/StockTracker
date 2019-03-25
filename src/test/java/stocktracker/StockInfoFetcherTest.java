package stocktracker;

import org.junit.jupiter.api.*;
import org.patriques.output.AlphaVantageException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockInfoFetcherTest {
    private static String PATH;
    private static List<String> dataList;
    @BeforeAll
    static synchronized void updateData() {
        StockInfoFetcher.getData("TSLA", LocalDate.now().minusDays(139));
        PATH = StockTracker.PATH;
        dataList = FileManager.readLines(PATH + "TSLA_temp.csv");
    }

    @Nested
    @DisplayName("getData")
    class getDataTests {

        @Test
        void testInvalidTicker() {
            assertThrows(AlphaVantageException.class, () -> StockInfoFetcher
                    .getData("STEN", LocalDate.now().minusDays(139)));
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
            assertTrue(dataList.size() > 80);
        }

        @Test
        void testFetchingNewData() {
            File dataFile = new File(PATH + "TSLA_temp.csv");
            assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
        }

        @Test
        void testConfig() {
            File configFile = new File(PATH + "save_config.csv");
            if (configFile.exists()) {
                String[] config = FileManager.readLines(PATH + "save_config.csv").get(0).split(",");
                assertDoesNotThrow(() -> Integer.parseInt(config[1]));
                assertDoesNotThrow(() -> Double.parseDouble(config[2]));
            }
        }
    }

    @Test
    void testGetMostRecentDay() {
        LocalDate now = LocalDate.now();
        LocalDate mostRecent = StockInfoFetcher.getMostRecentDay();
        assertTrue(mostRecent.isBefore(now) || mostRecent.isEqual(now));
        assertTrue(mostRecent.isAfter(now.minusDays(7)));
    }

    @AfterAll
    static void teardown() throws InterruptedException {
        Thread.sleep(15000);
    }
}