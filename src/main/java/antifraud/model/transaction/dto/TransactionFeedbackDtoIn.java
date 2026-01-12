package antifraud.model.transaction.dto;

import antifraud.model.transaction.TransactionState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionFeedbackDtoIn {
    @NotNull
    private Long transactionId;
    @NotNull
    private TransactionState feedback;
}
