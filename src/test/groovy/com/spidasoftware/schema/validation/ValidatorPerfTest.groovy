/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class ValidatorPerfTest extends Specification {

	def test() {
		setup:
			File tempdir = Files.createTempDir()
			(new AntBuilder()).unzip(src: "/Users/jasongarrett/Downloads/SeveralAnalyzedDesigns-v7.2.spida", dest: tempdir.getCanonicalPath(), encoding: 'UTF-8')
			File projectFile = new File(tempdir, "project.json")
			List<File> resultsFiles = (new File(tempdir, "Results")).listFiles()

			Validator validator = new Validator()

			long totalTime = 0
			int iterations = 10
			iterations.times {
				Date startTime = new Date()
				validator.validateAndReport("/schema/spidacalc/calc/project.schema", parseFileToJsonNode(projectFile))
				resultsFiles.each {
					validator.validateAndReport("/schema/spidacalc/results/results.schema", parseFileToJsonNode(it))
				}
				Date endTime = new Date()
				long iterationTime = endTime.time - startTime.time
				log.warn("iteration time: " + iterationTime)
				totalTime += iterationTime
			}
		log.warn("total time: " + totalTime)
	}

	static Object parseFile(File file) {
		ObjectMapper mapper = new ObjectMapper()
		JsonNode node = mapper.readValue(file, JsonNode)
		return nodeToValue(mapper, node)
	}

	static JsonNode parseFileToJsonNode(File file) {
		ObjectMapper mapper = new ObjectMapper()
		JsonNode node = mapper.readValue(file, JsonNode)
		return node
	}

	protected static Object nodeToValue(ObjectMapper mapper, JsonNode node) {
		if (node.isArray()) {
			return mapper.treeToValue(node, List)
		} else if (node.isObject()) {
			return mapper.treeToValue(node, LinkedHashMap)
		} else {
			return node.asText()
		}
	}
}
