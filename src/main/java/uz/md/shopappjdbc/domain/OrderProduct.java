package uz.md.shopappjdbc.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderProduct {

    private Long id;

    private boolean deleted;

    private Order order;

    private Product product;

    private Integer quantity;

    private Double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderProduct) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }


}