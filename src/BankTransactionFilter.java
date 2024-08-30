import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface BankTransactionFilter {
    boolean test(BankTransaction bankTransaction);

}
