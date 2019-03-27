package stocktracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//TODO: These tests are bad. Fix it
class StockTrackerTest {

    private static ArrayList<String> testList;
    private static ArrayList<Number> testAmounts;
    private static final String PATH = StockTracker.PATH;

    @BeforeAll
    static void setup() {
        testList = new ArrayList<>();
        testList.add("IVV");
        testList.add("QQQ");
        testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
    }

    @Test
    void runNewTest()
    {
        StockTracker.createConfig(testList, testAmounts);
        StockTracker.writeData("IVV", LocalDate.now().minusDays(139));
        StockTracker.writeData("QQQ", LocalDate.now().minusDays(139));

        System.out.println("Data fetching done");
        System.out.println("$$$");
        StockTracker.calculateMoney(testList, testAmounts);
        StockTracker.createSave();
        StockTracker.deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");
        assertTrue(new File(PATH + "save_data.csv").exists());
        assertTrue(new File(PATH + "save_config.csv").exists());
        assertFalse(StockTracker.updateSave());
    }

    //TODO: test actually updating the save

    @AfterAll
    static void teardown() {
        FileManager.deleteTempFiles(PATH);
    }
}