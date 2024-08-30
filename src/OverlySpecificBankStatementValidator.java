import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OverlySpecificBankStatementValidator {

    private String description;
    private String date;
    private String amount;
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public OverlySpecificBankStatementValidator(final String description, final String date, final String amount) {
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
    }

    public Notification validate() {
        final Notification notification = new Notification();
        if (this.description.length() > 100) {
            notification.addError("The description is too long");
        }

        final LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(this.date, DATE_PATTERN);
            if (parsedDate.isAfter(LocalDate.now())) {
                notification.addError("Date cannot be in the future");
            }
        } catch (DateTimeParseException e) {
            notification.addError("Invalid format for date");
        }

        try {
            Double.parseDouble(this.amount);
        } catch (NumberFormatException e) {
            notification.addError("Invalid format for amount");
        }

        return notification;
    }

    public static class Notification {
        private final List<String> errors = new ArrayList<>();

        public void addError(String message) {
            errors.add(message);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
