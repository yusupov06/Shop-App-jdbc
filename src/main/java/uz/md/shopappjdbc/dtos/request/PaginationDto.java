package uz.md.shopappjdbc.dtos.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaginationDto {
    private int page;
    private int pageCount;
}
