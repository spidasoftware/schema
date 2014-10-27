package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.spidasoftware.schema.utils.VersionUtils
import com.github.fge.jsonschema.uri.*
import org.apache.log4j.Logger;

class ClientSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class)
	def report

	void setUp() {
	}


	void testClientPoleObject(){
 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze();
    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		def schema = factory.getJsonSchema("v1/schema/spidacalc/client/pole.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v${VersionUtils.CURRENT_VERSION}/examples/spidacalc/client/client_pole_example.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "the instance should be valid against a schema", report.isSuccess()		
	}
}