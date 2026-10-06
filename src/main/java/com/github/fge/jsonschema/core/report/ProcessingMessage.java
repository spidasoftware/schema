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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * One processing message: an ordered map of string keys to {@link JsonNode} values plus a log level.
 *
 * <p>All mutation methods return {@code this}. Null values are stored as JSON null; null keys are ignored.</p>
 */
public final class ProcessingMessage {

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final Map<String, JsonNode> map = new LinkedHashMap<>();

    private LogLevel level;

    /**
     * Creates a message with a log level of {@link LogLevel#INFO}.
     */
    public ProcessingMessage() {
        setLogLevel(LogLevel.INFO);
    }

    public String getMessage() {
        return map.containsKey("message") ? map.get("message").textValue() : "(no message)";
    }

    public LogLevel getLogLevel() {
        return level;
    }

    public ProcessingMessage setMessage(final String message) {
        return put("message", message);
    }

    public ProcessingMessage setLogLevel(final LogLevel level) {
        this.level = Objects.requireNonNull(level, "log level must not be null");
        return put("level", level);
    }

    public ProcessingMessage put(final String key, final JsonNode value) {
        if (key == null) {
            return this;
        }
        if (value == null) {
            return putNull(key);
        }
        map.put(key, value.deepCopy());
        return this;
    }

    public ProcessingMessage put(final String key, final String value) {
        return value == null ? putNull(key) : put(key, FACTORY.textNode(value));
    }

    public ProcessingMessage put(final String key, final int value) {
        return put(key, FACTORY.numberNode(value));
    }

    /**
     * Stores {@code value.toString()} as a text node.
     */
    public <T> ProcessingMessage put(final String key, final T value) {
        return value == null ? putNull(key) : put(key, FACTORY.textNode(value.toString()));
    }

    /**
     * Stores each element's {@code toString()} in an array node.
     */
    public <T> ProcessingMessage put(final String key, final Iterable<T> values) {
        if (values == null) {
            return putNull(key);
        }
        final ArrayNode node = FACTORY.arrayNode();
        for (final T value : values) {
            node.add(value == null ? FACTORY.nullNode() : FACTORY.textNode(value.toString()));
        }
        return put(key, node);
    }

    private ProcessingMessage putNull(final String key) {
        if (key != null) {
            map.put(key, FACTORY.nullNode());
        }
        return this;
    }

    public JsonNode asJson() {
        final ObjectNode ret = FACTORY.objectNode();
        ret.setAll(map);
        return ret;
    }

    public ProcessingException asException() {
        return new ProcessingException(this);
    }

    /**
     * Format is {@code "<level>: <message>"} followed by one indented {@code "key: value"} line per
     * remaining entry, terminated by a newline. Consumers surface this text to users; keep it stable.
     */
    @Override
    public String toString() {
        final Map<String, JsonNode> tmp = new LinkedHashMap<>(map);
        final JsonNode node = tmp.remove("message");
        final String message = node == null ? "(no message)" : node.textValue();
        final StringBuilder sb = new StringBuilder().append(level).append(": ").append(message);
        for (final Map.Entry<String, JsonNode> entry : tmp.entrySet()) {
            sb.append("\n    ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.append('\n').toString();
    }
}
