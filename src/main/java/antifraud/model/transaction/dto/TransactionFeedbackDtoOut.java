package antifraud.model.transaction.dto;

import antifraud.validation.card.ValidLuhn;
import antifraud.validation.ip.ValidIpV4;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFeedbackDtoOut {
    @NotNull
    public Long transactionId;

    @Min(1)
    @NotNull
    @JsonProperty("amount")
    public Long moneyAmount;

    @NotBlank
    @ValidIpV4
    public String ip;

    @NotBlank
    @ValidLuhn
    @JsonProperty("number")
    public String cardNumber;

    @NotBlank
    @JsonProperty("region")
    public String worldRegion;

    @NotNull
    @JsonProperty("date")
    public LocalDateTime dateOfTransaction;

    @NotBlank
    public String result;

    @NotBlank
    public String feedback;
}
