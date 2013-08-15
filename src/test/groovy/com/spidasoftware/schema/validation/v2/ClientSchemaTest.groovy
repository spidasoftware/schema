package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import org.apache.log4j.*
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.*
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;

class ClientSchemaValidationTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class)
	def report

	void setUp() {
		//org.apache.log4j.BasicConfigurator.configure();
		//log.setLevel(Level.INFO);
	}


	void testClientPoleObject(){
 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v1").toURI().toString()).freeze();
    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		def schema = factory.getJsonSchema("spidacalc/client/pole.schema")
		report = schema.validate(JsonLoader.fromString(new File("fixtures/spidacalc/client/client_pole_example.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}
}