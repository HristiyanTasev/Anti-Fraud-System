package antifraud.model.card.dto;

import antifraud.validation.card.ValidLuhn;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StolenCardDtoIn {
    @NotBlank
    @ValidLuhn
    private String number;
}
