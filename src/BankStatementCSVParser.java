import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BankStatementCSVParser implements BankStatementParser {
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public BankTransaction parseFrom(final String line){
        final String[] columns = line.split(",");
        final LocalDate date ;
        final double amount;
        final String description = columns[2];

        try {
            date = LocalDate.parse(columns[0], DATE_PATTERN);
            amount = Double.parseDouble(columns[1]);
            OverlySpecificBankStatementValidator validator = new OverlySpecificBankStatementValidator(description, date.format(DATE_PATTERN), columns[1]);
            OverlySpecificBankStatementValidator.Notification notification = validator.validate();

            if(notification.hasErrors()){
                System.err.println("Validation failed for line: " + line);
                for(String error : notification.getErrors()){
                    System.err.println(("Reason: " + error));
                }
                return null;
            }
            return new BankTransaction(date,amount,description);

        } catch(Exception e){
           System.err.println("Unexpected error for line: " + line);
           System.err.println("Reason: " + e.getMessage());
           return null;
        }


    }

    public List<BankTransaction> parseLinesFrom(final List<String> lines){
        final List<BankTransaction> bankTransactions = new ArrayList<>();
        for(final String line: lines){
            bankTransactions.add(parseFrom(line));
        }
        return bankTransactions;
    }

}
