package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import com.github.fge.jsonschema.processors.syntax.*
import org.apache.log4j.*
import com.github.fge.jackson.*

class SchemaValidationTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class)
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault())

	void setUp() {
	}
	
  	void testSchemasAreValid(){
  		new File("resources/v1/schema").eachFileRecurse(groovy.io.FileType.FILES){ schemaFile ->
  			if(schemaFile.absolutePath.endsWith(".schema")){
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				report.each{ log.info "${schemaFile.absolutePath} validation report "+it.toString() }
				assertTrue "the schema should be valid", report.isSuccess()
  			}
  		}
	}


}