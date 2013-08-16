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

class SchemaValidationTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault());

	void setUp() {
		org.apache.log4j.BasicConfigurator.configure();
		log.setLevel(Level.INFO);
	}
	
  	void testSchemasAreValid(){
  		new File("v2/schema").eachFileRecurse(groovy.io.FileType.FILES){ schemaFile ->
  			if(schemaFile.absolutePath.endsWith(".schema")){
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				report.each{ log.info "${schemaFile.absolutePath} validation report "+it.toString() }
				assertTrue "the schema should be valid", report.isSuccess()
  			}
  		}
	}


}