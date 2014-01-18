package com.spidasoftware.schema.client.rest
/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
class RestAPIResource {
	String name

	RestAPI parent

	public ConfigObject settings = new ConfigObject()

	RestAPIResource(String name, RestAPI parent) {
		this(name, "/" + name, parent)
	}

	RestAPIResource(String name, String path, RestAPI parent) {
		this.parent = parent
		this.name = name
		settings.setProperty("name", name)
		settings.setProperty("path", path)

	}


	def find(String id) {
		def result = parent.find(settings, id)
		return result
	}

	def list(Map params) {
		return parent.list(settings, params)
	}

	def update(String id, Map params) {
		return parent.update(settings, params, id)
	}

	def save(Map params) {
		return parent.save(settings, params)
	}

	def delete(String id) {
		return parent.delete(settings, id)
	}

	def propertyMissing(String name, value) {
		settings.setProperty(name, value)
		value
	}

	def propertyMissing(String name) {
		def prop = settings.getProperty(name)
		if (!prop) {
			settings.setProperty(name, new ConfigObject())
			prop = settings.getProperty(name)
		}
		prop
	}






}
