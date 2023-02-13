package uz.md.shopappjdbc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Currency {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("Code")
    private Integer code;

    @JsonProperty("Ccy")
    private String ccy;

    @JsonProperty("CcyNm_RU")
    private String ccyNm_RU;

    @JsonProperty("CcyNm_UZ")
    private String ccyNm_UZ;

    @JsonProperty("CcyNm_UZC")
    private String ccyNm_UZC;

    @JsonProperty("CcyNm_EN")
    private String ccyNm_EN;

    @JsonProperty("Nominal")
    private String nominal;

    @JsonProperty("Rate")
    private Double rate;

    @JsonProperty("Diff")
    private Double diff;

    @JsonProperty("Date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;
}
