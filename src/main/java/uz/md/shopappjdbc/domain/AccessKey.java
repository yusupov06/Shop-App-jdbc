package uz.md.shopappjdbc.domain;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessKey {

    private Long id;
    private String access;
    private LocalDateTime validTill;
    private boolean deleted;
    private Client client;

    public AccessKey(String access, Client client, int validTillInDays) {
        this.access = access;
        this.client = client;
        this.validTill = LocalDateTime.now().plusDays(validTillInDays);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccessKey)) {
            return false;
        }
        return getId() != null && getId().equals(((AccessKey) o).getId());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
