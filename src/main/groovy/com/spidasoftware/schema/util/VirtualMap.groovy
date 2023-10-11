package com.spidasoftware.schema.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Path

/**
 * Map class implementation backed by temporary file to minimize memory usage.
 */
class VirtualMap implements Map {

    static final String PREFIX = "temp-"
    static final String SUFFIX = ".vmap"

    File file

    VirtualMap() {
    }

    VirtualMap(Map map) {
        if (map) {
            putAll(map)
        }
    }

    @Override
    int size() {
        return file ? getFileMap().size() : 0
    }

    @Override
    boolean isEmpty() {
        return getFileMap().isEmpty()
    }

    @Override
    boolean containsKey(Object key) {
        return getFileMap().containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        return getFileMap().containsValue(value)
    }

    @Override
    Object get(Object key) {
        Object object = getFileMap()?.get(key)
        return (object instanceof Map | object instanceof List) ? object.asImmutable() : object
    }

    @Override
    Object put(Object key, Object value) {
        Map map = new HashMap(getFileMap())
        map.put(key,value)
        save(map)
        return map.asImmutable()
    }

    @Override
    Object remove(Object key) {
        return getFileMap().remove(key)
    }

    @Override
    void putAll(Map m) {
        save(m)
    }

    @Override
    void clear() {
        getFileMap().clear()
    }

    @Override
    Set keySet() {
        return getFileMap().keySet()
    }

    @Override
    Collection values() {
        return getFileMap().values().asImmutable()
    }

    @Override
    Set<Entry> entrySet() {
        return getFileMap().entrySet().asImmutable()
    }

    private Map getFileMap() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map map = file ? jsonSlurper.parse(file) : [:]
        return map.asImmutable()
    }

    private void save(Map map) {
        if (!file) {
            Path path = Files.createTempFile(PREFIX, SUFFIX)
            file = path.toFile()
            file.deleteOnExit()
        }
        file.write(JsonOutput.toJson(map))
    }

    private void discard() {
        if (file) {
            Files.deleteIfExists(file.toPath())
        }
    }
}
