package coding.challenge.service;

import coding.challenge.domain.RecordsPerSecond;
import coding.challenge.domain.Statistics;
import coding.challenge.domain.Transaction;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.time.Instant.ofEpochMilli;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@Slf4j
public class TransactionService {
    private static final int PROCESS_WINDOW = 59;

    private Lock lock = new ReentrantLock();
    private Map<Long, RecordsPerSecond> map = new ConcurrentHashMap<>(PROCESS_WINDOW);

    @Setter
    @Autowired
    private Clock clock;

    public boolean add(Transaction transaction) {

        if (isOlderThan60Secs(transaction)) {
            log.debug("Transaction [{}]is older than 60 sec. Current millis: {}", transaction, clock.millis());
            return false;
        }
        log.info("Adding transaction [{}] to current records.", transaction);

        lock.lock();
        try {
            long txTimestampInSeconds = ofEpochMilli(transaction.getTimestamp())
                    .truncatedTo(SECONDS)
                    .toEpochMilli();
            long index = txTimestampInSeconds % PROCESS_WINDOW;
            RecordsPerSecond existing = map.getOrDefault(index, new RecordsPerSecond());
            RecordsPerSecond newRec = new TransactionProcessor(txTimestampInSeconds, existing, transaction).process();

            map.put(index, newRec);

        } catch (Exception e) {
            log.error("Couldn't add transaction[{}] due to an exception", transaction, e);

        } finally {
            lock.unlock();
        }

        return true;
    }

    private boolean isOlderThan60Secs(Transaction tx) {
        Instant sixtySecondsAgo = clock.instant().minusSeconds(PROCESS_WINDOW);
        return tx.getTimestamp() < sixtySecondsAgo.toEpochMilli();
    }


    public Statistics getStatistics() {
        long sixtySecondsAgo = clock.instant().minusSeconds(PROCESS_WINDOW).truncatedTo(SECONDS).toEpochMilli();
        log.debug("Getting statistics for records between [{}] and [{}].", clock.millis(), sixtySecondsAgo);

        return new StatisticsProcessor(sixtySecondsAgo, map).process();
    }
}
