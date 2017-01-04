package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.uri.*
import net.sf.json.JSONObject
import org.apache.log4j.Logger;

class ResultsSchemaTest extends GroovyTestCase {

	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void testErrorResult(){
		def schema = factory.getJsonSchema("schema/spidacalc/results/results.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/results/error.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testSimpleResult(){
		def schema = factory.getJsonSchema("schema/spidacalc/results/results.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/results/simple-pole.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

}
