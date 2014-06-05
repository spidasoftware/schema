package com.spidasoftware.schema.changesets
import com.spidasoftware.schema.utils.VersionUtils
import com.spidasoftware.schema.validation.Validator
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
/**
 * Checks json version and runs change sets if needed.
 */
@Log4j
class JsonUpdater {


	List availableChangeSets = [CalcAddressChangeSet, CalcIdChangeSet, GPSAndStructureChangeSet, UserDefinedValueChangeSet]
	List changeSetInstances // call getChangeSetInstances()

	/**
	 * Parses json string, modifies json object, and returns new json string.
	 * @param schemaPath
	 * @param originalJsonString
	 * @return
	 */
	String update(String schemaPath, String originalJsonString) {
		JSONObject jsonObject = JSONObject.fromObject(originalJsonString)
		update(schemaPath, jsonObject)
		return jsonObject.toString()
	}
	/**
	 * Modifies the json object.
	 * @param schemaPath
	 * @param jsonObject
	 * @return
	 */
	void update(String schemaPath, JSONObject jsonObject){
		String jsonVersion = getJsonVersion(jsonObject)
		String currentVersion = getCurrentVersion()

		//Run all the change sets that apply to this json object.
		def changeSetsThatApply = getChangeSetsThatApply(jsonObject, schemaPath, jsonVersion, currentVersion)
		log.info("${changeSetsThatApply.size()} Changesets that apply.")
		changeSetsThatApply.each { ChangeSet changeSetInstance ->
			log.info("Running ${changeSetInstance.class.simpleName}...")
			changeSetInstance.convert(jsonObject)
		}

		//If the json is now valid, set the current version number.
		if(isValid(schemaPath, jsonObject.toString())){
			VersionUtils.addSchemaJarVersion(jsonObject)
			log.info("Done running changesets.  JSON now valid against current schema.")

		} else {
			log.warn("JSON still does not validate against current schema ${schemaPath}")
		}
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
	List getChangeSetsThatApply(JSONObject jsonObject, String jsonSchemaPath, String jsonVersion, String currentSchemaVersion){
		def changeSetsToApply = []

		if(VersionUtils.isOlder(jsonVersion, currentSchemaVersion) || !isValid(jsonSchemaPath, jsonObject.toString())){
			getChangeSetInstances().each { ChangeSet changeSetInstance ->
				String changeSetVersion = changeSetInstance.schemaVersion

				//json version > version with changesets <= current schema version
				if(changeSetInstance.schemaPath == jsonSchemaPath && (changeSetVersion == currentSchemaVersion ||
						(VersionUtils.isNewer(changeSetVersion, jsonVersion) && VersionUtils.isOlder(changeSetVersion, currentSchemaVersion)))){

					changeSetsToApply.add(changeSetInstance)
				}
			}
		}
		return changeSetsToApply
	}

	/**
	 * Creates instances of changeset if they aren't already created
	 * @return
	 */
	List getChangeSetInstances() {
		if(!changeSetInstances){
			changeSetInstances = availableChangeSets.collect { Class changeSet ->

				changeSet.newInstance() as ChangeSet

			}.sort { ChangeSet changeSet1, ChangeSet changeSet2 ->

				if (changeSet1.schemaVersion == changeSet2.schemaVersion) {
					0
				} else if (VersionUtils.isOlder(changeSet1.schemaVersion, changeSet2.schemaVersion)) {
					-1
				} else {
					1
				}
			}
		}
		return changeSetInstances
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
	 * get current version of this schema
	 * @return
	 */
	String getCurrentVersion(){
		String currentVersion = VersionUtils.getSchemaJarVersion()

		if(currentVersion){
			log.debug("Current version \"${currentVersion}\" determined from schema jar file name.")
		} else {
			currentVersion = getChangeSetInstances().last().schemaVersion
			log.debug("Using latest change set version \"${currentVersion}\" since version cannot be determined from schema jar file name.")
		}
		return currentVersion
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
