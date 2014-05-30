package com.spidasoftware.schema.client.rest

import groovy.transform.WithReadLock
import groovy.transform.WithWriteLock
import groovy.util.logging.Log4j

import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * This class holds resource-specific settings for a RestAPI and allows you to easily make
 * calls to the api. Typically, you would not directly create instances of this class, but rather
 * use the more fluent syntax of:
 * <code>
 *     def api = new RestAPI("http://www.bookstore.com/api", new GenericHttpClient())
 *     // if you had a resource called books at the path '/books'
 *     api.books.list(["author":"Mark Twain"])  // this will construct and execute the request and return the result as JSON
 * </code>
 * This will create a new resource and set it's path to /books and call it's list method with the given parameters.
 * Many use cases will be covered by this simple one-line approach, but sometimes you will need to override the default
 * settings. There are two simple ways of doing so:
 * First you can just set whatever you want like so:
 * <code>
 *     api.books.path = "/v2/books"  // creates a books resource if it doesn't already exist and set's it's path
 *     api.books.headers = ["Accept":"application/xml", "Content-Type":"application/xml"]
 *     // this second line overrides the default json headers so you can use xml for this resource.
 *
 *     //you can even set your own response handler to deal with parsing the xml
 *     api.books.doWithResponse = {response ->
 *          assert response instanceof org.apache.http.HttpResponse
 *          // <return response as an XML element
 *     }
 *
 *     //you can also set defaults for the whole api
 *     api.doWithResponse = {response->
 *
 *     }
 * </code>
 * The second way of overriding defaults is by specifying a directory to use for storing configuration files:
 * <code>
 *     def externalConfigDirectory = new File("/path/to/directory")
 *     RestAPI api = new RestAPI(url, httpClient, externalConfigDirectory)
 *     // alternatively, api.configDirectory = externalConfigDirectory
 *     api.books.find("12345")  //this will cause the books resource to use the settings found in <api.configDirectory>/books.config
 * </code>
 * These files are written using Groovy's ConfigSlurper syntax. Resource config files should have the same name as the
 * resource plus a '.config' extension
 *
 * All the resource settings, wherever they come from, will get merged with the default settings of the api, so there's
 * typically no need to do a lot of configuration for each individual resource.
 *
 *
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 8:22 PM
 */
@Log4j
class RestAPIResource {
	ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock()

	String name

	RestAPI parent

	/**
	 * Holds the Settings specific to this resource. Note that modifying these settings is not inherently
	 * thread safe. If you need to change any settings after the initialization of the resource, you'll need to
	 * get a copy of the settings by calling getSettings and
	 */
	private ConfigObject settings = new ConfigObject()

	RestAPIResource(String name, RestAPI parent) {
		this.parent = parent
		this.name = name
		reloadSettings()
	}

	/**
	 * Clears and reloads the settings for this resource. If an external config directory has been specified
	 * for the parent RestAPI, then the resource will look in there for the file <resource_name>.config
	 * If no config override is found, then it will just clear all the settings and set the name and default path.
	 */
	@WithWriteLock("settingsLock")
	void reloadSettings(){
		this.settings = new ConfigObject()
		this.settings.setProperty("name", name)
		this.settings.setProperty("path", "/"+name)
		ConfigObject overrides = getOverrideSettings()
		if (overrides) {
			this.settings.merge(overrides)
			log.info("Loaded settings for resource: ${name} from external config directory")
		} else {
			log.info("Settings for resource: ${name} have been set to defaults")
		}
	}

	protected ConfigObject getOverrideSettings(){
		if (parent.configDirectory) {
			File overrideFile = new File(parent.configDirectory, "${name}.config")
			if (overrideFile.exists()) {
				return new ConfigSlurper().parse(overrideFile.toURI().toURL())
			} else {
				log.warn("Config directory was specified, but no override was found for resource '${name}' at: ${parent.configDirectory.getAbsolutePath() + "/${name}.config"}")
			}

		}
		return null

	}

