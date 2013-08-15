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

class MinSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	//private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault());

	void setUp() {
		// org.apache.log4j.BasicConfigurator.configure();
		// log.setLevel(Level.INFO);
	}

	void testBasicProjectObject(){
		def instance = '{"id":1,"flowId":1}'				
		def fileUri = new File("v1").toURI().toString()
 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(fileUri).freeze();
    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		def schema = factory.getJsonSchema("spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "point using url: "+it.toString()
		}
		assertTrue "the intance itself should be true against a file namespace", report.isSuccess()
	}	

	void testFullProjectObject(){
		def instance = new File("fixtures/spidamin/project.json").text				
		def fileUri = new File("v1").toURI().toString()
 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(fileUri).freeze();
    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		def schema = factory.getJsonSchema("spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "point using url: "+it.toString()
		}
		assertTrue "the intance itself should be true against a file namespace", report.isSuccess()
	}
}