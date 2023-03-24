package com.example.forexproxy.models;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.example.forexproxy.config.Configuration.ALLOWED_CURRENCIES;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface Currency {
    String message() default "Invalid currency";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class CurrencyValidator implements ConstraintValidator<Currency, String> {

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext context) {
        return ALLOWED_CURRENCIES.contains(currency);
    }
}