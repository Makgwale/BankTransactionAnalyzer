import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BankStatementCSvParserTest {

    private final BankStatementParser statementParser = new BankStatementCSVParser();

    @Test
    public void shouldParseOneCorrectLine() throws Exception {
        final String line = "30-01-2017,-50,Tesco";

        final BankTransaction result = statementParser.parseFrom(line);
        final BankTransaction expected =
                new BankTransaction(LocalDate.of(2017, Month.JANUARY,30),-50,"Tesco");

        final double tolerance = 0.0d;

        Assert.assertEquals(expected.getDate(),result.getDate());
        Assert.assertEquals(expected.getAmount(),result.getAmount(), tolerance);
        Assert.assertEquals(expected.getDescription(),result.getDescription());

    }
    @Test
    public void shouldMaxTransationInRange() throws Exception{
        final List<String> lines = List.of(
                "01-01-2024,1000,Salary",
                "15-01-2024,200,Grocery",
                "10-02-2024,5000,Investment",
                "01-03-2024,150,Utilities"
        );
        final List<BankTransaction> transactions = statementParser.parseLinesFrom(lines);
        final BankStatementProcessor processor = new BankStatementProcessor(transactions);

        final LocalDate startDate = LocalDate.of(2024,1,1);
        final LocalDate endDate   = LocalDate.of(2024,12,31);

        final Optional<BankTransaction> maxTransaction = processor.findMaxTransactionInDateRange(startDate,endDate);

        Assert.assertTrue(maxTransaction.isPresent());
        Assert.assertEquals(5000, maxTransaction.get().getAmount(),0.0d);
    }

    @Test
    public void shouldFindMinTransactionInRange() throws Exception {
        final List<String> lines = List.of(
                "01-01-2024,1000,Salary",
                "15-01-2024,200,Grocery",
                "10-02-2024,5000,Investment",
                "01-03-2024,150,Utilities"
        );

        final List<BankTransaction> transactions = statementParser.parseLinesFrom(lines);
        final BankStatementProcessor processor = new BankStatementProcessor(transactions);

        final LocalDate startDate = LocalDate.of(2024, 1, 1);
        final LocalDate endDate = LocalDate.of(2024, 12, 31);

        final Optional<BankTransaction> minTransaction = processor.findMinTransactionInDateRange(startDate, endDate);

        Assert.assertTrue(minTransaction.isPresent());
        Assert.assertEquals(150, minTransaction.get().getAmount(), 0.0d);
    }

    @Test
    public void shouldGenerateCorrectExpensesHistogram() throws Exception{
        final List<String> lines = List.of(
                "01-01-2024,1000,Salary",
                "01-01-2024,200,Grocery",
                "01-02-2024,1500,Investment",
                "01-02-2024,300,Grocery",
                "01-03-2024,400,Utilities",
                "01-03-2024,250,Grocery",
                "01-04-2024,2000,Salary",
                "01-04-2024,100,Entertainment"
        );
        final List<BankTransaction> transactions = statementParser.parseLinesFrom(lines);
        final BankStatementProcessor processor = new BankStatementProcessor(transactions);

        final Map<Month, Map<String, Double>> histogram = processor.getExpensesHistogram();

        //Validate histogram for January
        Assert.assertTrue(histogram.containsKey(Month.JANUARY));
        Map<String, Double> januaryExpenses = histogram.get(Month.JANUARY);
        Assert.assertEquals(2, januaryExpenses.size());
        Assert.assertEquals(1000.0, januaryExpenses.get("Salary"), 0.0d);
        Assert.assertEquals(200.0, januaryExpenses.get("Grocery"), 0.0d);


        // Validate histogram for February
        Assert.assertTrue(histogram.containsKey(Month.FEBRUARY));
        Map<String, Double> februaryExpenses = histogram.get(Month.FEBRUARY);
        Assert.assertEquals(2, februaryExpenses.size());
        Assert.assertEquals(1500.0, februaryExpenses.get("Investment"), 0.0d);
        Assert.assertEquals(300.0, februaryExpenses.get("Grocery"), 0.0d);

        // Validate histogram for March
        Assert.assertTrue(histogram.containsKey(Month.MARCH));
        Map<String, Double> marchExpenses = histogram.get(Month.MARCH);
        Assert.assertEquals(2, marchExpenses.size());
        Assert.assertEquals(400.0, marchExpenses.get("Utilities"), 0.0d);
        Assert.assertEquals(250.0, marchExpenses.get("Grocery"), 0.0d);

    }


}