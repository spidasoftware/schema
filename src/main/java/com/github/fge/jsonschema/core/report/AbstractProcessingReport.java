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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base implementation of a processing report. Subclasses implement {@link #log(LogLevel, ProcessingMessage)}.
 */
public abstract class AbstractProcessingReport implements ProcessingReport {

    private LogLevel currentLevel = LogLevel.DEBUG;

    private final LogLevel logLevel;

    private final LogLevel exceptionThreshold;

    protected AbstractProcessingReport(final LogLevel logLevel, final LogLevel exceptionThreshold) {
        this.logLevel = logLevel;
        this.exceptionThreshold = exceptionThreshold;
    }

    protected AbstractProcessingReport(final LogLevel logLevel) {
        this(logLevel, LogLevel.FATAL);
    }

    protected AbstractProcessingReport() {
        this(LogLevel.INFO, LogLevel.FATAL);
    }

    @Override
    public final LogLevel getLogLevel() {
        return logLevel;
    }

    @Override
    public final LogLevel getExceptionThreshold() {
        return exceptionThreshold;
    }

    @Override
    public final void debug(final ProcessingMessage message) throws ProcessingException {
        dispatch(message.setLogLevel(LogLevel.DEBUG));
    }

    @Override
    public final void info(final ProcessingMessage message) throws ProcessingException {
        dispatch(message.setLogLevel(LogLevel.INFO));
    }

    @Override
    public final void warn(final ProcessingMessage message) throws ProcessingException {
        dispatch(message.setLogLevel(LogLevel.WARNING));
    }

    @Override
    public final void error(final ProcessingMessage message) throws ProcessingException {
        dispatch(message.setLogLevel(LogLevel.ERROR));
    }

    @Override
    public final void fatal(final ProcessingMessage message) throws ProcessingException {
        dispatch(message.setLogLevel(LogLevel.FATAL));
    }

    @Override
    public final boolean isSuccess() {
        return currentLevel.compareTo(LogLevel.ERROR) < 0;
    }

    public abstract void log(LogLevel level, ProcessingMessage message);

    protected final void dispatch(final ProcessingMessage message) throws ProcessingException {
        final LogLevel level = message.getLogLevel();

        if (level.compareTo(exceptionThreshold) >= 0) {
            throw message.asException();
        }
        if (level.compareTo(currentLevel) > 0) {
            currentLevel = level;
        }
        if (level.compareTo(logLevel) >= 0) {
            log(level, message);
        }
    }

    @Override
    public Iterator<ProcessingMessage> iterator() {
        return Collections.<ProcessingMessage>emptyList().iterator();
    }

    @Override
    public final void mergeWith(final ProcessingReport other) throws ProcessingException {
        // The other report may carry no messages yet still be a failure.
        if (!other.isSuccess() && currentLevel.compareTo(LogLevel.ERROR) < 0) {
            currentLevel = LogLevel.ERROR;
        }
        for (final ProcessingMessage message : other) {
            dispatch(message);
        }
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getCanonicalName()).append(": ")
            .append(isSuccess() ? "success" : "failure").append('\n');
        final List<ProcessingMessage> messages = new ArrayList<>();
        for (final ProcessingMessage message : this) {
            messages.add(message);
        }
        if (!messages.isEmpty()) {
            sb.append("--- BEGIN MESSAGES ---\n");
            for (final ProcessingMessage message : messages) {
                sb.append(message);
            }
            sb.append("---  END MESSAGES  ---\n");
        }
        return sb.toString();
    }
}
