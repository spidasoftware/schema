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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * {@link List}-based implementation of a {@link ProcessingReport}
 */
public final class ListProcessingReport extends AbstractProcessingReport {

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final List<ProcessingMessage> messages = new ArrayList<>();

    public ListProcessingReport(final LogLevel logLevel, final LogLevel exceptionThreshold) {
        super(logLevel, exceptionThreshold);
    }

    public ListProcessingReport(final LogLevel logLevel) {
        super(logLevel);
    }

    public ListProcessingReport() {
    }

    public ListProcessingReport(final ProcessingReport other) {
        this(other.getLogLevel(), other.getExceptionThreshold());
    }

    @Override
    public void log(final LogLevel level, final ProcessingMessage message) {
        messages.add(message);
    }

    /**
     * Logged messages, in order. Exposed explicitly because Groovy callers rely on {@code report.messages}.
     */
    public List<ProcessingMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public JsonNode asJson() {
        final ArrayNode ret = FACTORY.arrayNode();
        for (final ProcessingMessage message : messages) {
            ret.add(message.asJson());
        }
        return ret;
    }

    @Override
    public Iterator<ProcessingMessage> iterator() {
        return getMessages().iterator();
    }
}
