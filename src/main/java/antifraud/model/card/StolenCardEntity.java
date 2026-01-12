package antifraud.model.card;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stolen_cards")
@Getter
@Setter
@NoArgsConstructor
public class StolenCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String cardNumber;

    public StolenCardEntity(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
