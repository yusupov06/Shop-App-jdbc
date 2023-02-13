package uz.md.shopappjdbc.domain.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum PermissionEnum implements GrantedAuthority {

    ADD_USER,
    GET_USER,
    DELETE_USER,
    EDIT_USER,

    ADD_PRODUCT,
    GET_PRODUCT,
    DELETE_PRODUCT,
    EDIT_PRODUCT,

    ADD_CATEGORY,
    GET_CATEGORY,
    DELETE_CATEGORY,
    EDIT_CATEGORY,

    ADD_ORDER,
    GET_ORDER,
    DELETE_ORDER,
    EDIT_ORDER,

    ADD_ROLE,
    GET_ROLE,
    DELETE_ROLE,
    EDIT_ROLE
    ;
    @Override
    public String getAuthority() {
        return name();
    }
}
