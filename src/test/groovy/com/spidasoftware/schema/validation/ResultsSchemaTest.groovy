package com.spidasoftware.schema.validation

class ResultsSchemaTest extends GroovyTestCase {

	def report
	Validator validator = new Validator()

	void testResultFiles(){
		def schema = "/schema/spidacalc/results/results.schema"
		[
			"/examples/spidacalc/results/simple-pole.json",
			"/examples/spidacalc/results/simple-pole-converted-from-6.4.json",
			"/examples/spidacalc/results/error.json",
			"/examples/spidacalc/results/error-converted-from-6.4.json"
		].each {
			report = validator.validateAndReport(schema, Validator.getResourceAsStream(it).text)
			assertTrue "${it} should be valid against the schema \n${report.toString()}", report.isSuccess()
		}
	}

}
