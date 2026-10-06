/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ListProcessingReport
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import spock.lang.Specification

/**
 * Pins the vendored com.github.fge report API to the exact surface downstream consumers (Studio) rely on,
 * including toString() formats that are shown to end users.
 */
class ProcessingReportCompatibilitySpec extends Specification {

	void "empty report is a success and iterates nothing"() {
		given:
			ProcessingReport report = new ListProcessingReport()
		expect:
			report.isSuccess()
			report.success
			report.toList().isEmpty()
			((ListProcessingReport) report).messages.isEmpty()
			report.toString() == 'com.github.fge.jsonschema.core.report.ListProcessingReport: success\n'
	}

	void "error messages fail the report and are exposed via messages, iteration and getMessage"() {
		given:
			ListProcessingReport report = new ListProcessingReport()
			report.error(new ProcessingMessage().setMessage('first problem'))
			report.error(new ProcessingMessage().setMessage('second problem'))
		expect:
			!report.isSuccess()
			report.messages.size() == 2
			report.messages.get(0).message == 'first problem'
			report.messages*.logLevel == [LogLevel.ERROR, LogLevel.ERROR]
			report.collect { it.message } == ['first problem', 'second problem']
			report.toList().first().toString() == 'error: first problem\n    level: "error"\n'
	}

	void "info and warn messages do not fail the report"() {
		given:
			ListProcessingReport report = new ListProcessingReport()
			report.info(new ProcessingMessage().setMessage('fyi'))
			report.warn(new ProcessingMessage().setMessage('careful'))
		expect:
			report.isSuccess()
			report.messages.size() == 2
	}

	void "ProcessingMessage toString uses the level-prefixed format consumers display"() {
		given:
			ProcessingMessage message = new ProcessingMessage().setMessage('bad value').setLogLevel(LogLevel.ERROR)
		expect:
			message.toString() == 'error: bad value\n    level: "error"\n'
			message.asJson().get('message').textValue() == 'bad value'
			message.asJson().get('level').textValue() == 'error'
	}

	void "report toString lists messages between markers"() {
		given:
			ListProcessingReport report = new ListProcessingReport()
			report.error(new ProcessingMessage().setMessage('oops'))
		expect:
			report.toString() == 'com.github.fge.jsonschema.core.report.ListProcessingReport: failure\n' +
				'--- BEGIN MESSAGES ---\n' +
				'error: oops\n    level: "error"\n' +
				'---  END MESSAGES  ---\n'
	}

	void "messages list is read-only"() {
		given:
			ListProcessingReport report = new ListProcessingReport()
		when:
			report.messages.add(new ProcessingMessage())
		then:
			thrown(UnsupportedOperationException)
	}

	void "fatal messages raise ProcessingException carrying the message"() {
		given:
			ListProcessingReport report = new ListProcessingReport()
		when:
			report.fatal(new ProcessingMessage().setMessage('boom'))
		then:
			ProcessingException e = thrown()
			e.shortMessage == 'boom'
			e.processingMessage.logLevel == LogLevel.FATAL
			e.message == 'fatal: boom\n    level: "fatal"\n'
	}

	void "mergeWith propagates failure"() {
		given:
			ListProcessingReport failed = new ListProcessingReport()
			failed.error(new ProcessingMessage().setMessage('nope'))
			ListProcessingReport target = new ListProcessingReport()
		when:
			target.mergeWith(failed)
		then:
			!target.isSuccess()
			target.messages*.message == ['nope']
	}

	void "JsonLoader reads a single JSON text from a string"() {
		when:
			JsonNode node = JsonLoader.fromString('{"a": 1.10, "b": [true]}')
		then:
			node.get('a').decimalValue() == new BigDecimal('1.10')
			node.get('b').get(0).booleanValue()
	}

	void "JsonLoader rejects empty input and trailing data"() {
		when:
			JsonLoader.fromString(input)
		then:
			thrown(JsonParseException)
		where:
			input << ['', '   ', '[]]]', '{} {}']
	}

	void "JsonLoader reads classpath resources"() {
		when:
			JsonNode node = JsonLoader.fromResource('/schema/spidacalc/calc/project-v4.schema')
		then:
			node.isObject()
		when:
			JsonLoader.fromResource('/does/not/exist.json')
		then:
			thrown(IOException)
		when:
			JsonLoader.fromResource('no/leading/slash.json')
		then:
			thrown(IllegalArgumentException)
	}

	void "JsonLoader does not expose URL loading"() {
		expect:
			!JsonLoader.methods.any { it.name == 'fromURL' }
	}
}
