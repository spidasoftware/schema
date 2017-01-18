package com.spidasoftware.schema.conversion.changeset

import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import spock.lang.Specification
/**
 * Created: 11/17/14
 * Copyright SPIDAWeb
 */
@Log4j

class ConverterTest extends Specification {

	JSONObject json
	AbstractConverter converter

	ChangeSet oneToTwo
	ChangeSet twoToThreeA
	ChangeSet twoToThreeB

	void setup() {
		converter = new ProjectConverter()
		json = new JSONObject()

		oneToTwo = GroovyMock()
		twoToThreeA = GroovyMock()
		twoToThreeB = GroovyMock()

		converter.versions = [2:[oneToTwo], 3: [twoToThreeA, twoToThreeB]] as TreeMap<Integer, List<ChangeSet>>
	}

	def "Convert1-2"() {


		when:
			json.put("version", 1)
			converter.convert(json, 2)
		then:
			1 * oneToTwo.apply(json)
			0 * _._ // no others should be applied
	}
	def "Convert1-3"() {
		when:
			json.put("version", 1)
			converter.convert(json, 3)
		then:
			1 * oneToTwo.apply(json)
		then:
			1 * twoToThreeA.apply(json)
		then:
			1 * twoToThreeB.apply(json)
			0 * _._ // no others should be applied
	}
	def "convert 2-3"() {
		when:
			json.put("version", 2)
			converter.convert(json, 3)
		then:
			1 * twoToThreeA.apply(json)
		then:
			1 * twoToThreeB.apply(json)
			0 * _._ // no others should be applied
	}

	def "convert 3-2"() {
		when:
			json.put("version", 3)
			converter.convert(json, 2)
		then:
			1 * twoToThreeB.revert(json)
		then:
			1 * twoToThreeA.revert(json)
			0 * _._ // no others should be applied
	}

	def "convert 3-1"() {
		when:
			json.put("version", 3)
			converter.convert(json, 1)
		then:
			1*twoToThreeB.revert(json)
		then:
			1*twoToThreeA.revert(json)
		then:
			1*oneToTwo.revert(json)
			0 * _._ // no others should be applied
	}
}
