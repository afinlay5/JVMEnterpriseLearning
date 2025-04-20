package com.nimdec.api.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

// @AliasFor can't be used with Stereotype Annotations: @Component, @Service, @Repository, and @Controller

@Bean
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Trophy {

    @AliasFor(annotation = Bean.class, attribute = "name")
    String[] value() default {};

    // declares an alias for attribute 'name' in annotation [org.springframework.context.annotation.Bean] which is not meta-present.
    // This is what happens if you don't annotate this class with the target annotation (in this case, Bean.class).

}