	/**
	 * Gets a specific resource by id.
	 * This will simply generate a GET request to <base url of parent>/<path>/id and add any
	 * additionalParams and headers, then call the appropriate event closures
	 *
	 * @param id the id of the resource to retrieve, gets appended to the end of the url as in host/path/<id>
	 * @return whatever is returned by the doWithFindResult closure - By default, will return a net.sf.json.JSONObject
	 *  or will throw an exception if the response is not valid JSON. This can all be easily overridden by adding
	 *  or changing the doWithResponse and/or doWithFindResult closures
	 */
	@WithReadLock("settingsLock")
	def find(String id) {
		def result = parent.find(settings, id)
		return result
	}

	/**
	 * Called to query for a list of resources.
	 * This will execute a GET request to <base url of parent>/<path> and add parameters and headers,
	 * then call the appropriate event closures.
	 *
	 * @param params a map of params to be sent with the request. These will be merged with any additionalParams
	 * if any are specified. Params passed in as an argument will always take precedence, though.
	 * @return whatever is returned by the doWithListResult closure - By default, will return a net.sf.json.JSONObject
	 *  or will throw an exception if the response is not valid JSON. This can all be easily overridden by adding
	 *  or changing the doWithResponse and/or doWithListResult closures
	 */
	@WithReadLock("settingsLock")
	def list(Map params) {
		return parent.list(settings, params)
	}

	/**
	 * Update the given resource.
	 * Executes a PUT request to <base url of parent>/<path>/<id> and adds parameters and headers,
	 * then call the appropriate event closures.
	 *
	 * @param id id of the resource to update - gets added to the url as host/<path>/<id>
	 * @param params a Map of request parameters. These will all get added to the request body by default. Handles files automatically
	 * @return  whatever is returned by the doWithUpdateResult closure - By default, will return a net.sf.json.JSONObject
	 *  or will throw an exception if the response is not valid JSON. This can all be easily overridden by adding
	 *  or changing the doWithResponse and/or doWithUpdateResult closures
	 */
	@WithReadLock("settingsLock")
	def update(String id, Map params) {
		return parent.update(settings, params, id)
	}

	/**
	 * Saves the given resource.
	 * Executes a POST request to <base url of parent>/<path> and adds parameters and headers,
	 * then calles the appropriate event closures.
	 *
	 * @param params Map of request parameters that gets merged with any additionalParams and added to the request body.
	 *  Handles files automatically.
	 * @return whatever is returned by the doWithSaveResult closure - By default, will return a net.sf.json.JSONObject
	 *  or will throw an exception if the response is not valid JSON. This can all be easily overridden by adding
	 *  or changing the doWithResponse and/or doWithSaveResult closures
	 */
	@WithReadLock("settingsLock")
	def save(Map params) {
		return parent.save(settings, params)
	}

	/**
	 * Deletes the resource with the given id
	 * Executes a DELETE request to <base url of parent>/<path>/<id>
	 *     and adds any additionalParams and headers, then calls the appropriate event closures.
	 *
	 * @param id the id of the resource to delete, gets added to the url as /<path>/<id>
	 * @return whatever is returned by the doWithDeleteResult closure - By default, will return a net.sf.json.JSONObject
	 *  or will throw an exception if the response is not valid JSON. This can all be easily overridden by adding
	 *  or changing the doWithResponse and/or doWithDeleteResult closures
	 */
	@WithReadLock("settingsLock")
	def delete(String id) {
		return parent.delete(settings, id)
	}


	@WithWriteLock("settingsLock")
	void setSettings(ConfigObject settings) {
		this.settings = settings;
	}

	/**
	 * returns a COPY of this settings object. If you modify anything you'll have to apply the changes by calling
	 * <code>setSettings()</code> with the modified settings object.
	 * @return
	 */
	@WithReadLock("settingsLock")
	ConfigObject getSettings(){
		return this.settings.clone()
	}

	/**
	 * allows settings to be added more easily/fluently. Don't call this method directly
	 */
	@WithWriteLock
	def propertyMissing(String name, value) {
		settings.setProperty(name, value)
		value
	}


	@WithReadLock
	def propertyMissing(String name) {
		this.settingsLock.readLock().lock()
		ConfigObject o = this.settings.clone()
		this.settingsLock.readLock().unlock()
		return o.getProperty(name)
	}

}
