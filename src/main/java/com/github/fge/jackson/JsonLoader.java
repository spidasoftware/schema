/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 *
 * Derived from jackson-coreutils 2.0 under the Apache Software License (ASL) version 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0.txt). Vendored here so the unmaintained
 * jackson-coreutils artifact (CVE-2026-86321, CVE-2026-86511, CVE-2026-86513) can be dropped.
 * {@code fromURL} is intentionally omitted (SSRF surface, CVE-2026-86321).
 */
package com.github.fge.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

/**
 * Loads JSON values from strings, readers, files, and classpath resources as {@link JsonNode}s.
 *
 * <p>Unlike a plain {@link ObjectMapper#readTree}, exactly one JSON text is accepted: empty input and
 * trailing data after the first value both raise a {@link JsonParseException}.</p>
 */
public final class JsonLoader {

    private static final ObjectReader READER = new ObjectMapper()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
        .readerFor(JsonNode.class);

    private JsonLoader() {
    }

    /**
     * @param resource classpath resource path; must begin with a {@code /}
     * @throws IOException the resource does not exist or is not a single valid JSON text
     */
    public static JsonNode fromResource(final String resource) throws IOException {
        if (resource == null) {
            throw new NullPointerException("resource");
        }
        if (!resource.startsWith("/")) {
            throw new IllegalArgumentException("resource path does not start with a '/'");
        }
        URL url = JsonLoader.class.getResource(resource);
        if (url == null) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            final ClassLoader classLoader = contextLoader != null ? contextLoader : JsonLoader.class.getClassLoader();
            if (classLoader == null) {
                throw new NullPointerException("no classloader available");
            }
            url = classLoader.getResource(resource.replaceFirst("^/+", ""));
        }
        if (url == null) {
            throw new IOException("resource " + resource + " not found");
        }
        try (InputStream in = url.openStream()) {
            return fromInputStream(in);
        }
    }

    public static JsonNode fromPath(final String path) throws IOException {
        try (InputStream in = new FileInputStream(path)) {
            return fromInputStream(in);
        }
    }

    public static JsonNode fromFile(final File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return fromInputStream(in);
        }
    }

    public static JsonNode fromReader(final Reader reader) throws IOException {
        try (JsonParser parser = READER.getFactory().createParser(reader);
             MappingIterator<JsonNode> iterator = READER.readValues(parser)) {
            return readSingleValue(iterator);
        }
    }

    public static JsonNode fromString(final String json) throws IOException {
        return fromReader(new StringReader(json));
    }

    private static JsonNode fromInputStream(final InputStream in) throws IOException {
        try (JsonParser parser = READER.getFactory().createParser(in);
             MappingIterator<JsonNode> iterator = READER.readValues(parser)) {
            return readSingleValue(iterator);
        }
    }

    private static JsonNode readSingleValue(final MappingIterator<JsonNode> iterator) throws IOException {
        if (!iterator.hasNextValue()) {
            throw new JsonParseException(iterator.getParser(), "no JSON Text to read from input");
        }
        final JsonNode ret = iterator.nextValue();
        if (iterator.hasNextValue()) {
            throw new JsonParseException(iterator.getParser(), "input has trailing data after first JSON Text",
                iterator.getCurrentLocation());
        }
        return ret;
    }
}
