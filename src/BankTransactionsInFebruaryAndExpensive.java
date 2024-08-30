import java.time.Month;

class BankTransactionsInFebruaryAndExpensive implements BankTransactionFilter{
    @Override
    public boolean test(final BankTransaction bankTransaction){
        return bankTransaction.getDate().getMonth() == Month.FEBRUARY
                && bankTransaction.getAmount() >= 1_000;
    }

}
