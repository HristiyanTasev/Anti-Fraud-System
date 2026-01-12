package antifraud.model.transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long moneyAmount;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    @Column
    private String ip;

    @Column
    private String cardNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private WorldRegion worldRegion;

    @Column
    private LocalDateTime dateOfTransaction;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionState feedback;

    public TransactionEntity() {
    }

    public TransactionEntity(Long moneyAmount, TransactionState transactionState, String ip,
                             String cardNumber, WorldRegion worldRegion, LocalDateTime dateOfTransaction) {
        this.moneyAmount = moneyAmount;
        this.transactionState = transactionState;
        this.ip = ip;
        this.cardNumber = cardNumber;
        this.worldRegion = worldRegion;
        this.dateOfTransaction = dateOfTransaction;
    }
}
