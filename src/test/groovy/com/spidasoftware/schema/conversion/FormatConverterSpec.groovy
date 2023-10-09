package com.spidasoftware.schema.conversion

import com.spidasoftware.schema.validation.JSONServletException
import com.spidasoftware.schema.validation.Validator
import com.spidasoftware.utils.json.JsonIO
import groovy.json.JsonSlurper
import spock.lang.Specification

class FormatConverterSpec extends Specification {

    JsonSlurper jsonSlurper = new JsonSlurper()

    def "test"() {
        setup:
            FormatConverter formatConverter = new FormatConverter()
            Map calcProject = new JsonSlurper().parse(FormatConverterSpec.getResourceAsStream("/conversions/studio/project.json"))
            List<File> resultsFiles = []
        when:
            def components = formatConverter.convertCalcProject(calcProject, resultsFiles)
        then:
            components.size() > 0
    }

    def "test convert calc location"() {
        setup:
            FormatConverter formatConverter = new FormatConverter()
            Map calcProject = new JsonSlurper().parse(FormatConverterSpec.getResourceAsStream("/conversions/studio/project.json"))
            Map calcLocation = new JsonSlurper().parse(FormatConverterSpec.getResourceAsStream("/conversions/studio/location-3630533.json"))
            File resultsFile = new File(getClass().getResource("/conversions/studio/628fa8efd0bb6c664573e719.json").toURI())
            List<File> resultsFiles = [resultsFile]
        when:
            def components = formatConverter.convertCalcLocation(calcLocation, calcProject, resultsFiles)
        then:
            components.size() > 0
    }
}
