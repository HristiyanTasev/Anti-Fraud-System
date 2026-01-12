package antifraud.model.ip.dto;

import antifraud.validation.ip.ValidIpV4;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SuspiciousIpDtoIn {
    @NotBlank
    @ValidIpV4
    private String ip;
}
