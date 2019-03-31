package stocktracker;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAggregatorTest {

    private static final String PATH = System.getProperty("user.dir") + "/src/test/resources/DataAggregatorTest/";
    private static final List<String> testTickers = new ArrayList<>();

    @BeforeAll
    static void setUp() throws IOException {
        StockTracker.PATH = PATH;
        testTickers.add("AAPL");
        testTickers.add("MSFT");
        FileManager.writeList(PATH + "AAPL_currency_temp.csv", DataAggregator.aggregateStock("AAPL", "USD"));
        FileManager.writeList(PATH + "MSFT_currency_temp.csv", DataAggregator.aggregateStock("MSFT", "USD"));

    }

    @Test
    void testInvalidData() {
        assertThrows(Exception.class, () -> DataAggregator.aggregateStock("AAAPL", "USD"));
    }

    @Test
    void testSingleAggregation() throws IOException {
        List<String> aaplReferenceList = FileManager.readLines(PATH + "AAPL_reference.csv");
        assertEquals(aaplReferenceList, DataAggregator.aggregateStock("AAPL", "USD"));

        List<String> msftReferenceList = FileManager.readLines(PATH + "MSFT_reference.csv");
        assertEquals(msftReferenceList, DataAggregator.aggregateStock("MSFT", "USD"));
    }

    @Test
    void testCompoundAggregation() {
        ArrayList<Number> testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);

        List<String> compoundReferenceList = FileManager.readLines(PATH + "aggregated_with_money_reference.csv");
        assertEquals(compoundReferenceList, DataAggregator.calculateMoney(testTickers, testAmounts));
    }

    @Test
    void testDividendAggregation() {
        List<String> dividendReferenceList = FileManager.readLines(PATH + "dividends_aggregated_reference.csv");
        assertEquals(dividendReferenceList, DataAggregator.aggregateDividends(testTickers));
    }

    @AfterAll
    static void teardown() {
        new File(PATH + "MSFT_currency_temp.csv").delete();
        new File(PATH + "AAPL_currency_temp.csv").delete();
        new File(PATH + "aggregated_temp.csv").delete();
        new File(PATH + "dividends_aggregated_temp.csv").delete();
    }
}