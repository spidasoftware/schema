package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import com.github.fge.jackson.*

class GeneralSchemaValidationTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class)
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault())

	void setUp() {
		//org.apache.log4j.BasicConfigurator.configure();
		//log.setLevel(Level.INFO);
	}

	void testThatAddressIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/geometry.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message address: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}

	void testThatBearingIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/bearing.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message bearing: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}
	void testThatGeometryIsValidSchema(){

		def instance =  '{"type": "POINT", "coordinates": []}'
		def schemaNode = JsonLoader.fromPath("v1/general/geometry.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message geometry: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()

		def schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode)
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "Validation message geometry: "+it.toString()
		}    
		assertTrue "test item should assert true against schema", report.isSuccess()
	}

	void testThatIdIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/id.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message id: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}

	void testThatMeasurableIsValidSchema(){
		def instance =  '{"unit": "FOOT", "value": 10}'
		def schemaNode = JsonLoader.fromPath("v1/general/measurable.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message measurable: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()

		def schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode)

		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "Validation message measurable: "+it.toString()
		}
		assertTrue "test item should assert true against schema", report.isSuccess()
	}  

	void testThatMethodResponseIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/method_response.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message method reponse: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}    

	void testThatOwnerIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/owner.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message owner: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}  

	void testThatServiceMethodIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("v1/general/service_method.schema")
		report = schemaValidator.validateSchema(schemaNode)
		report.each{
			log.info "Validation message service method: "+it.toString()
		}
		assertTrue "the schema itself should be true", report.isSuccess()
	}

}