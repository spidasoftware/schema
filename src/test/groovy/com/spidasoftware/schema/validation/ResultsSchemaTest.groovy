package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.apache.log4j.Logger

class ResultsSchemaTest extends GroovyTestCase {

	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void testResultFiles(){
		def schema = factory.getJsonSchema("schema/spidacalc/results/results.schema")
		[
			new File("resources/examples/spidacalc/results/simple-pole.json"),
			new File("resources/examples/spidacalc/results/simple-pole-converted-from-6.4.json"),
			new File("resources/examples/spidacalc/results/error.json"),
			new File("resources/examples/spidacalc/results/error-converted-from-6.4.json")
		].each {
			report = schema.validate(JsonLoader.fromString(it.text))
			assertTrue "${it.name} should be valid against the schema \n${report.toString()}", report.isSuccess()
		}
	}

}
