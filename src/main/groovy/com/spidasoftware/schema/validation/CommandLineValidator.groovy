/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

/**
 * A script to run the json validator from the command line.
 * User: mford
 * Date: 10/30/13
 * Time: 11:31 AM
 * Copyright SPIDAWeb
 */
class CommandLineValidator {

	public static void main(String[] args) {
		if (args.length < 2) {
			println "Usage: java -cp schema.jar com.spidasoftware.schema.validation.CommandLineValidator /path/to/schema /path/to/json"
			println "   or"
			println("Usage: gradlew validateJson -Pschema=/path/to/schema -PjsonFile=/path/to/json")
			println("  schema - resource path to schema. eg. /schema/spidacalc/calc/structure.schema")
			println("  json - json file to be validated. Relative paths are resolved from the project directory.")
			println("  Add \"strict\": true to the json to also reject additional properties.")
			System.exit(-2)
		}

		File jsonFile = new File(args[1])
		if (!jsonFile.isFile()) {
			println "JSON file not found: ${jsonFile.absolutePath}"
			System.exit(-2)
		}

		Validator validator = new NetworkNtJsonValidator()
		def report = validator.validateAndReport(args[0], jsonFile.text)
		if (report == null) {
			println "Could not validate ${args[1]} against schema ${args[0]}. Check that the schema path exists and the file is valid JSON."
			System.exit(-1)
		}
		if(!report.isSuccess()){
			report.messages.each { println it }
			println "JSON does not pass validation against the projects schema.  See Logs."
			System.exit(-1)
		} else {
			println ( "JSON file: " + args[1] + " passes validation against schema: " + args[0])
		}
	}
}
