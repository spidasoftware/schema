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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

/**
 * Interface for a processing report
 */
public interface ProcessingReport extends Iterable<ProcessingMessage> {

    LogLevel getLogLevel();

    LogLevel getExceptionThreshold();

    void debug(ProcessingMessage message) throws ProcessingException;

    void info(ProcessingMessage message) throws ProcessingException;

    void warn(ProcessingMessage message) throws ProcessingException;

    void error(ProcessingMessage message) throws ProcessingException;

    void fatal(ProcessingMessage message) throws ProcessingException;

    /**
     * A report is successful if no message with a level of {@link LogLevel#ERROR} or higher has been logged.
     */
    boolean isSuccess();

    void mergeWith(ProcessingReport other) throws ProcessingException;
}
