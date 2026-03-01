package com.omeralkan.customer.validation;

import com.omeralkan.customer.util.TCKNDogrulama;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TcNoConstraintValidator implements ConstraintValidator<ValidTcNo, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        //Algoritmayı çağırdığımız yer.
        return TCKNDogrulama.tcknGecerliMi(value);

    }
}