package coding.challenge.service;

import coding.challenge.domain.RecordsPerSecond;
import coding.challenge.domain.Statistics;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class StatisticsProcessor {

    private long threshold;
    private Map<Long, RecordsPerSecond> map;

    public Statistics process() {
        Statistics statistics = new Statistics();

        map.forEach((ignoreMe, record) -> {
            if (record.getLastTimestampInSeconds() > threshold) { // if record is not stale
                updateStatisticsUsingRecord(statistics, record);
            }
        });

        postProcess(statistics);


        return statistics;
    }

    private void updateStatisticsUsingRecord(Statistics statistics, RecordsPerSecond record) {
        statistics.addCount(record.getCount());
        statistics.addSum(record.getSum());
        if (statistics.getMin() == null || statistics.getMin() > record.getMin()) {
            statistics.setMin(record.getMin());
        }
        if (statistics.getMax() == null || statistics.getMax() < record.getMax()) {
            statistics.setMax(record.getMax());
        }
    }

    private void postProcess(Statistics statistics) {
        if (statistics.getCount() != 0) {
            statistics.setAvg(statistics.getSum() / statistics.getCount());

        } else {
            if (statistics.getMin() == null)
                statistics.setMin(0d);
            if (statistics.getMax() == null)
                statistics.setMax(0d);
        }
    }

}
