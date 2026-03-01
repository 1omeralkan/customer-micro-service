package com.omeralkan.customer.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TcNoConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTcNo {

    String message() default "Geçersiz T.C. Kimlik Numarası girdiniz!";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}