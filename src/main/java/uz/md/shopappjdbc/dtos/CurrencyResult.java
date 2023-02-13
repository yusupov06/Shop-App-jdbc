package uz.md.shopappjdbc.dtos;

import lombok.*;
import uz.md.shopappjdbc.domain.Currency;
import uz.md.shopappjdbc.dtos.request.CurrencyRequest;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CurrencyResult {
    private CurrencyRequest currencyRequest;
    private Currency currency;
    private Double totalResult;
}
