package uz.md.shopappjdbc.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Address {

    private Long id;
    private User user;
    private Integer houseNumber;
    private String street;
    private String city;
    private boolean deleted;

    public Address(User user, Integer houseNumber, String street, String city) {
        this.user = user;
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
    }

    public Address(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        return getId() != null && getId().equals(((Address) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

}
