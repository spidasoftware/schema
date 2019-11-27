package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.apache.log4j.Logger

class DamageSchemaTest extends GroovyTestCase {

	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
  final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void testHeartObject(){
		def instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}}'
		def schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		assertFalse "this instance should not be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "shellThickness":{"unit":"FOOT", "value":100}}'
		schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

    instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}}'
		schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

    instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}, "shellThickness":{"unit":"FOOT", "value":100}}}'
		schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

  void testShellObject(){
    def instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}}'
    def schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
    report = schema.validate(JsonLoader.fromString(instance))
    assertFalse "this instance should not be valid against the schema \n${report.toString()}", report.isSuccess()

    instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "circumference":{"unit":"FOOT", "value":100}}'
    schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
    report = schema.validate(JsonLoader.fromString(instance))
    assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

    instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}}'
    schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
    report = schema.validate(JsonLoader.fromString(instance))
    assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

    instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}, "circumference":{"unit":"FOOT", "value":100}}}'
    schema = factory.getJsonSchema("schema/spidacalc/calc/damage.schema")
    report = schema.validate(JsonLoader.fromString(instance))
    assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
  }

}
