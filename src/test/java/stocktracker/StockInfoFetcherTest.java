package stocktracker;

import org.junit.jupiter.api.*;
import org.patriques.output.AlphaVantageException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockInfoFetcherTest {
    private static List<String> dataList;

    @BeforeAll
    static void updateData() {
        dataList = StockInfoFetcher.getData("BRK-B", LocalDate.now().minusDays(139));
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
        Thread.sleep(30000);
    }
}