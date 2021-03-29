/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import com.spidasoftware.schema.utils.StringFormatting
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
/**
 * Custom environments were introduced in version 8.0.
 *
 * When a client data is up converted, pre fill them with our old default environments.
 *
 * When a client data is down converted, check if name when converted to enum form, matches {@link Environment}.
 * If it does match an enum from {@link Environment}, then convert all references in the project to that enum.
 * If it does not match an enum from {@link Environment}, then convert all references in the project to NONE.
 */
class EnvironmentClientDataChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		List<Map> defaultEnvironmentMaps = Environment.values().sort{it.toString()}.collect ({ [name: it.toString(), description: "N/A"] as Map})
		clientDataJSON.put("environments", defaultEnvironmentMaps)
		return true
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		if (clientDataJSON.containsKey("environments")) {
			clientDataJSON.remove("environments")
			return true
		} else {
			return false
		}
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		boolean revertedProjectClientData = false
		projectJSON.remove("clientFileVersion")
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			revertedProjectClientData = revertClientData(clientDataJSON)
			if (revertedProjectClientData) {
				if (clientDataJSON.containsKey("hash")) {
					clientDataJSON.remove("hash")
				}
			}
		}
		projectJSON.get("leads")?.each { Map leadJSON ->
			leadJSON.get("locations")?.each { Map locationJSON ->
				revertLocation(locationJSON, revertedProjectClientData)
			}
		}
	}

	void revertLocation(Map locationJSON, boolean revertedProjectClientData) throws ConversionException {
		locationJSON.get("designs")?.each { Map designJSON ->
			revertDesign(designJSON, revertedProjectClientData)
		}
	}

	@CompileDynamic
	void revertDesign(Map designJSON, boolean revertedProjectClientData) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			revertResults(detailedResultsJSON)
		}
		if (revertedProjectClientData) {
			revertEnvironment(designJSON.structure.pole)
			(designJSON.structure.spanPoints as List<Map>).each { Map spanPoint -> revertEnvironment(spanPoint)}
			(designJSON.structure.wireEndPoints as List<Map>).each { Map wireEndPoint -> revertEnvironment(wireEndPoint)}
		}
	}

	protected void revertEnvironment(Map environmentItem) {
		Environment defaultEnvironment = Environment.values().toList().find{StringFormatting.equalsIgnoreCaseAndEnumFormat(it.name(), environmentItem.environment as String)}
		if (defaultEnvironment != null) {
			environmentItem.put("environment", defaultEnvironment.name())
		} else {
			environmentItem.put("environment", "NONE")
		}
	}
}
