package antifraud.model.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction_limits")
@Getter
@Setter
@NoArgsConstructor
public class TransactionLimitsEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String cardNumber;

    @Column(nullable = false)
    private Long allowedLimit;

    @Column(nullable = false)
    private Long manualLimit;

    public TransactionLimitsEntity(String cardNumber, Long allowedLimit, Long manualLimit) {
        this.cardNumber = cardNumber;
        this.allowedLimit = allowedLimit;
        this.manualLimit = manualLimit;
    }
}