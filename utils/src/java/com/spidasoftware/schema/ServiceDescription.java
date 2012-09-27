package com.spidasoftware.schema;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceDescription {
    String id() default "[unassigned]";
    String description() default "[unassigned]";
}