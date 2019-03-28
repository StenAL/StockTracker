package stocktracker;


// growth (%, absolute), dividends, ??, r.o.i, etc
public class StatisticsManager {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        calculateStatistics();
    }

    static void calculateStatistics() {
        calculateTotalDividends();
        calculateReturns();
    }

    private static void calculateTotalDividends() {

    }

    private static void calculateReturns() {
        // absolute return, relative return, % roi
    }

    private static class Statistic {
        private String name;
        private Number value;
        private Statistic(String name, Number value) {
            this.name = name;
            this.value = value;
        }
    }
}
