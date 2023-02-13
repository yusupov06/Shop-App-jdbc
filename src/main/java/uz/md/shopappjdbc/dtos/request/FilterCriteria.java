package uz.md.shopappjdbc.dtos.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FilterCriteria {
    private String filterKey;
    private Double value;
    private FilterOperation operation;
}
