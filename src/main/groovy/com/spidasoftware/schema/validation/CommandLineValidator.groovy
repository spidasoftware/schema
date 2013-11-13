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
			println "Usage: java -cp schema.jar com.spidasoftware.schema.validation.Validator /path/to/schema /path/to/json"
			println "   or"
			println("Usage: gradlew validateJson -Pschema=/path/to/schema -PjsonFile=/path/to/json")
			println("  schema - path to schema starting from resources. eg. /v1/schema/spidacalc/calc/structure.schema")
			println("  json - json file to be validated.")
			System.exit(-2)
		}

		Validator validator = new Validator();
		def json = new File(args[1]).text


		def report = validator.validateAndReport(args[0], json)
		if(!report.isSuccess()){
			report.messages.each { println it }
			println "JSON does not pass validation against the projects schema.  See Logs."
			System.exit(-1)
		} else {
			println ( "JSON file: " + args[1] + " passes validation against schema: " + args[0])
		}
	}
}
