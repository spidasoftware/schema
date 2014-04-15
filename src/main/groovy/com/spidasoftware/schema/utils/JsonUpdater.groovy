package com.spidasoftware.schema.utils

import com.spidasoftware.schema.changesets.CalcAddressChangeSet
import com.spidasoftware.schema.changesets.ChangeSet
import com.spidasoftware.schema.changesets.AmStationIdChangeSet
import com.spidasoftware.schema.validation.Validator
import groovy.util.logging.Log4j
import net.sf.json.JSONObject


/**
 * Checks json version and runs change sets if needed.
 */
@Log4j
class JsonUpdater {

	def versionToChangeSetsMap = [
			"0.6": [CalcAddressChangeSet, AmStationIdChangeSet]
	]

	/**
	 * Updates a json string to the latest version by running changesets
	 * @param schemaPath
	 * @param originalJsonString
	 * @return
	 */
	String update(String schemaPath, String originalJsonString){
		def jsonObject = JSONObject.fromObject(originalJsonString)
		def jsonVersion = jsonObject.version as String
		def changeSetsToApply = [:]
		def currentVersion = getCurrentVersion(getJarFile())

		if(jsonVersion){

			//If the version is specified, find the change sets up to the current one.
			//NOTE: We currently trust that this version number value is correct.
			changeSetsToApply = getChangeSetsToApply(schemaPath, jsonVersion, currentVersion)

		} else if(!isValid(schemaPath, originalJsonString)){

			//If no version specified, then try to run all of the change sets.
			changeSetsToApply = versionToChangeSetsMap
		}

		log.info("${changeSetsToApply.values().flatten().size()} Changesets to apply.")

		//Run all the change sets that apply to this json object.
		changeSetsToApply = sortMapByVersionKey(changeSetsToApply)
		changeSetsToApply.each{ String versionKey, List changeSets ->
			changeSets.each { Class changeSet ->
				ChangeSet changeSetInstance = changeSet.newInstance() as ChangeSet
				log.info("Running ${changeSet.simpleName}...")
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
	 * Gets change sets with the same schema path up to the current version.
	 * @param jsonVersion
	 * @param currentVersion
	 * @return
	 */
	Map getChangeSetsToApply(String schemaPath, String jsonVersion, String currentVersion){
		if(VersionUtils.isOlder(jsonVersion, currentVersion)){
			def changeSetsMap = [:]
			versionToChangeSetsMap.each { versionKey, changeSetClasses ->
				//json version > version with changesets <= current schema version
				if(versionKey == currentVersion || (VersionUtils.isNewer(versionKey, jsonVersion) && VersionUtils.isOlder(versionKey, currentVersion))){

					changeSetClasses.each { changeSetClass ->
						ChangeSet changeSetInstance = changeSetClass.newInstance() as ChangeSet
						if(changeSetInstance.schemaPath == schemaPath) {
							if(changeSetsMap.containsKey(versionKey)){
								changeSetsMap.get(versionKey).add(changeSetClass)
							} else {
								changeSetsMap.put(versionKey, [changeSetClass])
							}
						}
					}
				}
			}
			return changeSetsMap
		}
		return [:]
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
	 * returns the jar file this class is in
	 * @return
	 */
	File getJarFile(){
		def file = new File(this.class.getProtectionDomain().getCodeSource().getLocation().path)
		log.info "jar file = ${file}"
		return file
	}

	/**
	 * Example: build/schema-0.5.1.jar returns 0.5.1
	 * @param jarFile
	 * @return
	 */
	String getCurrentVersion(File jarFile){
		return jarFile.name.toLowerCase().replace(".jar", "").split("-").last()
	}


}
