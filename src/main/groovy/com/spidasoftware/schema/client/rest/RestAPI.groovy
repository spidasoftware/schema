package com.spidasoftware.schema.client.rest

import com.spidasoftware.schema.client.HttpClientInterface
import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.apache.http.util.EntityUtils

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class RestAPI {
	HttpClientInterface client
    String baseUrl

    List<RestAPIResource> resources = []

	def defaults = new ConfigObject()

	RestAPI(String baseUrl, HttpClientInterface client) {
        this.baseUrl = baseUrl
		this.client = client
		this.defaults.doWithResponse = getResultAsJSON
		this.defaults.doWithFindResult = { return it }
		this.defaults.doWithListResult = { return it }
		this.defaults.doWithSaveResult = { return it }
		this.defaults.doWithUpdateResult = { return it }
		this.defaults.doWithDeleteResult = { return it }
    }

	def find(ConfigObject settings, String id) {
		def config = mergeConfig(settings)

		URI uri = new URI(baseUrl + config.path + "/" + id)
		Map headers = config.headers
		def result = client.executeRequest("GET", uri, config.additionalParams, headers, config.doWithResponse)
		return config.doWithFindResult.call(result)
	}

	def list(ConfigObject settings, Map params) {
		def config = mergeConfig(settings)
		URI uri = new URI(baseUrl + config.path)
		Map headers = config.headers
		def result = client.executeRequest("GET", uri, mergeParams(params, config.additionialParams), headers, config.doWithResponse)
		return config.doWithListResult.call(result)
	}

	def update(ConfigObject settings, Map params, String id) {
		def config = mergeConfig(settings)
		URI uri = new URI(baseUrl + config.path + "/" + id)
		Map headers = config.headers
		def result = client.executeRequest("PUT", uri, mergeParams(params, config.additionalParams), headers, config.doWithResponse)

		return config.doWithUpdateResult.call(result)
	}

	def save(ConfigObject settings, Map params) {
		def config = mergeConfig(settings)
		URI uri = new URI(baseUrl + config.path)
		Map headers = config.headers

		def result = client.executeRequest("POST", uri, mergeParams(params, config.additionalParams), headers, config.doWithResponse)

		return config.doWithSaveResult.call(result)
	}

	def delete(ConfigObject settings, String id) {
		def config = mergeConfig(settings)
		URI uri = new URI(baseUrl + config.path + "/" + id)
		Map headers = config.headers
		def result = client.executeRequest("DELETE", uri, config.additionalParams, headers, config.doWithResponse)

		return config.doWithDeleteResult.call(result)

	}

	protected static Map mergeParams(Map originalParams, Map additional) {
		if (additional && !additional.isEmpty()) {
			return additional.plus(originalParams)
		}
		return originalParams
	}

	def getResultAsJSON = { response ->
		def sc = response.getStatusLine().getStatusCode()
		JSON json

		try {
			json = JSONSerializer.toJSON(EntityUtils.toString(response.getEntity()))
		} catch (Exception e) {
			throw new RestClientException("Response was not valid JSON", e)
		}
		JSONObject j = new JSONObject()
		j.put("status", sc)
		j.put("json", json)
		return j

	}


	ConfigObject mergeConfig(ConfigObject resourceSettings) {
		def d = this.defaults.clone()

		d.merge(resourceSettings)
		return d
	}

	def propertyMissing(String name) {
		def res = this.resources.find{ it.name == name }
		if (!res) {
			res = new RestAPIResource(name, this)
			this.resources.add(res)
		}

		return res
	}

	def propertyMissing(String name, value) {
		if (value instanceof RestAPIResource) {
			def res = this.resources.find {it.name == name }
			if (res) { this.resources.remove(res) }
			this.resources.add(res)
			return res
		} else {
			throw new NoSuchFieldException("No such field: $name for class RestAPI")
		}

	}
}
