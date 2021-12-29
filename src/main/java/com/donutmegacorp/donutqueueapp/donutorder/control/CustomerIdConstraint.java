package com.donutmegacorp.donutqueueapp.donutorder.control;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotNull
@Min(CustomerIdConstraint.MIN_CUSTOMER_ID)
@Max(CustomerIdConstraint.MAX_CUSTOMER_ID)
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface CustomerIdConstraint {
    int MIN_CUSTOMER_ID = 1;
    int MAX_CUSTOMER_ID = 20_000;

    String message() default "invalid customer ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
