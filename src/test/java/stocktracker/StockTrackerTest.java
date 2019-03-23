package stocktracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.patriques.output.AlphaVantageException;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//TODO: This test is bad. Fix it
class StockTrackerTest {

    private static ArrayList<String> testList;
    private static ArrayList<Number> testAmounts;
    private static String PATH = StockTracker.PATH;

    @BeforeAll
    static void setup() {
        testList = new ArrayList<>();
        testList.add("IVV_USD");
        testList.add("QQQ_USD");
        testAmounts = new ArrayList<>();
        testAmounts.add(5);
        testAmounts.add(10);
    }

    @Test
    void runNewTest()
    {
        StockTracker.createConfig(testList, testAmounts);
        StockTracker.writeData("ASDIVV", "USD", LocalDate.now().minusDays(139));
        StockTracker.writeData("QQQ", "USD", LocalDate.now().minusDays(139));

        System.out.println("Data fetching done");
        System.out.println("$$$");
        StockTracker.calculateMoney(testList, testAmounts);
        StockTracker.createSave();
        StockTracker.deleteTempFiles();
        System.out.println("Files aggregated, money calculated");
        System.out.println("Done");
    }

    //TODO: test actually updating the save
    @Test
    void updateSave() {
        assertFalse(StockTracker.updateSave());
    }

}