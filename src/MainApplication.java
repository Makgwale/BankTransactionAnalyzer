import java.io.IOException;
import java.util.List;

public class MainApplication {

    public static void main(final String... args) throws IOException {

        final BankTransactionAnalyzerSimple bankStatementAnalyzer
                = new BankTransactionAnalyzerSimple();

        final BankStatementParser bankStatementParser
                = new BankStatementCSVParser();

        bankStatementAnalyzer.analyze(args[0], bankStatementParser);




    }
}
