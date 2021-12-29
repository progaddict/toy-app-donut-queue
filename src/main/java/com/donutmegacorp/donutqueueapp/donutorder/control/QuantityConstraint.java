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
@Min(QuantityConstraint.MIN_QUANTITY)
@Max(QuantityConstraint.MAX_QUANTITY)
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface QuantityConstraint {
    int MIN_QUANTITY = 1;
    int MAX_QUANTITY = 50;

    String message() default "invalid quantity";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
