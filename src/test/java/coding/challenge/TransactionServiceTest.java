package coding.challenge;

import coding.challenge.domain.Statistics;
import coding.challenge.domain.Transaction;
import coding.challenge.service.TransactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TransactionServiceTest {

    private TransactionService service;
    private Clock clock;

    @Before
    public void setup() {
        clock = Clock.systemUTC();
        service = new TransactionService();
        service.setClock(clock);
    }

    @Test
    public void add() throws Exception {
        // given I have one tx in current second
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(clock.millis());

        // when I add this tx
        boolean result = service.add(tx);

        // then tx is added
        Assert.assertTrue(result);
    }

    @Test
    public void add_when_txOlderThan60Seconds() throws Exception {
        // given I have one tx in current second
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(clock.instant().minusSeconds(60).toEpochMilli());

        // when I add this tx
        boolean result = service.add(tx);

        // then tx is not added
        Assert.assertFalse(result);
    }

    @Test
    public void getStatistics() throws Exception {
        // given I have one tx in current second
        long millis = clock.millis();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(millis);
        service.add(tx);
        tx = new Transaction();
        tx.setAmount(20);
        tx.setTimestamp(millis);
        service.add(tx);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then I have correct statistics
        Assert.assertEquals(30d, statistics.getSum(), 0.001);
        Assert.assertEquals(15d, statistics.getAvg(), 0.001);
        Assert.assertEquals(2, statistics.getCount());
        Assert.assertEquals(10d, statistics.getMin(), 0.001);
        Assert.assertEquals(20d, statistics.getMax(), 0.001);
    }

    @Test
    public void getStatistics_when_60SecondsIsElapsed() throws Exception {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        service.setClock(clock);


        // given I have a tx
        long millis = clock.millis();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(millis);
        service.add(tx);

        // and we are 60 seconds later (current second inclusive)
        clock = Clock.fixed(clock.instant().plusSeconds(59), ZoneId.systemDefault());
        service.setClock(clock);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then no statistics is returned
        Assert.assertEquals(0d, statistics.getSum(), 0.001);
        Assert.assertEquals(0d, statistics.getAvg(), 0.001);
        Assert.assertEquals(0, statistics.getCount());
        Assert.assertEquals(0d, statistics.getMin(), 0.001);
        Assert.assertEquals(0d, statistics.getMax(), 0.001);

    }

    @Test
    public void getStatistics_when_moreThen60SecondsIsElapsed() throws Exception {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        service.setClock(clock);


        // given I have a tx
        long millis = clock.millis();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(millis);
        service.add(tx);

        // and we are much more than 60 seconds later
        clock = Clock.fixed(clock.instant().plusSeconds(150), ZoneId.systemDefault());
        service.setClock(clock);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then no statistics is returned
        Assert.assertEquals(0d, statistics.getSum(), 0.001);
        Assert.assertEquals(0d, statistics.getAvg(), 0.001);
        Assert.assertEquals(0, statistics.getCount());
        Assert.assertEquals(0d, statistics.getMin(), 0.001);
        Assert.assertEquals(0d, statistics.getMax(), 0.001);

    }

    @Test
    public void getStatistics_when_60SecondsIsElapsed_with_multiple_txs() throws Exception {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        service.setClock(clock);


        // given I have a tx
        long millis = clock.millis();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(millis);
        service.add(tx);

        // and we 10 second later a new tx is added
        clock = Clock.fixed(clock.instant().plusSeconds(10), ZoneId.systemDefault());
        service.setClock(clock);

        millis = clock.millis();
        tx = new Transaction();
        tx.setAmount(20);
        tx.setTimestamp(millis);
        service.add(tx);

        // and now we are 60 seconds after the first tx was added (current second inclusive)
        clock = Clock.fixed(clock.instant().plusSeconds(49), ZoneId.systemDefault());
        service.setClock(clock);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then statistics only include the second tx
        Assert.assertEquals(20d, statistics.getSum(), 0.001);
        Assert.assertEquals(20d, statistics.getAvg(), 0.001);
        Assert.assertEquals(1, statistics.getCount());
        Assert.assertEquals(20d, statistics.getMin(), 0.001);
        Assert.assertEquals(20d, statistics.getMax(), 0.001);

    }

    @Test
    public void getStatistics_when_twoTxInDifferentSeconds() throws Exception {
        // given I have one tx in current second
        Instant now = clock.instant();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(now.toEpochMilli());
        service.add(tx);
        tx = new Transaction();
        tx.setAmount(20);
        tx.setTimestamp(now.minusSeconds(1).toEpochMilli());
        service.add(tx);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then I have correct statistics
        Assert.assertEquals(30d, statistics.getSum(), 0.001);
        Assert.assertEquals(15d, statistics.getAvg(), 0.001);
        Assert.assertEquals(2, statistics.getCount());
        Assert.assertEquals(10d, statistics.getMin(), 0.001);
        Assert.assertEquals(20d, statistics.getMax(), 0.001);

    }

    @Test
    public void getStatistics_when_twoTxHavingSameAmount() throws Exception {
        // given I have one tx in current second
        Instant now = clock.instant();
        Transaction tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(now.toEpochMilli());
        service.add(tx);
        tx = new Transaction();
        tx.setAmount(10);
        tx.setTimestamp(now.minusSeconds(1).toEpochMilli());
        service.add(tx);

        // when I get statistics
        Statistics statistics = service.getStatistics();

        // then I have correct statistics
        Assert.assertEquals(20d, statistics.getSum(), 0.001);
        Assert.assertEquals(10d, statistics.getAvg(), 0.001);
        Assert.assertEquals(2, statistics.getCount());
        Assert.assertEquals(10d, statistics.getMin(), 0.001);
        Assert.assertEquals(10d, statistics.getMax(), 0.001);
    }


}