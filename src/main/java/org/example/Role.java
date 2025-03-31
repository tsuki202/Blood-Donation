package org.example;
import java.lang.annotation.ElementType;
import java.lang.annotation.*;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Role {
    String value();
}
