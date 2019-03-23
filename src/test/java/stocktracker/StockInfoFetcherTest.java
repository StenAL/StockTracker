package stocktracker;

import org.junit.jupiter.api.*;
import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.output.AlphaVantageException;

import java.time.format.DateTimeParseException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockInfoFetcherTest {

    @BeforeAll
    static void updateData() {
        StockInfoFetcher.getData("AAPL", LocalDate.now().minusDays(365));
    }

    @Nested
    @DisplayName("getData")
    class getDataTests {

        String PATH = StockTracker.PATH;
        List<String> dataList = FileManager.readLines(PATH + "AAPL_temp.txt");

        @Test
        void testInvalidTicker() {
            assertThrows(AlphaVantageException.class, () -> StockInfoFetcher
                    .getData("AAAPL", LocalDate.now().minusDays(365)));
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
            File dataFile = new File(PATH + "AAPL_temp.txt");
            assertTrue(dataFile.lastModified() > System.currentTimeMillis()-120000);
        }

        @Test
        void testConfig() {
            File configFile = new File(PATH + "save_config.txt");
            if (configFile.exists()) {
                String[] config = FileManager.readLines(PATH + "save_config.txt").get(0).split(" ");
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
}