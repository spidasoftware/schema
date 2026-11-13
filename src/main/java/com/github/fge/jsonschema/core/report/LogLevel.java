/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 *
 * Derived from json-schema-core 1.2.14 under the Apache Software License (ASL) version 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0.txt). Vendored here so the unmaintained
 * json-schema-core / jackson-coreutils artifacts can be dropped while keeping the
 * report API binary compatible for existing consumers.
 */
package com.github.fge.jsonschema.core.report;

/**
 * Message log levels
 */
public enum LogLevel {
    DEBUG("debug"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    FATAL("fatal"),
    NONE("none");

    private final String s;

    LogLevel(final String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
