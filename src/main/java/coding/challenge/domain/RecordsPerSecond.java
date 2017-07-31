package coding.challenge.domain;

import lombok.Data;

@Data
public class RecordsPerSecond {
    private int count;
    private long lastTimestampInSeconds;
    private double sum;
    private double min;
    private double max;
}
