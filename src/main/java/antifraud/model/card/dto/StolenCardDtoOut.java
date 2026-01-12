package antifraud.model.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StolenCardDtoOut {
    private Long id;
    private String number;
}
