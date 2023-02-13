package uz.md.shopappjdbc.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CurrencyRequest {

    @NotBlank(message = "Currency request is required")
    private String currency;

    @NotNull(message = "Currency amount is required")
    private Double amount;

    private LocalDateTime dateTime = LocalDateTime.now();
}
