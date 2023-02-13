package uz.md.shopappjdbc.service.contract;

import uz.md.shopappjdbc.dtos.ApiResult;
import uz.md.shopappjdbc.dtos.CurrencyResult;
import uz.md.shopappjdbc.dtos.request.CurrencyRequest;

public interface CurrencyService  {
    ApiResult<CurrencyResult> getCurrency(CurrencyRequest request);
}
