package uz.md.shopappjdbc.aop.annotation;

import uz.md.shopappjdbc.domain.enums.PermissionEnum;

import java.lang.annotation.*;


@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CheckAuth {
    PermissionEnum[] permission() default {};
}
