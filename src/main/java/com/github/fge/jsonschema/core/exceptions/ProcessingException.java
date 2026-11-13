/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 *
 * Derived from json-schema-core 1.2.14 under the Apache Software License (ASL) version 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0.txt). Vendored here so the unmaintained
 * json-schema-core / jackson-coreutils artifacts can be dropped while keeping the
 * report API binary compatible for existing consumers.
 */
package com.github.fge.jsonschema.core.exceptions;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;

/**
 * Generic processing exception. Carries a {@link ProcessingMessage} whose level is always {@link LogLevel#FATAL}.
 */
public class ProcessingException extends Exception {

    private static final long serialVersionUID = -4194415456857460489L;

    private final ProcessingMessage processingMessage;

    public ProcessingException() {
        this(new ProcessingMessage().setLogLevel(LogLevel.FATAL));
    }

    public ProcessingException(final String message) {
        this(new ProcessingMessage().setMessage(message).setLogLevel(LogLevel.FATAL));
    }

    public ProcessingException(final ProcessingMessage message) {
        processingMessage = message.setLogLevel(LogLevel.FATAL);
    }

    public ProcessingException(final String message, final Throwable e) {
        processingMessage = new ProcessingMessage().setLogLevel(LogLevel.FATAL)
            .setMessage(message)
            .put("exceptionClass", e.getClass().getName())
            .put("exceptionMessage", e.getMessage());
    }

    public ProcessingException(final ProcessingMessage message, final Throwable e) {
        processingMessage = message.setLogLevel(LogLevel.FATAL)
            .put("exceptionClass", e.getClass().getName())
            .put("exceptionMessage", e.getMessage());
    }

    @Override
    public final String getMessage() {
        return processingMessage.toString();
    }

    public final String getShortMessage() {
        return processingMessage.getMessage();
    }

    public final ProcessingMessage getProcessingMessage() {
        return processingMessage;
    }
}
