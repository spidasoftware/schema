package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import com.github.fge.jsonschema.processors.syntax.*
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.github.fge.jackson.*

class CalcSchemaValidationTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault());

	void setUp() {
		//org.apache.log4j.BasicConfigurator.configure();
		//log.setLevel(Level.INFO);
	}


  	void testThatAllCalcItemsAreValidSchema(){
  		for(schemaFile in new File("v1/spidacalc/calc").listFiles()){
  			if(schemaFile.absolutePath.endsWith(".schema")){
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				report.each{
					log.info "${schemaFile.absolutePath}: "+it.toString()
				}
				assertTrue "the schema itself should be true", report.isSuccess()
  			}
  		}
	}

  	void testThatAllCalcClientReferencesItemsAreValidSchema(){
  		for(schemaFile in new File("v1/spidacalc/calc/client_references").listFiles()){
  			if(schemaFile.absolutePath.endsWith(".schema")){
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				report.each{
					log.info "${schemaFile.absolutePath}: "+it.toString()
				}
				assertTrue "the schema itself should be true", report.isSuccess()
  			}
  		}
	}

	void testThatAllCalcEnumsItemsAreValidSchema(){
  		for(schemaFile in new File("v1/spidacalc/calc/enums").listFiles()){
  			if(schemaFile.absolutePath.endsWith(".schema")){
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				report.each{
					log.info "${schemaFile.absolutePath}: "+it.toString()
				}
				assertTrue "the schema itself should be true", report.isSuccess()
  			}
  		}
	}

}