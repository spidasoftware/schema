package com.spidasoftware.schema.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class VirtualMapSpec extends Specification  {

  void "test empty constructor"() {
        given:
            Map map = getTestMap()
            VirtualMap vMap = new VirtualMap()
        when:
            vMap.put(map)
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

    private Map getTestMap() {
        File f =  new File (this.getClass().getResource("/conversions/studio/project-1.json").toURI())
        JsonSlurper jsonSlurper = new JsonSlurper()
        jsonSlurper.parse(f)
    }
}
