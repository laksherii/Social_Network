package com.senla.resource_server.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GroupMemberValidator.class)
public @interface ValidGroupMember {
    String message() default "User is not an active member of this group";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
