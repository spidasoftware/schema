package com.spidasoftware.schema.conversion

import com.spidasoftware.utils.json.JsonIO
import groovy.json.JsonSlurper
import spock.lang.Specification

class FormatConverterSpec extends Specification {

    def "test"() {
        setup:
            FormatConverter formatConverter = new FormatConverter()
            File workingDirectory = new File("/home/jeffseifert/tmp/components/")
            Map calcProject = [:]
            List<File> resultsFiles = []
        when:
            def components = formatConverter.convertCalcProject(calcProject, resultsFiles)
        then:
            components.size() > 0
    }

    def "test convert calc location"() {
        setup:
            FormatConverter formatConverter = new FormatConverter()
            File workingDirectory = new File("/home/jeffseifert/tmp/components/")
            Map calcProject = [:]
            List<File> resultsFiles = []
        when:
            def components = formatConverter.convertCalcProject(calcProject, resultsFiles)
        then:
            components.size() > 0
    }
}
