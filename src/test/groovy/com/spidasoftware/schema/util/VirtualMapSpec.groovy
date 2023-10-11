package com.spidasoftware.schema.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class VirtualMapSpec extends Specification  {

  void "test empty constructor"() {
        given:
            Map map = getTestMap()
            VirtualMap vMap = new VirtualMap()
        when:
            vMap.putAll(map)
        then:
            vMap.get("label") == "20190110896"
        cleanup:
            vMap.discard()
    }

    void "test single arg constructor"() {
        given:
            Map map = getTestMap()
        when:
            VirtualMap vMap = new VirtualMap(map)
        then:
            vMap.get("label") == "20190110896"
        cleanup:
            vMap.discard()
    }

    void "test get"() {
        given:
            Map map = getTestMap()
            VirtualMap vMap = new VirtualMap(map)
        when:
            String label = vMap.get("label")
        then:
            label == "20190110896"
        cleanup:
            vMap.discard()
    }

    void "test put"() {
        given:
            Map map = getTestMap()
            VirtualMap vMap = new VirtualMap(map)
        when:
            vMap.put("label","123")
        then:
            vMap.get("label") == "123"
        cleanup:
            vMap.discard()
    }

    void "test size"() {
        given:
            Map vMap = new VirtualMap()
        expect:
            vMap.size() == 0
        cleanup:
            vMap.discard()
    }

    void "test get immutable"() {
        given:
            Map map = getTestMap()
            VirtualMap vMap = new VirtualMap(map)
        when:
            Object address = vMap.get("address")
        then:
            address instanceof Map
        when:
            address.putAt("number","123")
        then:
            thrown(UnsupportedOperationException)
        when:
            address["number"] = "123"
        then:
            thrown(UnsupportedOperationException)
        when:
            Collection collection = vMap.values()
            collection[0] = "replaced"
        then:
            thrown(MissingMethodException)
        cleanup:
            vMap.discard()
    }

    private Map getTestMap() {
        File f =  new File (this.getClass().getResource("/conversions/one-of-everything-project.json").toURI())
        JsonSlurper jsonSlurper = new JsonSlurper()
        jsonSlurper.parse(f)
    }
}
