package coding.challenge.domain;

import lombok.Data;

@Data
public class Transaction {
    private double amount;
    private long timestamp;
}
