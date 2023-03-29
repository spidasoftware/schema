package com.spidasoftware.schema.validation

import groovy.json.JsonSlurper
import org.apache.log4j.Logger

class DamageSchemaTest extends GroovyTestCase {

	def report
	def log = Logger.getLogger(this.class)

	Validator validator = new Validator()

	void testHeartObject() {
		def instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}}'
		def schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertFalse "this instance should not be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "shellThickness":{"unit":"FOOT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"HEART", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}, "shellThickness":{"unit":"FOOT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testShellObject() {
		def instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}}'
		def schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertFalse "this instance should not be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "circumference":{"unit":"FOOT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		instance = '{"id":"idvalue", "attachHeight":{"unit":"FOOT", "value":100}, "damageType":"SHELL", "damageHeight":{"unit":"FOOT", "value":100}, "remainingSectionModulus":{"unit":"PERCENT", "value":100}, "circumference":{"unit":"FOOT", "value":100}}'
		schema = "/schema/spidacalc/calc/damage.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}
}
