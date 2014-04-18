package com.spidasoftware.schema.utils

import com.spidasoftware.schema.changesets.CalcAddressChangeSet
import com.spidasoftware.schema.changesets.ChangeSet
import com.spidasoftware.schema.changesets.AmStationIdChangeSet
import com.spidasoftware.schema.validation.Validator
import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper


/**
 * Checks json version and runs change sets if needed.
 */
@Log4j
class JsonUpdater {

	List availableChangeSets = [AmStationIdChangeSet, CalcAddressChangeSet]

	/**
	 * Updates a json string to the latest version by running change sets
	 * @param schemaPath
	 * @param originalJsonString
	 * @return
	 */
	String update(String schemaPath, String originalJsonString) {
		JSON jsonObject = new JsonSlurper().parseText(originalJsonString)
		return update(schemaPath, jsonObject)
	}
	/**
	 * Updates a json object to the latest version by running change sets
	 * @param schemaPath
	 * @param jsonObject
	 * @return
	 */
	String update(String schemaPath, JSON jsonObject){
		String jsonVersion = getJsonVersion(jsonObject)
		String currentVersion = VersionUtils.getSchemaJarVersion()
		if(!currentVersion){
			throw new IllegalStateException("Unable to determine the current version.")
		}

		def changeSetsToApply = getChangeSetInstances(jsonObject, schemaPath, jsonVersion, currentVersion)
		log.info("${changeSetsToApply.values().flatten().size()} Changesets to apply.")

		//Run all the change sets that apply to this json object.
		changeSetsToApply = sortMapByVersionKey(changeSetsToApply)
		changeSetsToApply.each{ String versionKey, List changeSets ->
			changeSets.each { ChangeSet changeSetInstance ->
				log.info("Running ${changeSetInstance.class.simpleName}...")
				changeSetInstance.convert(jsonObject)
			}
		}

		//If the json is now valid, set the current version number.
		String newString = jsonObject.toString()
		if(isValid(schemaPath, newString)){

			jsonObject.version = currentVersion
			newString = jsonObject.toString()
			log.info("Done running changesets.  JSON now valid against current schema.")

		} else {
			log.warn("JSON still does not validate against current schema ${schemaPath}")
		}

		return newString
	}

	/**
	 * Gets change sets that need to be applied to the json object.
	 * If the json object is not currently valid, try to run the changesets.
	 * If the version is specified, find the change sets up to the current one.
	 * If no version specified, then try to run all of the change sets of the same schema.
	 *
	 * @param jsonObect
	 * @param jsonSchemaPath
	 * @param jsonVersion
	 * @param currentSchemaVersion
	 * @return
	 */
	Map getChangeSetInstances(JSON jsonObject, String jsonSchemaPath, String jsonVersion, String currentSchemaVersion){
		def changeSetsToApply = [:]

		if(VersionUtils.isOlder(jsonVersion, currentSchemaVersion) || !isValid(jsonSchemaPath, jsonObject.toString())){
			availableChangeSets.each { Class changeSetClass ->
				ChangeSet changeSetInstance = changeSetClass.newInstance() as ChangeSet

				String changeSetVersion = changeSetInstance.schemaVersion

				//json version > version with changesets <= current schema version
				if(changeSetVersion == currentSchemaVersion ||
						(VersionUtils.isNewer(changeSetVersion, jsonVersion) && VersionUtils.isOlder(changeSetVersion, currentSchemaVersion))){

					if(changeSetInstance.schemaPath == jsonSchemaPath) {
						if(changeSetsToApply.containsKey(changeSetVersion)){
							changeSetsToApply.get(changeSetVersion).add(changeSetInstance)
						} else {
							changeSetsToApply.put(changeSetVersion, [changeSetInstance])
						}
					}
				}
			}
		}
		return changeSetsToApply
	}

	/**
	 * oldest version is the first entry, newest version is the last entry
	 * @param map
	 * @return
	 */
	Map sortMapByVersionKey(Map map){
		return map.sort { entry1, entry2 ->
			if(entry1.key == entry2.key){
				0
			} else if(VersionUtils.isOlder(entry1.key, entry2.key)){
				-1
			} else {
				1
			}
		}
	}

	/**
	 * Is json valid against the schema.
	 * @param schemaPath
	 * @param jsonString
	 * @return
	 */
	boolean isValid(schemaPath, jsonString){
		def report = new Validator().validateAndReport(schemaPath, jsonString)
		report.messages.each{ log.error "validation message: ${it}" }
		return report.isSuccess()
	}

	/**
	 * returns json version if json an object (not array or null), has the version key, and the value is valid
	 * otherwise returns "0"
	 * @param jsonObject
	 * @return
	 */
	String getJsonVersion(jsonObject){
		def jsonVersion = "0"
		if(jsonObject instanceof JSONObject && jsonObject.containsKey("version") && VersionUtils.getMatcher(jsonObject.version).matches()){
			jsonVersion = jsonObject.version
		}
		return jsonVersion
	}

}
