package coding.challenge.domain;

import lombok.Data;

@Data
public class Statistics {
    private double sum;
    private Double min;
    private Double max;
    private double avg;
    private int count;


    public void addCount(int count){
        this.count += count;
    }
    public void addSum(double sum){
        this.sum += sum;
    }
}
