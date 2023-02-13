package uz.md.shopappjdbc.domain.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbsUUIDEntity {

    private UUID id;

    private boolean deleted =false;

    private boolean active = true;

    private LocalDateTime addedAt;

}
