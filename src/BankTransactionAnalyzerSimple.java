import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class BankTransactionAnalyzerSimple {
    private static final String RESOURCES = "src/";
   // private static final BankStatementCSVParser bankStatementParser = new BankStatementCSVParser();

    public void analyze(final String fileName, final BankStatementParser bankStatementParser)
            throws IOException {

        final Path path = Paths.get(RESOURCES + fileName);
        final List<String> lines = Files.readAllLines(path);

        final List<BankTransaction> bankTransactions = bankStatementParser.parseLinesFrom(lines).stream()
                .filter(transaction -> transaction != null)
                .toList();

        final BankStatementProcessor bankStatementProcessor = new BankStatementProcessor(bankTransactions);

        collectSummary(bankStatementProcessor);

        final List<BankTransaction> transactions
                = bankStatementProcessor.findTransactions(bankTransaction ->
                bankTransaction.getDate().getMonth() == Month.FEBRUARY
                && bankTransaction.getAmount() >= 1_000);

        for(final BankTransaction bankTransaction: transactions) {
            System.out.println("Transaction: " + bankTransaction );
        }
    }

    private static void collectSummary(final BankStatementProcessor bankStatementProcessor) {
        try {
            System.out.println("The total for all transactions is "
                    + bankStatementProcessor.calculateTotalAmount());

            System.out.println("The total for transactions in January is "
                    + bankStatementProcessor.calculateTotalInMonth(Month.JANUARY));

            System.out.println("The total for transactions in February is "
                    + bankStatementProcessor.calculateTotalInMonth(Month.FEBRUARY));

            System.out.println("The total salary received is "
                    + bankStatementProcessor.calculateTotalForCategory("Salary"));

            Scanner keyboard = new Scanner(System.in);
            LocalDate startDate;
            LocalDate endDate;

            System.out.println("Please enter the start date >");
            startDate = LocalDate.parse(keyboard.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

            System.out.println("Please enter the end date >");
            endDate = LocalDate.parse(keyboard.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

            //LocalDate startDate = LocalDate.of(2017, 1,30);
            //LocalDate endDate = LocalDate.of(2017,2,1);

            // Find max transaction in the date range
            Optional<BankTransaction> maxTransaction = bankStatementProcessor.findMaxTransactionInDateRange(startDate, endDate);
            maxTransaction.ifPresent(tx -> System.out.println("The maximum transaction in the range is " + tx.getAmount() + " on " + tx.getDate()));

            // Find min transaction in the date range
            Optional<BankTransaction> minTransaction = bankStatementProcessor.findMinTransactionInDateRange(startDate, endDate);
            minTransaction.ifPresent(tx -> System.out.println("The minimum transaction in the range is " + tx.getAmount() + " on " + tx.getDate()));
        }
        catch(DateTimeParseException | NullPointerException e){
            System.err.println("Reason " + e.getMessage());
        }
        finally {


            //Display histogram
            Map<Month, Map<String, Double>> histogram = bankStatementProcessor.getExpensesHistogram();
            System.out.println("Histogram of expenses by month and description:");
            for (Map.Entry<Month, Map<String, Double>> monthEntry : histogram.entrySet()) {
                System.out.println("Month: " + monthEntry.getKey());
                for (Map.Entry<String, Double> descriptionEntry : monthEntry.getValue().entrySet()) {
                    System.out.println(" " + descriptionEntry.getKey() + ": " + descriptionEntry.getValue());
                }
            }
}
    }




}
