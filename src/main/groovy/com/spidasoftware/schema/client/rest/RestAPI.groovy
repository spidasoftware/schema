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
	boolean loadOverrides

    List<RestAPIResource> resources = []

	ConfigObject defaults



	RestAPI(String baseUrl, HttpClientInterface client) {
        this(baseUrl, client, true)

    }

	RestAPI(String baseUrl, HttpClientInterface client, boolean loadOverrides) {
		this.baseUrl = baseUrl
		this.client = client
		this.loadOverrides = loadOverrides
		loadDefaults()
	}

	void loadDefaults(){
		this.defaults = getDefaultConfigFromResources()
		log.debug("Loading configuration for RestAPI for: ${this.baseUrl}")
		if (loadOverrides) {
			File defaultConfigFile = getOverrideConfigFile()
			if (defaultConfigFile && defaultConfigFile.exists()) {
				try {

					def config = new ConfigSlurper().parse(defaultConfigFile.toURI().toURL())
					log.info("RestAPI defaults have been overridden by ${defaultConfigFile.getCanonicalPath()}")
					this.defaults.merge(config)
					return

				} catch (Exception e) {
					log.error("Error loading default config", e)
				}
			} else if (defaultConfigFile) {
				log.warn("RestAPI config directory was specified, but no config file was found! Using built-in defaults instead.")
			}
		}
	}

	/**
	 * Returns the default config file for this api or null if none was specified
	 * @return the File, whether it exists or not.
	 */
	File getOverrideConfigFile(){
		File configDir = getConfigDirectory()
		if (configDir) {
			return new File(configDir, "defaults.config")
		}
		return null
	}

	/**
	 * returns the directory used to store the config files for this api, or null if no system property is set
	 * @return config dir for this api whether it exists or not, null if no system property is set
	 */
	File getConfigDirectory(){
		def configPath = System.getProperty("spidasoftware.rest.client.config.dir")
		if (!configPath) {
			log.debug("RestAPI config override path not specified")
			return null
		}
		File baseConfigDir = new File(configPath)

		String host = new URI(baseUrl).getHost()
		log.debug("RestAPI config path is: ${baseConfigDir.getCanonicalPath()}/${host}")
		return  new File(baseConfigDir, host)
	}

	protected ConfigObject getDefaultConfigFromResources(){
		log.info("Using default configuration for RestAPI")

		URL configUrl = getClass().getResource("/client/rest/defaults.config")
		def config = new ConfigSlurper().parse(configUrl)
		return config
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
		def result = client.executeRequest("GET", uri, mergeParams(params, config.additionalParams), headers, config.doWithResponse)
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
