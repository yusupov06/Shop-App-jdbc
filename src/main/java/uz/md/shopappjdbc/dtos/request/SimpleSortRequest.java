package uz.md.shopappjdbc.dtos.request;

import lombok.*;
import uz.md.shopappjdbc.service.query.Sort;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SimpleSortRequest {

    private String sortBy = "id";

    private Sort.Direction direction = Sort.Direction.ASC;

    private int page;

    private int pageCount = 10;
}
