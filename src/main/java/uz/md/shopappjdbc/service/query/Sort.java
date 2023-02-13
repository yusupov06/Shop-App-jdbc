package uz.md.shopappjdbc.service.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sort {

    private Direction direction;
    private String orderBy;

    @AllArgsConstructor
    @Getter
    public static enum Direction {
        ASC, DESC
    }

}
