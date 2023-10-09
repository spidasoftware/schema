package com.spidasoftware.schema.conversion

import com.spidasoftware.utils.json.JsonIO
import groovy.json.JsonSlurper
import spock.lang.Specification

class FormatConverterSpec extends Specification {

    def "test"() {
        setup:
            FormatConverter formatConverter = new FormatConverter()
            File workingDirectory = new File("/home/jeffseifert/tmp/components/")
            ExchangeFile exchangeFile = new ExchangeFile(workingDirectory)
        when:
            def components = formatConverter.convertCalcProject(exchangeFile)
        then:
            components.size() == 2
    }
}
