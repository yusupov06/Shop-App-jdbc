package uz.md.shopappjdbc.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilterRequest {
    private FilterCriteria[] filterCriteria;
    private Integer page;
    private Integer pageCount;
}
