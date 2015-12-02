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

	void testOneOfEverythingConverted(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/results/results.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/results/one_of_everything.json").text))
		report.each{ log.info "testOneOfEverythingConverted validation report "+it.toString() }
		println report.toString()
		assertTrue "this instance should be valid against the schema", report.isSuccess()
	}

}
