package demo.soa.misc.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import demo.soa.misc.validator.constraints.impl.MobileValidator;


@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy=MobileValidator.class)
public @interface Mobile {
	String message() default "mobile不是有效的手机号码格式";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};	
}
