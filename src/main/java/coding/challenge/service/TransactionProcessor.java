package coding.challenge.service;

import coding.challenge.domain.RecordsPerSecond;
import coding.challenge.domain.Transaction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransactionProcessor {
    private long txTimestampInSeconds;
    private RecordsPerSecond existing;
    private Transaction transaction;

    public RecordsPerSecond process() {
        return shouldReplaceExisting(txTimestampInSeconds, existing) ?
                replaceExisting(transaction, txTimestampInSeconds) :
                updateExisting(existing, transaction, txTimestampInSeconds);
    }

    private boolean shouldReplaceExisting(long timestamp, RecordsPerSecond existing) {
        return timestamp != existing.getLastTimestampInSeconds();
    }

    private RecordsPerSecond replaceExisting(Transaction transaction, long timestamp) {
        RecordsPerSecond result = new RecordsPerSecond();

        result.setLastTimestampInSeconds(timestamp);
        result.setCount(1);
        result.setSum(transaction.getAmount());
        result.setMin(transaction.getAmount());
        result.setMax(transaction.getAmount());

        return result;
    }

    private RecordsPerSecond updateExisting(RecordsPerSecond existing, Transaction transaction, long timestamp) {
        RecordsPerSecond result = new RecordsPerSecond();

        result.setLastTimestampInSeconds(timestamp);
        result.setCount(existing.getCount() + 1);
        result.setSum(existing.getSum() + transaction.getAmount());

        if (existing.getMin() > transaction.getAmount())
            result.setMin(transaction.getAmount());
        else
            result.setMin(existing.getMin());

        if (existing.getMax() < transaction.getAmount())
            result.setMax(transaction.getAmount());
        else
            result.setMax(existing.getMax());

        return result;
    }


}
