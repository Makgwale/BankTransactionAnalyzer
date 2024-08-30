import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class BankStatementProcessor {
    private final List<BankTransaction> bankTransactions;

    public BankStatementProcessor(final List<BankTransaction> bankTransactions){
        this.bankTransactions = bankTransactions;
    }

    public double summarizeTransactions(final BankTransactionSummarizer bankTransactionSummarizer){
        double result = 0;
        for(final BankTransaction bankTransaction: bankTransactions){
            result = bankTransactionSummarizer.summarize(result,bankTransaction);
        }
        return result;
    }

    // might not need method
    public double calculateTotalAmount(){
        double total = 0;
        for(final BankTransaction bankTransaction: bankTransactions){
            total += bankTransaction.getAmount();
        }
        return total;
    }

    //edited (screenshot of original in laptop gallery) and on notepadd++
    public double calculateTotalInMonth(final Month month){
        return summarizeTransactions((acc, bankTransactions) ->
                bankTransactions.getDate().getMonth() == month ? acc + bankTransactions.getAmount() : acc);

    }

    public double calculateTotalForCategory(final String category){
        double total = 0;
        for(final BankTransaction bankTransaction: bankTransactions){
            if(bankTransaction.getDescription().equals(category)) {
                total += bankTransaction.getAmount();
            }
        }
        return total;
    }

    //Optional class is used here to handle cases where there might be no transactions within the specified date range
    // .stream(): Converts the list into a stream, enabling the use of functional operations like filtering and finding the maximum.
    //.filter(...): Filters the stream based on a condition.
    //tx: Represents each BankTransaction in the stream.
    //BankTransaction::getAmount: Method reference to getAmount(), which retrieves the transaction amount.
    public Optional<BankTransaction> findMaxTransactionInDateRange(final LocalDate startDate, final LocalDate endDate) {
        return bankTransactions.stream()
                .filter(tx -> !tx.getDate().isBefore(startDate) && !tx.getDate().isAfter(endDate))
                .max(Comparator.comparingDouble(BankTransaction::getAmount));
    }

    // Find the minimum transaction in a date range
    public Optional<BankTransaction> findMinTransactionInDateRange(final LocalDate startDate, final LocalDate endDate) {
        return bankTransactions.stream()
                .filter(tx -> !tx.getDate().isBefore(startDate) && !tx.getDate().isAfter(endDate))
                .min(Comparator.comparingDouble(BankTransaction::getAmount));
    }

    // Return histogram of expenses by month and description
    public Map<Month, Map<String, Double>> getExpensesHistogram(){
        return bankTransactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getDate().getMonth(),
                        Collectors.groupingBy(
                                BankTransaction::getDescription,
                                Collectors.summingDouble(BankTransaction::getAmount)
                        )
                ));
    }

    //Find bank transactions over a certain amount
    public List<BankTransaction> findTransactionsGreaterThanEqual(final int amount){
       return findTransactions(bankTransaction -> bankTransaction.getAmount() >= amount);
    }

    //Find bank transactions in certain month
    public List<BankTransaction> findTransactionInMonth(final Month month){
        final List<BankTransaction> result = new ArrayList<>();
        for(final BankTransaction bankTransaction: bankTransactions){
            if(bankTransaction.getDate().getMonth() == month){
                result.add(bankTransaction);
            }
        }
        return result;
    }

    //Find a specific bank transaction in a certain month over a certain amount.
    public List<BankTransaction> findTransactionsInMonthAndGreater(final Month month, final int amount){
        final List<BankTransaction> result = new ArrayList<>();
        for(final BankTransaction bankTransaction: bankTransactions){
            if(bankTransaction.getDate().getMonth()  == month && bankTransaction.getAmount() >= amount){
                result.add(bankTransaction);
            }
        }
        return result;
    }

    public List<BankTransaction> findTransactions(final BankTransactionFilter bankTransactionFilter){
        final List<BankTransaction> result = new ArrayList<>();
        for(final BankTransaction bankTransaction: bankTransactions){
            if(bankTransactionFilter.test(bankTransaction)){
                result.add(bankTransaction);
            }
        }
        return result;
    }


}
