package com.spidasoftware.schema.validation

import org.apache.log4j.*

class LoggingTest extends GroovyTestCase {

	def log = Logger.getLogger(this.class)

	void test(){
		def testLogMessage = "this text should appear in stdout and the log file"
		log.info(testLogMessage)

		def fileAppender = Logger.getRootLogger().getAppender("logfile");
		assert fileAppender

		def logfile = new File(fileAppender.getFile())
		assert logfile.exists()
		assert logfile.text.contains(testLogMessage)
	}
}
