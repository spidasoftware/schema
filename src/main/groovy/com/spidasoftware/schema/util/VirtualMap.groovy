package com.spidasoftware.schema.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Path

/**
 * Map class backed by temporary file to minimize memory usage.
 */
class VirtualMap {
    static final String PREFIX = "temp-"
    static final String SUFFIX = ".vmap"

    File file

    VirtualMap() {
    }

    VirtualMap(Map map) {
        if (map) {
            put(map)
        }
    }

    Map get() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        file ? jsonSlurper.parse(file) : [:]
    }

    void put(Map map) {
        if (!file) {
            Path path = Files.createTempFile(PREFIX, SUFFIX)
            file = path.toFile()
            save(map)
        }
    }

    Object get(Object key) {
        get()?.get(key)
    }

    Object put(Object key, Object value) {
        Map map = get()
        map.put(key,value)
        save(map)
    }

    private void save(Map map) {
        String json = JsonOutput.toJson(map)
        file.write(json)
    }

    private void discard() {
        Files.deleteIfExists(file.toPath())
    }
}
