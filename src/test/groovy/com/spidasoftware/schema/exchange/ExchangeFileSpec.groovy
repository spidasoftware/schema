package com.spidasoftware.schema.exchange

import net.sf.json.JSONObject
import spock.lang.Specification

/**
 * Created by pfried on 7/29/14.
 */


public class ExchangeFileSpec extends Specification {

	void "ExchangeFiles should be created from zip files"(){
		setup:
		File zip = new File(getClass().getResource("/formats/exchange/exchangeTestProject.exchange.spida").toURI())

		when:
		ExchangeFile exf = ExchangeFile.createFromZipFile(zip)

		then:
		exf.getProjectJSONFile().isFile()
		exf.getPhotoDir().isDirectory()
		exf.getAllPhotos().size() == 2
		exf.getProjectJSON() instanceof JSONObject

		cleanup:
		exf.delete()
	}

	void "exchange files should be created from JSON and written out to a zip file"(){
		given:
		JSONObject json = JSONObject.fromObject(['name': 'testProject', 'clientFile':'Demo.client', 'leads':[]])
		File mockPhoto = File.createTempFile("mockPhoto", ".jpg")
		mockPhoto << "mockPhotoBytes"
		ExchangeFile exf = ExchangeFile.createFromProjectJSON(json, [mockPhoto])
		File tmpDir = new File(System.getProperty('java.io.tmpdir'))
		File zipFile = File.createTempFile("test", ".exchange.spida")//new File(tmpDir, "test.exchange.spida")
		println "zipFile w= ${zipFile.canWrite()}"
		exf.writeTo(zipFile)

		expect:
		ExchangeFile newExf = ExchangeFile.createFromZipFile(zipFile)
		def newJson = newExf.getProjectJSON()
		newJson.name == "testProject"
		newExf.getAllPhotos().size() == 1
		zipFile.delete()
		exf.delete()
		newExf.delete()
	}

}