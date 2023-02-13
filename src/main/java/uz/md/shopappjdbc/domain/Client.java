package uz.md.shopappjdbc.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Client {

    private Long id;
    private String username;
    private String phoneNumber;
    private boolean deleted;
    @ToString.Exclude
    private List<AccessKey> accessKeys = new ArrayList<>();

    public Client(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return getId() != null && getId().equals(((Client) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

}
