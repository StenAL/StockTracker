package stocktracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTrackerTest {

    private static ArrayList<String> testList;
    private static ArrayList<Number> testAmounts;
    private static final String PATH = System.getProperty("user.dir") + "/src/test/resources/StockTrackerTest/";

    @BeforeAll
    static void setup() {
        StockTracker.PATH = PATH;
        testList = new ArrayList<>();
        testList.add("IVV");
        testList.add("QQQ");
        testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
    }

    @Order(1)
    @Test
    void runNewTest() throws InterruptedException {
        StockTracker.createConfig(testList, testAmounts);
        StockTracker.writeData("IVV", LocalDate.now().minusDays(139));
        StockTracker.writeData("QQQ", LocalDate.now().minusDays(139));

        System.out.println("Data fetching done");
        System.out.println("$$$");
        StockTracker.calculateMoney(testList, testAmounts);
        StockTracker.createSave();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");
        assertEquals(FileManager.readLines(PATH + "save_config_reference.csv"), FileManager.readLines(PATH + "save_config.csv"));
        assertTrue(new File(PATH + "save_data.csv").exists());
        assertFalse(StockTracker.updateSave());
        Thread.sleep(30000);
    }

    @Order(2)
    @Test
    void testUpdate() {
        List<String> originalData = FileManager.readLines(PATH + "save_data.csv");
        List<String> data = originalData.subList(0, originalData.size()-10);
        data.forEach(System.out::println);
        FileManager.writeList(PATH + "save_data.csv", data);
        assertTrue(StockTracker.updateSave());
        assertEquals(FileManager.readLines(PATH + "save_data.csv"), originalData);
    }

    @AfterAll
    static void teardown() {
        new File(PATH + "aggregated_temp.csv").delete();
        new File(PATH + "aggregated_with_money_temp.csv").delete();
        new File(PATH + "dividends_aggregated_temp.csv").delete();
        new File(PATH + "IVV_currency_temp.csv").delete();
        new File(PATH + "IVV_dividend_temp.csv").delete();
        new File(PATH + "IVV_temp.csv").delete();
        new File(PATH + "QQQ_temp.csv").delete();
        new File(PATH + "QQQ_dividend_temp.csv").delete();
        new File(PATH + "QQQ_currency_temp.csv").delete();
        new File(PATH + "save_data.csv").delete();
        new File(PATH + "save_config.csv").delete();
        new File(PATH + "save_dividends.csv").delete();
        new File(PATH + "USD_temp.csv").delete();
        new File(PATH + "USD_XML_temp.xml").delete();
    }
}